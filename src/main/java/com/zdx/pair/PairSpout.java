package com.zdx.pair;
import java.util.List;
import java.util.Map;

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

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
//import backtype.storm.topology.IRichSpout;

public class PairSpout extends BaseRichSpout implements MessageListenerOrderly{  
	private static final long serialVersionUID = -3085994102089532269L;   
	private SpoutOutputCollector collector;  
	private transient DefaultMQPushConsumer consumer; 
	private static final Logger logger = LoggerFactory.getLogger(PairSpout.class);
    

	@SuppressWarnings("rawtypes")  
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) { 

		logger.info("init DefaultMQPushConsumer");
		logger.info("###"+(String) conf.get("ConsumerGroup"));
		consumer = new DefaultMQPushConsumer((String) conf.get("ConsumerGroup")); 
		consumer.setNamesrvAddr((String) conf.get("RocketMQNameServerAddress"));
		consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
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
			JSONObject jsonObject = JSON.parseObject(body);
			logger.info("Coin label ================1");
			logger.info("Coin label ================2");
			//logger.info("Coin label ================0");
			//logger.info("Spout Message body = " + body);
			//logger.info("Coin label ================1");
			String key = jsonObject.getString("exchangeType");
			//logger.info("Coin label ================2" + key);
			//logger.info("Coin label ================3" + jsonObject.toJSONString());
			//logger.info("Coin label ================" + key);
			//logger.info("Coin label ================" + key);
			if ("coin2coin".equals(key)){
				//coin to coin
				logger.info("Coin label ================111");
				String key1 = jsonObject.getString("coinA") + "_" + jsonObject.getString("coinB");
				logger.info("Coin label ================" + key1);
				collector.emit(new Values(key1, body));
				
				logger.info("send Coin data ================" + jsonObject.toJSONString());
			} else if ("coin2cash".equals(key)) {
				//coin to cash
				logger.info("Coin label ================222");
				String key2 = jsonObject.getString("coinA") + "_cash";
				collector.emit(new Values(key2, body));
				logger.info("cash label ================" + key2);
				logger.info("send cash data ================" + jsonObject.toJSONString());
			}
		}  
		return ConsumeOrderlyStatus.SUCCESS;  
	}
}  