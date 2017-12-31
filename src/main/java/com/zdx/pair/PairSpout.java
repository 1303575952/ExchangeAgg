package com.zdx.pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.zdx.common.DataFormat;
import com.zdx.common.TickerStandardFormat;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;

public class PairSpout extends BaseRichSpout implements MessageListenerConcurrently{  
	private static final long serialVersionUID = -3085994102089532269L;   
	private SpoutOutputCollector collector;  
	private transient DefaultMQPushConsumer consumer; 
	private static final Logger logger = LoggerFactory.getLogger(PairSpout.class);


	//public static PairStormConf PairSpoutConf;
	public static HashMap<String, LowestPrice> pairPriceMap = new HashMap<String, LowestPrice> ();

	public static InfluxDB influxDB = null;


	@SuppressWarnings("rawtypes") 
	@Override
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) { 
		logger.debug("===========================PairSpout prepare Begin=======================================");
		logger.debug("========================== Conf=" + conf.get("SpoutData").toString());
		
		PairSpoutConf.buildSpoutConfig(conf.get("SpoutData").toString());

		influxDB = InfluxDBFactory.connect(PairSpoutConf.influxURL);
		if (!influxDB.databaseExists(PairSpoutConf.influxDbName)){
			logger.debug("==================================================================Database" + PairSpoutConf.influxDbName + " not Exist");
			influxDB.createDatabase(PairSpoutConf.influxDbName);
		}
		influxDB.setDatabase(PairSpoutConf.influxDbName);
		influxDB.createRetentionPolicy(PairSpoutConf.influxRpName, PairSpoutConf.influxDbName, "30d", "30m", 2, true);




		consumer = new DefaultMQPushConsumer(PairSpoutConf.consumerGroup); 
		consumer.setNamesrvAddr(PairSpoutConf.mqAddress);
		consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
		consumer.setMessageModel(MessageModel.BROADCASTING);
		consumer.setConsumeThreadMin(1);
		consumer.setConsumeThreadMax(1);
		consumer.registerMessageListener(this);

		for (int i = 0; i < PairSpoutConf.topicList.size(); i++){
			try {
				consumer.subscribe("ticker_" + PairSpoutConf.topicList.get(i).toLowerCase(), "*");
			} catch (MQClientException e) {  
				e.printStackTrace();  
			}  
		}

		try {  
			consumer.start();  
		} catch (MQClientException e) {  
			e.printStackTrace();  
		} 
		logger.debug("Consumer Started.");

		this.collector = collector; 
		logger.debug("===========================PairSpout prepare End=======================================");
	}  

	@Override  
	public void nextTuple() {  
		//do nothing  
	}


	@Override  
	public void declareOutputFields(OutputFieldsDeclarer declarer) {  
		//declarer.declare(new Fields("pairArbitrage"));
	}

	@Override
	public ConsumeConcurrentlyStatus  consumeMessage(List<MessageExt> msgs,
			ConsumeConcurrentlyContext context) {  
		logger.info("===========================PairSpout Begin=======================================");
		for (MessageExt msg : msgs) {
			String body = new String(msg.getBody());		
			TickerStandardFormat tsf = new TickerStandardFormat();
			tsf.formatJsonString(body);
			logger.debug("message tsf format = " + tsf.toJsonString());
			updatePrice(tsf);
			updateForwardPairPrice(tsf);
		}
		logger.info("===========================PairSpout End=======================================");
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;  
	}

	public void updateForwardPairPrice(TickerStandardFormat tsf){
		logger.debug("updateForwardPairPrice: ... ...");
		String pair = tsf.coinA.toLowerCase() + "_" + tsf.coinB.toLowerCase();
		String exchangePairName = tsf.exchangeName.toLowerCase() + "_" + pair;
		logger.debug("updateForwardPairPrice: exchangePairName = " + exchangePairName);
		logger.debug("updateForwardPairPrice: keyset of pc.pairFourthMap = " + PairSpoutConf.pairFourthMap.keySet().toString());
		//logger.debug("updateForwardPairPrice: Ticker Data = "+ tsf.toJsonString());
		if (PairSpoutConf.pairFourthMap.containsKey(exchangePairName)){			
			ArrayList<String> tmp1 = PairSpoutConf.pairFourthMap.get(exchangePairName);
			logger.debug("updateForwardPairPrice: Candidate FourthList = "+ tmp1.toString());
			for (String x : tmp1){
				logger.debug("updateForwardPairPrice: Candidate Fourth =  "+ x);
				if (PairSpoutConf.fourthPriceMap.containsKey(x)){
					EnterPrice ep = PairSpoutConf.fourthPriceMap.get(x);

					logger.debug("updateForwardPairPrice: Candidate Fourth profit before update = "+ ep.toJsonString());					
					if (ep.sellExchangeName.equals(tsf.exchangeName.toLowerCase())){
						ep.bid1 = tsf.bid;
						ep.ask1 = tsf.ask;
						ep.sellPath = pair;
					} else if (ep.buyExchangeName.equals(tsf.exchangeName.toLowerCase())){
						ep.bid2 = tsf.bid;
						ep.ask2 = tsf.ask;
						ep.buyPath = pair;
					}
					if (ep.ask2 > 0.0 && ep.ask1 > 0.0){
						ep.priceDiff = ep.bid1 / ep.ask2 - 1.0;
					}
					if (ep.priceDiff > ep.maxPriceDiff){
						ep.maxPriceDiff = ep.priceDiff;
					}

					//if (ep.priceDiff > threshold ){
					//	ep.isSend = true;
					//}
					ep.isSend = true;
					if (ep.isSend){	
						/*						if (ep.priceDiff < thresholdNeglect ){
							ep.isSend = false;
						}*/
						String[] t1 = x.split("@@");
						logger.info("-----Sell at = " + t1[0]);
						logger.info("-----Buy at = " + t1[1]);
						logger.info("-----Price Diff = " + ep.priceDiff);
						ep.timeStamp = System.currentTimeMillis();
						logPriceDiff(ep);
					}
					logger.debug("updateForwardPairPrice: Candidate Fourth profit after update = "+ ep.toJsonString());
					PairSpoutConf.fourthPriceMap.put(x, ep);
				}

			}
		}
	}

	public void logPriceDiff(EnterPrice ep){
		int status = 0;
		if (ep.priceDiff > 0.2){
			//介入交易
			status = 1;
			ep.tradeFlag = "open";
		}  else	if (ep.priceDiff < 0.1){
			//退出交易
			status = -1;
			ep.tradeFlag = "close";
		}

		String tableName = DataFormat.removeShortTerm(ep.sellExchangeName) + "_" + DataFormat.removeShortTerm(ep.buyExchangeName);
		Point point1 = Point.measurement(tableName)
				.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
				.addField("sellExchangeName", ep.sellExchangeName)	
				.tag("sellPath", ep.sellPath)
				.addField("sellPrice", ep.bid1)
				.addField("buyExchangeName", ep.buyExchangeName)
				.tag("buyPath", ep.buyPath)
				.addField("buyPrice", ep.ask2)
				.addField("priceDiff", ep.priceDiff)
				.addField("status", status)
				.build();
		influxDB.write(PairSpoutConf.influxDbName, PairSpoutConf.influxRpName, point1);
		Query query = new Query("SELECT * FROM " + tableName + " GROUP BY *", PairSpoutConf.influxDbName);
		QueryResult result = influxDB.query(query);
		if (result.getResults().get(0).getSeries().get(0).getTags().isEmpty() == true){
			logger.debug("===========================InfluxDB Insert Failed=======================================");
			influxDB.close();
			influxDB = InfluxDBFactory.connect(PairSpoutConf.influxURL);
		} else {
			logger.debug("===========================InfluxDB Insert Sucess=======================================");
		}
	}


	public void updatePrice(TickerStandardFormat tsf){
		String pair = tsf.coinA.toLowerCase() + "_" + tsf.coinB.toLowerCase();
		logger.debug("updatePrice: pair = " + pair);
		String exchangePairName = tsf.exchangeName.toLowerCase() + "_" + pair;
		logger.debug("updatePrice: exchangePairName = " + exchangePairName);
		//用ticker直接值更新最低价，如BTC-USD，给BTC-USD自身pair用
		updateLowestPrice(exchangePairName, pair, tsf.ask);
		logger.debug("updatePrice: Direct LowestPrice = " + tsf.ask);
		logger.debug("updatePrice: Indirect ... keySet of pairPathMap = " + PairSpoutConf.pairPathMap.keySet().toString()); 

		//更新x-btc-usd间接价格，给x_usd用
		if (PairSpoutConf.pairPathMap.containsKey(exchangePairName)){
			ArrayList<String> pathNameList = PairSpoutConf.pairPathMap.get(exchangePairName);
			logger.debug("updatePrice: Candidate Indirect Paths = " + pathNameList.toString());
			for (String pathName : pathNameList){
				logger.debug("updatePrice: Current Candidate Indirect Path = " + pathName);
				PathPrice pp =  PairSpoutConf.pathPriceMap.get(pathName);
				logger.debug("updatePrice: Current Candidate Indirect PathPrice Before Update = " + pp.toJsonString());
				if (pair.equals(pp.path1)){
					pp.bid1 = tsf.bid;
					pp.ask1 = tsf.ask;
				}
				if (pair.equals(pp.path2)){
					pp.bid2 = tsf.bid;
					pp.ask2 = tsf.ask;
				}
				logger.debug("updatePrice: Current Candidate Indirect PathPrice After Update = " + pp.toJsonString());
				pp.price = (1 + pp.fee1) * pp.ask1 * (1 + pp.fee2) * pp.ask2;
				logger.debug("updatePrice: Indirect Price of Path = " + pathName + " = " + pp.price);

				PairSpoutConf.pathPriceMap.put(pathName, pp);
				//用间接价格更新最低价，如etc-usd
				String exchangePairName2 = tsf.exchangeName.toLowerCase() + "_" + pp.pair;
				logger.debug("updatePrice: updateLowestPrice with inDirect Price from Path = " + pathName + " with price = " + pp.price);
				updateLowestPrice(exchangePairName2, pp.pair, pp.price);

			}
		}
	}

	public static void updateLowestPrice(String tickerName, String lowestPath, double lowestPrice){
		logger.debug("updateLowestPrice: KeySet of pairPriceMap = " + pairPriceMap.keySet().toString());
		logger.debug("updateLowestPrice: Input Data tickerName = " + tickerName);
		logger.debug("updateLowestPrice: Input Data lowestPath = " + lowestPath);
		logger.debug("updateLowestPrice: Input Data lowestPrice = " + lowestPrice);
		if (pairPriceMap.containsKey(tickerName)){
			LowestPrice lp = pairPriceMap.get(tickerName);
			logger.debug("updateLowestPrice: Before Update lowestPath = " + lp.lowestPath);
			logger.debug("updateLowestPrice: Before Update lowestPrice = " + lp.lowestPrice);
			if (lowestPrice < lp.lowestPrice){
				lp.lowestPath = lowestPath;
				lp.lowestPrice = lowestPrice;
			}
			pairPriceMap.put(tickerName, lp);
		} else {
			logger.debug("updateLowestPrice: Before Update lowestPath = " + "NULL");
			logger.debug("updateLowestPrice: Before Update lowestPrice = " + "NULL");
			LowestPrice lp = new LowestPrice();
			lp.lowestPath = lowestPath;
			lp.lowestPrice = lowestPrice;
			pairPriceMap.put(tickerName, lp);			
		}
	}





}  