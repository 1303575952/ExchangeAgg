package com.zdx.tri;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
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
			try{
				String body = new String(msg.getBody());
				logger.info("message body = " + body);
				TickerStandardFormat tsf = new TickerStandardFormat();
				tsf.formatJsonString(body);
				Date date = new Date(tsf.timestamp*1000);
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				logger.info("message timestamp = " + simpleDateFormat.format(date));
				// 5 min message discard
				if (System.currentTimeMillis() < (tsf.timestamp + 300) * 1000){
					logger.warn("intime message handling..." );
					String pair = tsf.coinA + "/" + tsf.coinB;
					String key1 = tsf.exchangeName + "@@" + pair;
					pair = pair.toLowerCase();
					key1 = key1.toLowerCase();
					ArrayList<String> triList = new ArrayList<String>();
					if (TriSpoutConf.TICKER_TRIPLE_MAP.containsKey(key1)){
						triList = TriSpoutConf.TICKER_TRIPLE_MAP.get(key1);
					}
					logger.debug("====== influxURL = " + TriSpoutConf.influxURL);
					logger.debug("-------------------Content of TICKER_TRIPLE_MAP---------------------");
					for (Entry<String, ArrayList<String>> x: TriSpoutConf.TICKER_TRIPLE_MAP.entrySet()){
						logger.debug("---------TICKER_TRIPLE_MAP.key=" + x.getKey());
						logger.debug("---------TICKER_TRIPLE_MAP.val=" + x.getValue().toString());
					}
					logger.debug("----------------------------------------");
					logger.debug("-------------------Content of TRIPLE_INFO_MAP---------------------");
					for (Entry<String, TriArbitrageInfo> x: TriSpoutConf.TRIPLE_INFO_MAP.entrySet()){
						logger.debug("---------TRIPLE_INFO_MAP.key=" + x.getKey());
						logger.debug("---------TRIPLE_INFO_MAP.val=" + x.getValue().toString());
					}
					logger.debug("----------------------------------------");
					logger.debug("====== key1 = " + key1);
					logger.debug("====== pair = " + pair);
					logger.debug("====== triList = " + triList.toString());
					for (String tri : triList){
						TriArbitrageInfo triInfo = TriSpoutConf.TRIPLE_INFO_MAP.get(tri);
						String[] tmp = tri.split("@@");
						if (tmp.length == 3){
							triInfo.groupId = tmp[2];
							triInfo.fullPath = tmp[1];
							String[] tmp2 = tmp[1].split("-");
							if (tmp2.length == 3){
								logger.debug("====== triInfo before update ticker = " + triInfo.toString());
								if (pair.equals(tmp2[0])){
									triInfo.ask1 = tsf.ask;
									triInfo.bid1 = tsf.bid;
									triInfo.exchangeName = tsf.exchangeName;
								} else if (pair.equals(tmp2[1])){
									triInfo.ask2 = tsf.ask;
									triInfo.bid2 = tsf.bid;
									triInfo.exchangeName = tsf.exchangeName;
								} else if (pair.equals(tmp2[2])){
									triInfo.ask3 = tsf.ask;
									triInfo.bid3 = tsf.bid;
									triInfo.exchangeName = tsf.exchangeName;
								}
								logger.debug("====== triInfo after update ticker = " + triInfo.toString());
							}
						}
						logger.debug("----------- triInfo before update profit = " + triInfo.toString());
						triInfo.updateProfitByGroupId();
						//collector.emit(new Values(tri, triInfo.toString()));
						logger.debug("----------- triInfo after update profit = " + triInfo.toString());						
						logPriceDiff(triInfo);
						TriSpoutConf.TRIPLE_INFO_MAP.put(tri, triInfo);
					}
				}  else {
					logger.warn("obsolete message discard..." );
					return ConsumeConcurrentlyStatus.RECONSUME_LATER;  
				} 
			}catch (Exception e1){
				logger.warn("Unexcepted exception.", e1.getMessage());
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