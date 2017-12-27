package com.zdx.tri;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.zdx.common.DataFormat;
import com.zdx.common.FileIO;
import com.zdx.common.TickerStandardFormat;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public class TriSpout extends BaseRichSpout implements MessageListenerConcurrently{  
	private static final long serialVersionUID = -3085994102089532269L;   
	private SpoutOutputCollector collector;  
	private transient DefaultMQPushConsumer consumer; 
	private static final Logger logger = LoggerFactory.getLogger(TriSpout.class);
	private static final HashMap<String, ArrayList<String>> TICKER_TRIPLE_MAP = new HashMap<String, ArrayList<String>>();
	private static final HashMap<String, TriArbitrageInfo> TRIPLE_INFO_MAP = new HashMap<String, TriArbitrageInfo>();
	public String topicList = "";

	public static InfluxDB influxDB = null;
	public static String influxURL = "";
	public static String influxDbName = "";
	public static String influxRpName = "";

	private static double threshold = 0.01;

	@SuppressWarnings("rawtypes")  
	@Override
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		logger.debug("===========================TriSpout prepare Begin=======================================");
		String filePath = (String) conf.get("TripleInfoPath");

		threshold = Double.parseDouble((String) conf.get("TriArbitrageThreshold"));

		loadTickerTripleMapFromFile(filePath);
		buildTripleInfoMap();

		influxURL = (String) conf.get("InfluxDBURL");
		influxDbName = (String) conf.get("InfluxDbName");
		influxRpName = (String) conf.get("InfluxRpName");
		influxDB = InfluxDBFactory.connect(influxURL);
		if (!influxDB.databaseExists(influxDbName)){
			logger.debug("==================================================================Database" + influxDbName + " not Exist");
			influxDB.createDatabase(influxDbName);
		}
		influxDB.setDatabase(influxDbName);
		influxDB.createRetentionPolicy(influxRpName, influxDbName, "30d", "30m", 2, true);



		consumer = new DefaultMQPushConsumer((String) conf.get("ConsumerGroup")); 
		consumer.setNamesrvAddr((String) conf.get("RocketMQNameServerAddress"));
		consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
		//set to broadcast mode
		consumer.setMessageModel(MessageModel.BROADCASTING);
		consumer.registerMessageListener(this);
		consumer.setConsumeThreadMin(1);
		consumer.setConsumeThreadMax(1);
		topicList = (String) conf.get("topicList");
		String[] topics = topicList.split(";");
		for (int i = 0; i < topics.length; i++){
			String topic = topics[i];
			try {
				consumer.subscribe("ticker_" + topic.toLowerCase(), "*");
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
		logger.debug("===========================TriSpout prepare End=======================================");
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
	public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
			ConsumeConcurrentlyContext context) {  
		logger.debug("===========================TriSpout Begin=======================================");
		for (MessageExt msg : msgs) {
			String body = new String(msg.getBody());		
			TickerStandardFormat tsf = new TickerStandardFormat();
			tsf.formatJsonString(body);
			logger.debug("message tsf format = " + tsf.toJsonString());
			String pair = tsf.coinA + "/" + tsf.coinB;
			String key1 = tsf.exchangeName + "@@" + pair;
			pair = pair.toLowerCase();
			key1 = key1.toLowerCase();
			ArrayList<String> triList = new ArrayList<String>();
			if (TICKER_TRIPLE_MAP.containsKey(key1)){
				triList = TICKER_TRIPLE_MAP.get(key1);

			} else {
				//logger.debug("10 = " + "error");				
				//logger.debug("11 = " + tickerTripleMap.keySet().toString());
			}
			logger.debug("======Key = " + key1);
			logger.debug("======Value = " + triList.toString());
			String path1 = "";
			String path2 = "";
			String path3 = "";

			for (String tri : triList){
				TriArbitrageInfo triInfo = TRIPLE_INFO_MAP.get(tri);
				logger.debug("======Triple=" + tri);
				boolean isValid = false;
				String[] tmp = tri.split("@@");
				if (tmp.length == 3){
					triInfo.groupId = tmp[2];
					triInfo.fullPath = tmp[1];
					logger.debug("======Triple groupId= " + triInfo.groupId);
					logger.debug("======Triple fullPath= " + triInfo.fullPath);
					String[] tmp2 = tmp[1].split("-");
					if (tmp2.length == 3){
						logger.debug("====== triInfo before update = " + triInfo.toString());
						isValid = true;
						//logger.debug("tmp2[0] = " + tmp2[0]);
						//logger.debug("tmp2[1] = " + tmp2[1]);
						//logger.debug("tmp2[2] = " + tmp2[2]);
						if (pair.equals(tmp2[0])){
							triInfo.ask1 = tsf.ask;
							triInfo.bid1 = tsf.bid;
							triInfo.exchangeName = tsf.exchangeName;
							path1 = tmp2[0];
						} else if (pair.equals(tmp2[1])){
							triInfo.ask2 = tsf.ask;
							triInfo.bid2 = tsf.bid;
							triInfo.exchangeName = tsf.exchangeName;
							path2 = tmp2[1];
						} else if (pair.equals(tmp2[2])){
							triInfo.ask3 = tsf.ask;
							triInfo.bid3 = tsf.bid;
							triInfo.exchangeName = tsf.exchangeName;
							path3 = tmp2[2];
						}
						logger.debug("====== triInfo after update = " + triInfo.toString());
					}
				}
				//logger.debug("isValid = " + isValid);
				if (isValid){
					logger.debug("----------- triInfo before update = " + triInfo.toString());
					triInfo.updateProfitByGroupId();
					
					/*				if (triInfo.profitVal - 1 > = threshold){
						collector.emit(new Values(tri, triInfo.toString()));
					}*/
					logger.debug("----------- triInfo after update = " + triInfo.toString());
					if (triInfo.profitVal > 0){
						logPriceDiff(triInfo);
					}
					TRIPLE_INFO_MAP.put(tri, triInfo);
				}

			}

		}  
		logger.debug("===========================TriSpout End=======================================");
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;  
	}

	public void logPriceDiff(TriArbitrageInfo triInfo){
		String tableName = "tri_" + DataFormat.removeShortTerm(triInfo.exchangeName);
		Point point1 = Point.measurement(tableName)
				.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
				.tag("full_path", triInfo.fullPath)
				.addField("bid1", triInfo.bid1)
				.addField("ask1", triInfo.ask1)
				.addField("bid2", triInfo.bid2)
				.addField("ask2", triInfo.ask2)
				.addField("bid3", triInfo.bid3)
				.addField("ask3", triInfo.ask3)
				.addField("priceDiff", triInfo.profitVal)
				.build();
		influxDB.write(influxDbName, influxRpName, point1);
		Query query = new Query("SELECT * FROM " + tableName + " GROUP BY *", influxDbName);
		QueryResult result = influxDB.query(query);
		if (result.getResults().get(0).getSeries().get(0).getTags().isEmpty() == true){
			logger.debug("===========================InfluxDB Insert Failed=======================================");
			influxDB.close();
			influxDB = InfluxDBFactory.connect(influxURL);
		} else {
			logger.debug("===========================InfluxDB Insert Sucess=======================================");
		}
	}
	public static void loadTickerTripleMapFromFile(String path){
		String text = FileIO.readFile(path);
		ArrayList<String> ttStringList = JSON.parseObject(text, new TypeReference<ArrayList<String>>(){});

		for (String e : ttStringList){
			logger.debug("=======Input tri data = " + e);
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
			logger.debug("=======TripleData key = " + tickerName);
			logger.debug("=======TripleData val = " + trList.toString());
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