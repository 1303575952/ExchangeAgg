package com.zdx.tri;

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
import backtype.storm.tuple.Values;

public class TriSpout extends BaseRichSpout implements MessageListenerOrderly{  
	private static final long serialVersionUID = -3085994102089532269L;   
	private SpoutOutputCollector collector;  
	private transient DefaultMQPushConsumer consumer; 
	private static final Logger logger = LoggerFactory.getLogger(TriSpout.class);
	private static final HashMap<String, ArrayList<String>> TICKER_TRIPLE_MAP = new HashMap<String, ArrayList<String>>();
	private static final HashMap<String, TriArbitrageInfo> TRIPLE_INFO_MAP = new HashMap<String, TriArbitrageInfo>();

	private static double threshold = 0.01;

	@SuppressWarnings("rawtypes")  
	@Override
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		String filePath = (String) conf.get("TripleInfoPath");
		
		threshold = Double.parseDouble((String) conf.get("TriArbitrageThreshold"));
		
		loadTickerTripleMapFromFile(filePath);
		buildTripleInfoMap();		

		logger.debug("init DefaultMQPushConsumer");  
		consumer = new DefaultMQPushConsumer((String) conf.get("ConsumerGroup")); 
		consumer.setNamesrvAddr((String) conf.get("RocketMQNameServerAddress"));
		consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
		//set to broadcast mode
		consumer.setMessageModel(MessageModel.BROADCASTING);
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
		logger.debug("Consumer Started.");  
		this.collector = collector;  
	}  

	@Override  
	public void nextTuple() {  
		//do nothing  
	}  

	@Override  
	public void declareOutputFields(OutputFieldsDeclarer declarer) {  
		declarer.declare(new Fields("triName"));
	}

	@Override
	public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs,
			ConsumeOrderlyContext context) {  
		for (MessageExt msg : msgs) {
			String body = new String(msg.getBody());
			logger.debug("Spout Message body = " + body);
			JSONObject jsonObject = JSON.parseObject(body);
			//logger.debug("1 = " + jsonObject.isEmpty());
			String exchangeName = jsonObject.getString("exchangeName");
			//logger.debug("2 = " + jsonObject.getString("exchangeName"));
			String coinA =  jsonObject.getString("coinA");
			//logger.debug("3 = " + jsonObject.getString("coinA"));
			String coinB =  jsonObject.getString("coinB");
			//logger.debug("4 = " + jsonObject.getString("coinB"));
			String pair = coinA + "/" + coinB;
			//logger.debug("5 = " + pair);
			String key1 = exchangeName + "@@" + pair;
			//logger.debug("6 = " + key1);
			double ask = jsonObject.getDoubleValue("ask");
			//logger.debug("7 = " + ask);
			double bid = jsonObject.getDoubleValue("bid");
			//logger.debug("8 = " + bid);
			ArrayList<String> triList = new ArrayList<String>();
			if (TICKER_TRIPLE_MAP.containsKey(key1)){
				triList = TICKER_TRIPLE_MAP.get(key1);
				//logger.debug("9 = " + triList.toString());
			} else {
				//logger.debug("10 = " + "error");				
				//logger.debug("11 = " + tickerTripleMap.keySet().toString());
			}


			for (String tri : triList){
				TriArbitrageInfo triInfo = TRIPLE_INFO_MAP.get(tri);
				//logger.debug("12 = " + tri);
				boolean isValid = false;
				String[] tmp = tri.split("@@");
				if (tmp.length == 3){
					triInfo.groupId = tmp[2];
					//logger.debug("13 = " + triInfo.groupId);
					String[] tmp2 = tmp[1].split("-");
					if (tmp2.length == 3){
						isValid = true;
						//logger.debug("tmp2[0] = " + tmp2[0]);
						//logger.debug("tmp2[1] = " + tmp2[1]);
						//logger.debug("tmp2[2] = " + tmp2[2]);
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
				//logger.debug("isValid = " + isValid);
				if (isValid){
					//logger.debug("14 = " + body);
					//logger.debug("15 = " + triInfo.toString());
					triInfo.updateProfitByGroupId();
					if (triInfo.profitVal - 1 >= threshold){
						collector.emit(new Values(tri, triInfo.toString()));
					}
					//logger.debug("16 = " + triInfo.profitVal);
					TRIPLE_INFO_MAP.put(tri, triInfo);
					//logger.debug("17 = " + triInfo.toString());
				}

			}

		}  
		return ConsumeOrderlyStatus.SUCCESS;  
	}

	public static void loadTickerTripleMapFromFile(String path){
		String text = FileIO.readFile(path);
		ArrayList<String> ttStringList = JSON.parseObject(text, new TypeReference<ArrayList<String>>(){});

		for (String e : ttStringList){

			e = e.toLowerCase();
			//logger.debug("-----------3----------- " + e);
			JSONObject jsonObj = JSON.parseObject(e);

			String tickerName = String.valueOf(jsonObj.get("tickerpair"));
			//logger.debug("-----------4----------- " + tickerName);
			String tmp = String.valueOf(jsonObj.get("trilist"));
			//logger.debug("-----------5----------- " + tmp);
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
				//logger.debug("-----------6----------- " + tmp3);
				trList.add(tmp3);
			}

			TICKER_TRIPLE_MAP.put(tickerName, trList);
		}
		//logger.debug("-----------1----------- " + tickerTripleMap.keySet().toString());
	}

	public void buildTripleInfoMap(){
		for ( Entry<String, ArrayList<String>> e : TICKER_TRIPLE_MAP.entrySet()){
			ArrayList<String> val1 = e.getValue();
			for (String s : val1){							
				TriArbitrageInfo m = new TriArbitrageInfo();
				TRIPLE_INFO_MAP.put(s, m);
			}

		}
		//logger.debug("-----------1----------- " + tickerTripleMap.keySet().toString());
	}
}  