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
import com.zdx.pair.PairSpoutConf;

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

	public static InfluxDB influxDB = null;

	@SuppressWarnings("rawtypes")  
	@Override
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		logger.debug("===========================TriSpout prepare Begin=======================================");
		logger.debug("========================== Conf=" + conf.get("SpoutData").toString());

		TriSpoutConf.buildSpoutConfig(conf.get("SpoutData").toString());

		influxDB = InfluxDBFactory.connect(TriSpoutConf.influxURL);
		if (!influxDB.databaseExists(TriSpoutConf.influxDbName)){
			logger.debug("==================================================================Database" + TriSpoutConf.influxDbName + " not Exist");
			influxDB.createDatabase(TriSpoutConf.influxDbName);
		}
		influxDB.setDatabase(TriSpoutConf.influxDbName);
		influxDB.createRetentionPolicy(TriSpoutConf.influxRpName, TriSpoutConf.influxDbName, "30d", "30m", 2, true);

		consumer = new DefaultMQPushConsumer(TriSpoutConf.consumerGroup); 
		consumer.setNamesrvAddr(TriSpoutConf.mqAddress);
		consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
		consumer.setMessageModel(MessageModel.BROADCASTING);
		consumer.registerMessageListener(this);
		consumer.setConsumeThreadMin(1);
		consumer.setConsumeThreadMax(1);
		
		for (int i = 0; i < TriSpoutConf.topicList.size(); i++){
			try {
				consumer.subscribe("ticker_" + TriSpoutConf.topicList.get(i).toLowerCase(), "*");
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
			if (TriSpoutConf.TICKER_TRIPLE_MAP.containsKey(key1)){
				triList = TriSpoutConf.TICKER_TRIPLE_MAP.get(key1);
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
				TriArbitrageInfo triInfo = TriSpoutConf.TRIPLE_INFO_MAP.get(tri);
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
					TriSpoutConf.TRIPLE_INFO_MAP.put(tri, triInfo);
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
		influxDB.write(TriSpoutConf.influxDbName, TriSpoutConf.influxRpName, point1);
		Query query = new Query("SELECT * FROM " + tableName + " GROUP BY *", TriSpoutConf.influxDbName);
		QueryResult result = influxDB.query(query);
		if (result.getResults().get(0).getSeries().get(0).getTags().isEmpty() == true){
			logger.debug("===========================InfluxDB Insert Failed=======================================");
			influxDB.close();
			influxDB = InfluxDBFactory.connect(TriSpoutConf.influxURL);
		} else {
			logger.debug("===========================InfluxDB Insert Sucess=======================================");
		}
	}



}  