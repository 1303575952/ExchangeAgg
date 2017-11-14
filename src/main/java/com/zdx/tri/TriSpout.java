package com.zdx.tri;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.zdx.common.FileIO;


import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;

//import backtype.storm.topology.IRichSpout;

public class TriSpout extends BaseRichSpout implements MessageListenerConcurrently{  
	private static final long serialVersionUID = -3085994102089532269L;   
	private SpoutOutputCollector collector;  
	private transient DefaultMQPushConsumer consumer; 
	private static final Logger logger = LoggerFactory.getLogger(TriSpout.class);
	private static final HashMap<String, ArrayList<String>> tickerTripleMap = new HashMap<String, ArrayList<String>>();
	private volatile static HashMap<String, TriArbitrageInfo> tripleInfoMap = new HashMap<String, TriArbitrageInfo>();

	@SuppressWarnings("rawtypes")  
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) { 
		String filePath = "C:\\Users\\zdx\\git\\ExchangeAgg\\conf\\t3.json";

		loadTickerTripleMapFromFile(filePath);
		buildTripleInfoMap();
		logger.info("----------2------------ " + tickerTripleMap.keySet().toString());

		logger.info("init DefaultMQPushConsumer");  
		consumer = new DefaultMQPushConsumer(TriConfig.consumerGroup); 
		consumer.setNamesrvAddr(TriConfig.getRocketMQNameServerAddress());
		consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
		//set to broadcast mode
		consumer.setMessageModel(MessageModel.BROADCASTING);
		//consumer.setConsumeThreadMax(10);
		try {
			consumer.subscribe("ticker", "*");
		} catch (MQClientException e) {  
			e.printStackTrace();
		}  
		consumer.registerMessageListener(this);
		  
		try {  
			consumer.start();  
		} catch (MQClientException e) {  
			e.printStackTrace();  
		} 
		System.out.println("Consumer Started.");  
		logger.info("Consumer Started.");  



		this.collector = collector;  
	}  

	@Override  
	public void nextTuple() {  
		//do nothing  
	}  

	@Override  
	public void declareOutputFields(OutputFieldsDeclarer declarer) {  
		declarer.declare(new Fields("exchangeName"));
	}

	@Override
	public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
			ConsumeConcurrentlyContext context) {  
		for (MessageExt msg : msgs) {
			
			String body = new String(msg.getBody());

			logger.info("Spout Message body = " + body);

			JSONObject jsonObject = JSON.parseObject(body);
			logger.info("Spout Message body = " + jsonObject.isEmpty());
			String exchangeName = jsonObject.getString("exchangeName");
			logger.info("Spout Message body = " + jsonObject.getString("exchangeName"));
			String coinA =  jsonObject.getString("coinA");
			logger.info("Spout Message body = " + jsonObject.getString("coinA"));
			String coinB =  jsonObject.getString("coinB");
			logger.info("Spout Message body = " + jsonObject.getString("coinB"));
			String pair = coinA + "/" + coinB;
			logger.info("Spout Message body = " + pair);
			String key1 = exchangeName + "@@" + pair;
			logger.info("Spout Message body = " + key1);
			double ask = jsonObject.getDoubleValue("ask");
			logger.info("Spout Message body = " + ask);
			double bid = jsonObject.getDoubleValue("bid");
			logger.info("Spout Message body = " + bid);
			ArrayList<String> triList = new ArrayList<String>();
			if (tickerTripleMap.containsKey(key1)){
				triList = tickerTripleMap.get(key1);
				logger.info("Spout Message body = " + triList.toString());
			} else {
				logger.info("Spout Message body = " + "error");				
				logger.info("Spout Message body = " + tickerTripleMap.keySet().toString());
			}


			for (String tri : triList){
				TriArbitrageInfo triInfo = tripleInfoMap.get(tri);
				logger.info("tri = " + tri);
				boolean isValid = false;
				String[] tmp = tri.split("@@");
				if (tmp.length == 3){
					triInfo.groupId = tmp[2];
					logger.info("triInfo.groupId = " + triInfo.groupId);
					String[] tmp2 = tmp[1].split("-");
					if (tmp2.length == 3){
						isValid = true;
						logger.info("tmp2[0] = " + tmp2[0]);
						logger.info("tmp2[1] = " + tmp2[1]);
						logger.info("tmp2[2] = " + tmp2[2]);
						if (pair.equals(tmp2[0])){
							triInfo.ask1 = ask;
							triInfo.bid1 = bid;
						} else if (pair.equals(tmp2[1])){
							triInfo.ask2 = ask;
							triInfo.bid2 = bid;
						} else if (pair.equals(tmp2[2])){
							triInfo.ask3 = ask;
							triInfo.bid3 = bid;
						}

					}
				}
				logger.info("isValid = " + isValid);
				if (isValid){
					logger.info("Spout Message body = " + body);
					logger.info("Spout Message body = " + triInfo.toString());
					triInfo.updateProfitByGroupId();
					logger.info("triInfo.profitVal = " + triInfo.profitVal);
					tripleInfoMap.put(tri, triInfo);
					logger.info("Profit Update tri = " + triInfo.toString());
				}

			}

			//collector.emit(new Values(key1, body));

			//System.out.println("send Coin data ================" + jsonObject.toJSONString());

		}  
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;  
	}

	public static void loadTickerTripleMapFromFile(String path){
		String text = FileIO.ReadFile(path);
		ArrayList<String> ttStringList = JSON.parseObject(text, new TypeReference<ArrayList<String>>(){});

		for (String e : ttStringList){

			e = e.toLowerCase();
			logger.info("-----------3----------- " + e);
			JSONObject jsonObj = JSON.parseObject(e);

			String tickerName = String.valueOf(jsonObj.get("tickerpair"));
			logger.info("-----------4----------- " + tickerName);
			String tmp = String.valueOf(jsonObj.get("trilist"));
			logger.info("-----------5----------- " + tmp);
			if (tmp.contains("[")){
				tmp = tmp.replaceAll("\\[", "");
			}
			if (tmp.contains("]")){
				tmp = tmp.replaceAll("\\]", "");
			}
			if (tmp.contains("\"")){
				tmp = tmp.replaceAll("\"", "");
			}
			String[] tmp2 = tmp.split(",");
			ArrayList<String> trList = new ArrayList<String>();
			for(String tmp3 : tmp2){
				logger.info("-----------6----------- " + tmp3);
				trList.add(tmp3);
			}

			tickerTripleMap.put(tickerName, trList);
		}
		logger.info("-----------1----------- " + tickerTripleMap.keySet().toString());
	}

	public void buildTripleInfoMap(){
		for ( Entry<String, ArrayList<String>> e : tickerTripleMap.entrySet()){
			ArrayList<String> val1 = e.getValue();
			for (String s : val1){							
				TriArbitrageInfo m = new TriArbitrageInfo();
				tripleInfoMap.put(s, m);
			}

		}
		logger.info("-----------1----------- " + tickerTripleMap.keySet().toString());
	}
}  