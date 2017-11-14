package com.zdx.storm;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
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

public class TestRocketMQStormSpout extends BaseRichSpout implements MessageListenerConcurrently{  
	private static final long serialVersionUID = -3085994102089532269L;   
	private SpoutOutputCollector collector;  
	private transient DefaultMQPushConsumer consumer; 
	private static final Logger logger = LoggerFactory.getLogger(TestRocketMQStormSpout.class);
    

	@SuppressWarnings("rawtypes")  
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) { 

		logger.info("init DefaultMQPushConsumer");  
		consumer = new DefaultMQPushConsumer(AggConfig.consumerGroup); 
		consumer.setNamesrvAddr(AggConfig.getRocketMQNameServerAddress());
		consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
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
		declarer.declare(new Fields("tickerType"));
	}

	@Override
	public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
			ConsumeConcurrentlyContext context) {  
		for (MessageExt msg : msgs) {
			String body = new String(msg.getBody());
			JSONObject jsonObject = JSON.parseObject(body);
			System.out.println("Coin label ================1");
			System.out.println("Coin label ================2");
			//logger.info("Coin label ================0");
			//logger.info("Spout Message body = " + body);
			//logger.info("Coin label ================1");
			String key = jsonObject.getString("exchangeType");
			//logger.info("Coin label ================2" + key);
			//logger.info("Coin label ================3" + jsonObject.toJSONString());
			//System.out.println("Coin label ================" + key);
			//System.out.println("Coin label ================" + key);
			if ("coin2coin".equals(key)){
				//coin to coin
				System.out.println("Coin label ================111");
				String key1 = jsonObject.getString("coinA") + "_" + jsonObject.getString("coinB");
				System.out.println("Coin label ================" + key1);
				collector.emit(new Values(key1, body));
				
				System.out.println("send Coin data ================" + jsonObject.toJSONString());
			} else if ("coin2cash".equals(key)) {
				//coin to cash
				System.out.println("Coin label ================222");
				String key2 = jsonObject.getString("coinA") + "_cash";
				collector.emit(new Values(key2, body));
				System.out.println("cash label ================" + key2);
				System.out.println("send cash data ================" + jsonObject.toJSONString());
			}
		}  
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;  
	}
}  