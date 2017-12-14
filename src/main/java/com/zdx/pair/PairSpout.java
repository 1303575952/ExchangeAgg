package com.zdx.pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.zdx.common.TickerStandardFormat;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public class PairSpout extends BaseRichSpout implements MessageListenerOrderly{  
	private static final long serialVersionUID = -3085994102089532269L;   
	private SpoutOutputCollector collector;  
	private transient DefaultMQPushConsumer consumer; 
	private static final Logger logger = LoggerFactory.getLogger(PairSpout.class);

	public double threshold = 0.0;
	public double thresholdNeglect = 0.01;
	public PariConfig pc;
	public static HashMap<String, LowestPrice> pairPriceMap = new HashMap<String, LowestPrice> ();
	public String consumerTopic = "";
	
	@SuppressWarnings("rawtypes") 
	@Override
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) { 
		threshold = Double.parseDouble((String) conf.get("PairArbitrageThreshold"));
		String filePath1 = (String) conf.get("TopVol100MPath");
		String filePath2 = (String) conf.get("TopVol100MPairPath");
		consumerTopic = (String) conf.get("consumerTopic");
		pc = new PariConfig();
		pc.initPairConfig(filePath1, filePath2);


		logger.info("init DefaultMQPushConsumer");
		logger.info("###"+(String) conf.get("ConsumerGroup"));

		consumer = new DefaultMQPushConsumer((String) conf.get("ConsumerGroup")); 
		consumer.setNamesrvAddr((String) conf.get("RocketMQNameServerAddress"));
		consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
		//consumer.setMessageModel(MessageModel.BROADCASTING);
		try {
			consumer.subscribe(consumerTopic, "*");
		} catch (MQClientException e) {  
			e.printStackTrace();  
		}  
		consumer.registerMessageListener(this);

		try {  
			consumer.start();  
		} catch (MQClientException e) {  
			e.printStackTrace();  
		} 
		logger.info("Consumer Started.");

		this.collector = collector;  
	}  

	@Override  
	public void nextTuple() {  
		//do nothing  
	}  

	@Override  
	public void declareOutputFields(OutputFieldsDeclarer declarer) {  
		declarer.declare(new Fields("pairArbitrage"));
	}

	@Override
	public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs,
			ConsumeOrderlyContext context) {  
		logger.debug("===========================PairSpout Begin=======================================");
		for (MessageExt msg : msgs) {
			String body = new String(msg.getBody());		
			TickerStandardFormat tsf = new TickerStandardFormat();
			tsf.formatJsonString(body);
			logger.debug("message tsk format = " + tsf.toJsonString());
			updatePrice(tsf);
			updateForwardPairPrice(tsf);
		}
		logger.debug("===========================PairSpout End=======================================");
		return ConsumeOrderlyStatus.SUCCESS;  
	}

	public void updateForwardPairPrice(TickerStandardFormat tsf){
		logger.debug("updateForwardPairPrice: ... ...");
		String pair = tsf.coinA.toLowerCase() + "_" + tsf.coinB.toLowerCase();
		String exchangePairName = tsf.exchangeName.toLowerCase() + "_" + pair;
		logger.debug("updateForwardPairPrice: exchangePairName = " + exchangePairName);
		logger.debug("updateForwardPairPrice: keyset of pc.pairFourthMap = " + pc.pairFourthMap.keySet().toString());
		if (pc.pairFourthMap.containsKey(exchangePairName)){			
			ArrayList<String> tmp1 = pc.pairFourthMap.get(exchangePairName);
			logger.debug("updateForwardPairPrice: Candidate FourthList = "+ tmp1.toString());
			for (String x : tmp1){
				logger.debug("updateForwardPairPrice: Candidate Fourth =  "+ x);
				if (pc.fourthPriceMap.containsKey(x)){
					EnterPrice ep = pc.fourthPriceMap.get(x);
					logger.debug("updateForwardPairPrice: Ticker Data = "+ tsf.toJsonString());
					logger.debug("updateForwardPairPrice: Candidate Fourth profit before update = "+ ep.toJsonString());					
					if (ep.sellExchangeName.equals(tsf.exchangeName.toLowerCase())){
						ep.bid1 = tsf.bid;
						ep.ask1 = tsf.ask;
					} else if (ep.buyExchangeName.equals(tsf.exchangeName.toLowerCase())){
						ep.bid2 = tsf.bid;
						ep.ask2 = tsf.ask;
					}
					if (ep.ask2 > 0.0){
						ep.priceDiff = ep.bid1 / ep.ask2 - 1.0;
					}
					if (ep.priceDiff > ep.maxPriceDiff){
						ep.maxPriceDiff = ep.priceDiff;
					}

					if (ep.priceDiff > threshold ){
						ep.isSend = true;
					}
					if (ep.isSend){	
						if (ep.priceDiff < thresholdNeglect ){
							//ep.isSend = false;
						}
						String[] t1 = x.split("@@");
						logger.debug("-----Sell at = " + t1[0]);
						logger.debug("-----Buy at = " + t1[1]);
						logger.debug("-----Price Diff = " + ep.priceDiff);
						ep.timeStamp = System.currentTimeMillis();
						collector.emit(new Values(x, ep.toJsonString()));
					}
					logger.debug("updateForwardPairPrice: Candidate Fourth profit after update = "+ ep.toJsonString());
					pc.fourthPriceMap.put(x, ep);
				}

			}
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
		logger.debug("updatePrice: Indirect ... keySet of pairPathMap = " + pc.pairPathMap.keySet().toString()); 

		//更新x-btc-usd间接价格，给x_usd用
		if (pc.pairPathMap.containsKey(exchangePairName)){
			ArrayList<String> pathNameList = pc.pairPathMap.get(exchangePairName);
			logger.debug("updatePrice: Candidate Indirect Paths = " + pathNameList.toString());
			for (String pathName : pathNameList){
				logger.debug("updatePrice: Current Candidate Indirect Path = " + pathName);
				PathPrice pp =  pc.pathPriceMap.get(pathName);
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

				pc.pathPriceMap.put(pathName, pp);
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