package com.zdx.pair;
import java.util.ArrayList;
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
		//String filePath1 = "C:\\Users\\zdx\\git\\ExchangeAgg\\conf\\topVol100M.json";
		//String filePath2 = "C:\\Users\\zdx\\git\\ExchangeAgg\\conf\\ToyTopVol100MPair.json";
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
		/*consumer.registerMessageListener(new MessageListenerOrderly() {
			
			@Override
			public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
				context.setAutoCommit(true);  
                for (MessageExt msg : msgs) {  
                    logger.info(msg + ",内容：" + new String(msg.getBody()));
                    TickerStandardFormat tsf = new TickerStandardFormat();
                    String body = new String(msg.getBody());
					tsf.formatJsonString(body);
					updatePairPrice(tsf);
                }  
  
                try {  
                    TimeUnit.SECONDS.sleep(5L);  
                } catch (InterruptedException e) {  

                    e.printStackTrace();  
                }  
                ;  
  
                return ConsumeOrderlyStatus.SUCCESS;
				
			}
		});*/
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
			System.out.println("-----------1" + body);
			TickerStandardFormat tsf = new TickerStandardFormat();
			tsf.formatJsonString(body);
			updatePairPrice(tsf);
		}  
		return ConsumeOrderlyStatus.SUCCESS;  
	}
	
	public void updatePairPrice(TickerStandardFormat tsf){
		String pair = tsf.coinA.toLowerCase() + "_" + tsf.coinB.toLowerCase();
		String exchangePairName = tsf.exchangeName.toLowerCase() + "_" + pair;
		if (pc.pairFourthMap.containsKey(exchangePairName)){
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
						LowestPricePair lpp = PairBolt.getPairResetInfo(t1[0], t1[1]);
						System.out.println("    ---3-" + lpp.resetFee);
					}
					pc.fourthPriceMap.put(x, ep);
				}

			}
		}
	}
}  