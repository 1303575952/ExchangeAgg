package com.zdx.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.zdx.common.CoinCashCommon;
import com.zdx.common.FileIO;
import com.zdx.tri.TriArbitrageInfo;


import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;

public class ToyConsumer {
	private static Logger logger = Logger.getLogger(ToyConsumer.class);
	private static final HashMap<String, ArrayList<String>> tickerTripleMap = new HashMap<String, ArrayList<String>>();
	private static final HashMap<String, TriArbitrageInfo> tripleInfoMap = new HashMap<String, TriArbitrageInfo>();
	AtomicLong consumeTimes = new AtomicLong(0);
	public static void main(String[] args) throws Exception {
		DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("ToyConsumer");
		String filePath = "C:\\Users\\zdx\\git\\ExchangeAgg\\conf\\t3.json";
		loadTickerTripleMapFromFile(filePath);
		buildTripleInfoMap();

		consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
		String serverUrl = "182.92.150.57:9876";
		consumer.setNamesrvAddr(serverUrl);
		consumer.subscribe("toyTickerTest", "TagA || TagC || TagD");

		//ToyTriSpout trConsumer = new ToyTriSpout();
		//consumer.registerMessageListener(trConsumer);
		consumer.registerMessageListener(new MessageListenerOrderly() {  
            AtomicLong consumeTimes = new AtomicLong(0);  
  
            public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {  
                // 设置自动提交  
                context.setAutoCommit(true);  
                for (MessageExt msg : msgs) {  
                    logger.info(msg + ",内容：" + new String(msg.getBody()));  
                }  
  
                try {  
                    TimeUnit.SECONDS.sleep(5L);  
                } catch (InterruptedException e) {  
  
                    e.printStackTrace();  
                }  
                ;  
  
                return ConsumeOrderlyStatus.SUCCESS;  
            }  
        });  
		consumer.start();

		System.out.printf("Consumer Started.%n");
	}

	public static void buildTripleInfoMap(){
		for ( Entry<String, ArrayList<String>> e : tickerTripleMap.entrySet()){
			ArrayList<String> val1 = e.getValue();
			for (String s : val1){							
				TriArbitrageInfo m = new TriArbitrageInfo();
				tripleInfoMap.put(s, m);
			}

		}
		logger.info("-----------1----------- " + tickerTripleMap.keySet().toString());
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
}
class ToyTriSpout extends BaseRichSpout implements MessageListenerOrderly{
	private static Logger logger = Logger.getLogger(ToyTriSpout.class);
	HashMap<String, String> tmp = new HashMap<String, String>();
	
	private transient DefaultMQPushConsumer consumer; 


	@Override  
	public void nextTuple() {  
		//do nothing  
	}  

	@Override  
	public void declareOutputFields(OutputFieldsDeclarer declarer) {  
		declarer.declare(new Fields("exchangeName"));
	}

	@Override
	public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs,
			ConsumeOrderlyContext context) { 
		context.setAutoCommit(true);
		System.out.printf(Thread.currentThread().getName() + " Receive New Messages: " + msgs + "%n");
		for (MessageExt msg : msgs) {

			System.out.printf(Thread.currentThread().getName() + "----" + System.currentTimeMillis() + " Receive New Messages: " + msgs + "%n");
			String body = new String(msg.getBody());
			logger.info("before update-------------1--------------");
			logger.info(body);
			//logger.info(tmp.toString());
			//logger.info("before update---------------------------");
			JSONObject jsonObject = JSON.parseObject(body);			
			tmp.put("coinA", (String) jsonObject.get("bid"));
			//tmp.put("coinA", (String) jsonObject.get("as"));
			//logger.info("after update---------------------------");
			logger.info(tmp.toString());
			logger.info("after update---------------------------");
			//collector.emit(new Values(key1, body));

			//logger.info("send Coin data ================" + jsonObject.toJSONString());

		}  
		return ConsumeOrderlyStatus.SUCCESS;  
	}

	@Override
	public void open(Map arg0, TopologyContext arg1, SpoutOutputCollector arg2) {
		// TODO Auto-generated method stub
		
	}




}  