package com.zdx.pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

public class PairSpout extends BaseRichSpout implements MessageListenerOrderly{  
	private static final long serialVersionUID = -3085994102089532269L;   
	private SpoutOutputCollector collector;  
	private transient DefaultMQPushConsumer consumer; 
	private static final Logger logger = LoggerFactory.getLogger(PairSpout.class);
    
	public double threshold = 0.05;
	public PariConfig pc;
	
	@SuppressWarnings("rawtypes")  
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) { 
		threshold = Double.parseDouble((String) conf.get("PairArbitrageThreshold"));
		String filePath1 = (String) conf.get("TopVol100MPath");
		String filePath2 = (String) conf.get("TopVol100MPairPath");
		pc = new PariConfig();
		pc.initPairConfig(filePath1, filePath2);
		
		
		logger.info("init DefaultMQPushConsumer");
		logger.info("###"+(String) conf.get("ConsumerGroup"));
		
		consumer = new DefaultMQPushConsumer((String) conf.get("ConsumerGroup")); 
		consumer.setNamesrvAddr((String) conf.get("RocketMQNameServerAddress"));
		consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
		//consumer.setMessageModel(MessageModel.BROADCASTING);
		try {
			consumer.subscribe("toyTickerTest", "*");
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
		declarer.declare(new Fields("tickerType"));
	}

	@Override
	public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs,
			ConsumeOrderlyContext context) {  
		for (MessageExt msg : msgs) {
			
			String body = new String(msg.getBody());
			/*
			 * 接收到每一个ticker
			 */
			System.out.println("-----------1" + body);
			TickerStandardFormat tsf = new TickerStandardFormat();
			tsf.formatJsonString(body);
			updatePrice(tsf);
			updatePairPrice(tsf);
		}  
		return ConsumeOrderlyStatus.SUCCESS;  
	}
	
	public void updatePairPrice(TickerStandardFormat tsf){
		logger.info("updatePairPrice");
		
		String pair = tsf.coinA.toLowerCase() + "_" + tsf.coinB.toLowerCase();
		String exchangePairName = tsf.exchangeName.toLowerCase() + "_" + pair;
		logger.info(exchangePairName);
		logger.info("pc.pairFourthMap"+pc.pairFourthMap.size());
		if (pc.pairFourthMap.containsKey(exchangePairName)){
			logger.info("pc.pairFourthMap.containsKey(exchangePairName)"+exchangePairName);
			ArrayList<String> tmp1 = pc.pairFourthMap.get(exchangePairName);
			for (String x : tmp1){
				if (pc.fourthPriceMap.containsKey(x)){
					EnterPrice ep = pc.fourthPriceMap.get(x);
					if (ep.exchangeName1.equals(tsf.exchangeName.toLowerCase())){
						ep.bid1 = tsf.bid;
						ep.ask1 = tsf.ask;
					} else if (ep.exchangeName2.equals(tsf.exchangeName.toLowerCase())){
						ep.bid2 = tsf.bid;
						ep.ask2 = tsf.ask;
					}
					if (ep.ask2 > 0.0){
						ep.profit = ep.bid1 / ep.ask2;
					}
					if (ep.profit > 1 + threshold ){
						
						System.out.println("    ---2-" + ep.profit);
						System.out.println("----------------------1----------------------");
						System.out.println("----------------------1----------------------");
						System.out.println("----------------------1----------------------");
						String[] t1 = x.split("@@");
						System.out.println("    fourthTuple = " + x);
						System.out.println("    profit = " + ep.profit);
						System.out.println("----------------------2----------------------");
						System.out.println("----------------------2----------------------");
						System.out.println("----------------------2----------------------");
						
						LowestPricePair lpp = getPairResetInfo(t1[0], t1[1]);
						System.out.println("    ---3-" + lpp.resetFee);
					}
					pc.fourthPriceMap.put(x, ep);
				}

			}
		}
	}
	//****************************************************************************************
	//public double threshold = 0.05;
	public static HashMap<String, LowestPrice> pairPriceMap = new HashMap<String, LowestPrice> ();
	//PariConfig pc = new PariConfig();
	public void updatePrice(TickerStandardFormat tsf){
		String pair = tsf.coinA.toLowerCase() + "_" + tsf.coinB.toLowerCase();
		//System.out.println("---1-" + pair);
		String exchangePairName = tsf.exchangeName.toLowerCase() + "_" + pair;
		//System.out.println("---2-" + exchangePairName);
		//用ticker直接值更新最低价，如BTC-USD
		updateLowestPrice(exchangePairName, pair, tsf.ask);
		if (pc.pairPathMap.containsKey(exchangePairName)){
			ArrayList<String> pathNameList = pc.pairPathMap.get(exchangePairName);
			//System.out.println("---9-" + pathNameList.toString());
			for (String pathName : pathNameList){
				//System.out.println("---10-" + pathName);
				PathPrice pp =  pc.pathPriceMap.get(pathName);
				if (pair.equals(pp.path1)){
					pp.bid1 = tsf.bid;
					pp.ask1 = tsf.ask;
				}
				if (pair.equals(pp.path2)){
					pp.bid2 = tsf.bid;
					pp.ask2 = tsf.ask;
				}
				//System.out.println("---11-" + pp.ask1);
				//System.out.println("---12-" + pp.ask2);
				pp.price = (1 + pp.fee1) * pp.ask1 * (1 + pp.fee2) * pp.ask2;				
				//更新etc-eth-usd间接价格
				pc.pathPriceMap.put(pathName, pp);
				//用间接价格更新最低价，如etc-usd
				String exchangePairName2 = tsf.exchangeName.toLowerCase() + "_" + pp.pair;
				updateLowestPrice(exchangePairName2, pp.pair, pp.price);
			}
		}
	}
	
	public static void updateLowestPrice(String tickerName, String lowestPath, double lowestPrice){
		if (pairPriceMap.containsKey(tickerName)){
			LowestPrice lp = pairPriceMap.get(tickerName);
			if (lowestPrice < lp.lowestPrice){
				lp.lowestPath = lowestPath;
				lp.lowestPrice = lowestPrice;
				//System.out.println("    ---3-" + lp.lowestPath);
				//System.out.println("    ---4-" + lp.lowestPrice);
				//System.out.println("    ---5-" + tickerName);
			}
			pairPriceMap.put(tickerName, lp);
		} else {
			LowestPrice lp = new LowestPrice();
			lp.lowestPath = lowestPath;
			lp.lowestPrice = lowestPrice;
			//System.out.println("    ---6-" + lp.lowestPath);
			//System.out.println("    ---7-" + lp.lowestPrice);
			pairPriceMap.put(tickerName, lp);
			//System.out.println("    ---8-" + tickerName);			
		}
	}

	public static LowestPricePair getPairResetInfo(String exchangeTicker1, String exchangeTicker2){
		LowestPricePair lpp = new LowestPricePair();		
		LowestPrice lp1 = new LowestPrice();
		LowestPrice lp2 = new LowestPrice();
		logger.info("exchangeTicker1"+exchangeTicker1);
		logger.info("exchangeTicker2"+exchangeTicker2);
		logger.info("pairPriceMap.containsKey(exchangeTicker1):"+pairPriceMap.containsKey(exchangeTicker1));
		logger.info("pairPriceMap.containsKey(exchangeTicker2):"+pairPriceMap.containsKey(exchangeTicker2));
		logger.info("pairPriceMap.size()"+pairPriceMap.size());
		for (Entry<String, LowestPrice> entry : pairPriceMap.entrySet()) {
			logger.info("Key: " + entry.getKey() + " Value: " + entry.getValue().lowestPath+"\t"+entry.getValue().lowestPrice);
		}
		if (pairPriceMap.containsKey(exchangeTicker1) && pairPriceMap.containsKey(exchangeTicker2)){
			lp1 = pairPriceMap.get(exchangeTicker1);
			lp2 = pairPriceMap.get(exchangeTicker2);
		}
		
		lpp.lowestPath1 = lp1.lowestPath;
		lpp.lowestPrice1 = lp1.lowestPrice;
		lpp.lowestPath2 = lp2.lowestPath;
		lpp.lowestPrice2 = lp2.lowestPrice;
		logger.info("lpp.lowestPath1:"+lpp.lowestPath1+
				"\nlpp.lowestPrice1:"+lpp.lowestPrice1+
				"\nlpp.lowestPath2:"+lpp.lowestPath2+
				"\nlpp.lowestPrice2:"+lpp.lowestPrice2);
		if (lpp.lowestPrice2 < Integer.MAX_VALUE){
			lpp.resetFee = lpp.lowestPrice1 / lpp.lowestPrice2 - 1;
		}
		return lpp;
	}
}  