
package com.zdx.demo;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;



public class ToyProducer {
	private static Logger logger = Logger.getLogger(ToyProducer.class);

	public static void main(String[] args) throws Exception {

		testPairArbitrageCase4();

	}

	public static void testCases(ArrayList<String> td){
		DefaultMQProducer producer = new DefaultMQProducer("TopProducer");	
		String serverUrl = "182.92.150.57:9876";
		producer.setNamesrvAddr(serverUrl);
		//
		//producer.setSendMessageWithVIPChannel(false);
		//producer.setVipChannelEnabled(false);  

		try {
			producer.start();
		} catch (MQClientException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		for (int i = 0; i < td.size(); i++) {
			logger.info("Send ---- " + i + "-----begin" );
			Message msg = new Message();
			msg.setTopic("tickerInfo");
			msg.setTags("TagA");
			JSONObject jsonObject = JSON.parseObject(td.get(i));			
			String tmp = (String) jsonObject.get("coinA") + "-" + jsonObject.get("coinB");
			msg.setBody(td.get(i).getBytes());
			try {
				int id = Math.abs(tmp.hashCode()%10);
				System.out.println("msg.setBody"+td.get(i));
				producer.sendOneway(msg, new MessageQueueSelector(){
					@Override
					public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
						// TODO Auto-generated method stub
						Integer id = (Integer) arg;
						logger.info("id"+id);
						logger.info("mqs.size" + mqs.size());
						int index = id % mqs.size();
						logger.info("index" + index);
						logger.info("msg:"+msg);
						logger.info("mqs.get(index)" + mqs.get(index));
						return mqs.get(index);
					}
				}, id);

				//producer.send(msg);
			} catch (MQClientException | RemotingException | InterruptedException e1) {
				e1.printStackTrace();
			} 			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			logger.info("Send ---- " + i + "-----done" );
		}
		producer.shutdown();

	}

	public static ArrayList<String> getTickerDataCase1(){
		ArrayList<String> td = new ArrayList<String>();
		td.add("{\"exchangeName\":\"gate-io\",\"exchangeType\":\"coin2cash\",\"coinA\":\"eth\",\"coinB\":\"usdt\",\"midUSD\":\"0.0\",\"mid\":\"332.021\",\"bid\":\"1\",\"ask\":\"2\",\"last_price\":\"332.1136\",\"low\":\"316.72\",\"high\":\"336.0008\",\"volume\":\"605576.4685\",\"timestamp\":\"1510740493123\"}");
		td.add("{\"exchangeName\":\"gate-io\",\"exchangeType\":\"coin2cash\",\"coinA\":\"btc\",\"coinB\":\"usdt\",\"midUSD\":\"0.0\",\"mid\":\"332.021\",\"bid\":\"3\",\"ask\":\"4\",\"last_price\":\"332.1136\",\"low\":\"316.72\",\"high\":\"336.0008\",\"volume\":\"605576.4685\",\"timestamp\":\"1510740493123\"}");
		td.add("{\"exchangeName\":\"gate-io\",\"exchangeType\":\"coin2cash\",\"coinA\":\"eth\",\"coinB\":\"usdt\",\"midUSD\":\"0.0\",\"mid\":\"332.021\",\"bid\":\"5\",\"ask\":\"6\",\"last_price\":\"332.1136\",\"low\":\"316.72\",\"high\":\"336.0008\",\"volume\":\"605576.4685\",\"timestamp\":\"1510740493123\"}");
		td.add("{\"exchangeName\":\"gate-io\",\"exchangeType\":\"coin2cash\",\"coinA\":\"eth\",\"coinB\":\"usdt\",\"midUSD\":\"0.0\",\"mid\":\"332.021\",\"bid\":\"7\",\"ask\":\"8\",\"last_price\":\"332.1136\",\"low\":\"316.72\",\"high\":\"336.0008\",\"volume\":\"605576.4685\",\"timestamp\":\"1510740493123\"}");
		td.add("{\"exchangeName\":\"gate-io\",\"exchangeType\":\"coin2cash\",\"coinA\":\"eth\",\"coinB\":\"btc\",\"midUSD\":\"0.0\",\"mid\":\"332.021\",\"bid\":\"9\",\"ask\":\"10\",\"last_price\":\"332.1136\",\"low\":\"316.72\",\"high\":\"336.0008\",\"volume\":\"605576.4685\",\"timestamp\":\"1510740493123\"}");
		td.add("{\"exchangeName\":\"gate-io\",\"exchangeType\":\"coin2cash\",\"coinA\":\"btc\",\"coinB\":\"usdt\",\"midUSD\":\"0.0\",\"mid\":\"332.021\",\"bid\":\"11\",\"ask\":\"12\",\"last_price\":\"332.1136\",\"low\":\"316.72\",\"high\":\"336.0008\",\"volume\":\"605576.4685\",\"timestamp\":\"1510740493123\"}");
		td.add("{\"exchangeName\":\"gate-io\",\"exchangeType\":\"coin2cash\",\"coinA\":\"eth\",\"coinB\":\"usdt\",\"midUSD\":\"0.0\",\"mid\":\"332.021\",\"bid\":\"13\",\"ask\":\"14\",\"last_price\":\"332.1136\",\"low\":\"316.72\",\"high\":\"336.0008\",\"volume\":\"605576.4685\",\"timestamp\":\"1510740493123\"}");
		td.add("{\"exchangeName\":\"gate-io\",\"exchangeType\":\"coin2cash\",\"coinA\":\"eth\",\"coinB\":\"usdt\",\"midUSD\":\"0.0\",\"mid\":\"332.021\",\"bid\":\"15\",\"ask\":\"16\",\"last_price\":\"332.1136\",\"low\":\"316.72\",\"high\":\"336.0008\",\"volume\":\"605576.4685\",\"timestamp\":\"1510740493123\"}");
		td.add("{\"exchangeName\":\"gate-io\",\"exchangeType\":\"coin2cash\",\"coinA\":\"eth\",\"coinB\":\"btc\",\"midUSD\":\"0.0\",\"mid\":\"332.021\",\"bid\":\"17\",\"ask\":\"18\",\"last_price\":\"332.1136\",\"low\":\"316.72\",\"high\":\"336.0008\",\"volume\":\"605576.4685\",\"timestamp\":\"1510740493123\"}");
		return td;
	}

	public static ArrayList<String> getTickerDataCase2(){
		ArrayList<String> td = new ArrayList<String>();
		td.add("{\"exchangeName\":\"gate-io\",\"exchangeType\":\"coin2cash\",\"coinA\":\"eth\",\"coinB\":\"usdt\",\"midUSD\":\"0.0\",\"mid\":\"332.021\",\"bid\":\"1\",\"ask\":\"2\",\"last_price\":\"332.1136\",\"low\":\"316.72\",\"high\":\"336.0008\",\"volume\":\"605576.4685\",\"timestamp\":\"1510740493123\"}");
		td.add("{\"exchangeName\":\"gate-io\",\"exchangeType\":\"coin2cash\",\"coinA\":\"btc\",\"coinB\":\"usdt\",\"midUSD\":\"0.0\",\"mid\":\"332.021\",\"bid\":\"3\",\"ask\":\"4\",\"last_price\":\"332.1136\",\"low\":\"316.72\",\"high\":\"336.0008\",\"volume\":\"605576.4685\",\"timestamp\":\"1510740493123\"}");
		td.add("{\"exchangeName\":\"gate-io\",\"exchangeType\":\"coin2cash\",\"coinA\":\"eth\",\"coinB\":\"btc\",\"midUSD\":\"0.0\",\"mid\":\"332.021\",\"bid\":\"5\",\"ask\":\"6\",\"last_price\":\"332.1136\",\"low\":\"316.72\",\"high\":\"336.0008\",\"volume\":\"605576.4685\",\"timestamp\":\"1510740493123\"}");		
		return td;
	}

	public static ArrayList<String> getTickerDataCase3(){
		//coin 2 coin in two exchanges
		ArrayList<String> td = new ArrayList<String>();
		td.add("{\"exchangeName\":\"livecoin\",\"exchangeType\":\"coin2cash\",\"coinA\":\"btc\",\"coinB\":\"usd\",\"midUSD\":\"0.0\",\"mid\":\"7825.89805\",\"bid\":\"100.0\",\"ask\":\"150.0\",\"last_price\":\"7845.6886\",\"low\":\"7564.33\",\"high\":\"7860.8157\",\"volume\":\"238061.3136\",\"timestamp\":\"1\"}");
		td.add("{\"exchangeName\":\"bitstamp\",\"exchangeType\":\"coin2cash\",\"coinA\":\"btc\",\"coinB\":\"usd\",\"midUSD\":\"0.0\",\"mid\":\"7825.89805\",\"bid\":\"200.0\",\"ask\":\"250.0\",\"last_price\":\"7845.6886\",\"low\":\"7564.33\",\"high\":\"7860.8157\",\"volume\":\"238061.3136\",\"timestamp\":\"1\"}");
		return td;
	}

	public static void testPairArbitrageCase1(){
		ArrayList<String> td = new ArrayList<String>();
		td.add("{\"exchangeName\":\"livecoin\",\"exchangeType\":\"coin2cash\",\"coinA\":\"btc\",\"coinB\":\"usd\",\"midUSD\":\"0.0\",\"mid\":\"7825.89805\",\"bid\":\"100.0\",\"ask\":\"150.0\",\"last_price\":\"7845.6886\",\"low\":\"7564.33\",\"high\":\"7860.8157\",\"volume\":\"238061.3136\",\"timestamp\":\"54321\"}");
		td.add("{\"exchangeName\":\"bitstamp\",\"exchangeType\":\"coin2cash\",\"coinA\":\"btc\",\"coinB\":\"usd\",\"midUSD\":\"0.0\",\"mid\":\"7825.89805\",\"bid\":\"200.0\",\"ask\":\"250.0\",\"last_price\":\"7845.6886\",\"low\":\"7564.33\",\"high\":\"7860.8157\",\"volume\":\"238061.3136\",\"timestamp\":\"54321\"}");
		testCases(td);

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		td.clear();
		td.add("{\"exchangeName\":\"livecoin\",\"exchangeType\":\"coin2cash\",\"coinA\":\"btc\",\"coinB\":\"usd\",\"midUSD\":\"0.0\",\"mid\":\"7825.89805\",\"bid\":\"140.0\",\"ask\":\"160.0\",\"last_price\":\"7845.6886\",\"low\":\"7564.33\",\"high\":\"7860.8157\",\"volume\":\"238061.3136\",\"timestamp\":\"1\"}");
		td.add("{\"exchangeName\":\"bitstamp\",\"exchangeType\":\"coin2cash\",\"coinA\":\"btc\",\"coinB\":\"usd\",\"midUSD\":\"0.0\",\"mid\":\"7825.89805\",\"bid\":\"190.0\",\"ask\":\"200.0\",\"last_price\":\"7845.6886\",\"low\":\"7564.33\",\"high\":\"7860.8157\",\"volume\":\"238061.3136\",\"timestamp\":\"1\"}");
		testCases(td);

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		td.clear();
		td.add("{\"exchangeName\":\"livecoin\",\"exchangeType\":\"coin2cash\",\"coinA\":\"btc\",\"coinB\":\"usd\",\"midUSD\":\"0.0\",\"mid\":\"7825.89805\",\"bid\":\"160.0\",\"ask\":\"170.0\",\"last_price\":\"7845.6886\",\"low\":\"7564.33\",\"high\":\"7860.8157\",\"volume\":\"238061.3136\",\"timestamp\":\"1\"}");
		td.add("{\"exchangeName\":\"bitstamp\",\"exchangeType\":\"coin2cash\",\"coinA\":\"btc\",\"coinB\":\"usd\",\"midUSD\":\"0.0\",\"mid\":\"7825.89805\",\"bid\":\"180.0\",\"ask\":\"190.0\",\"last_price\":\"7845.6886\",\"low\":\"7564.33\",\"high\":\"7860.8157\",\"volume\":\"238061.3136\",\"timestamp\":\"1\"}");
		testCases(td);

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		td.clear();
		td.add("{\"exchangeName\":\"livecoin\",\"exchangeType\":\"coin2cash\",\"coinA\":\"btc\",\"coinB\":\"usd\",\"midUSD\":\"0.0\",\"mid\":\"7825.89805\",\"bid\":\"175.0\",\"ask\":\"176.0\",\"last_price\":\"7845.6886\",\"low\":\"7564.33\",\"high\":\"7860.8157\",\"volume\":\"238061.3136\",\"timestamp\":\"1\"}");
		td.add("{\"exchangeName\":\"bitstamp\",\"exchangeType\":\"coin2cash\",\"coinA\":\"btc\",\"coinB\":\"usd\",\"midUSD\":\"0.0\",\"mid\":\"7825.89805\",\"bid\":\"174.0\",\"ask\":\"175.0\",\"last_price\":\"7845.6886\",\"low\":\"7564.33\",\"high\":\"7860.8157\",\"volume\":\"238061.3136\",\"timestamp\":\"1\"}");
		testCases(td);

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		td.clear();
		td.add("{\"exchangeName\":\"livecoin\",\"exchangeType\":\"coin2cash\",\"coinA\":\"btc\",\"coinB\":\"usd\",\"midUSD\":\"0.0\",\"mid\":\"7825.89805\",\"bid\":\"175.5\",\"ask\":\"176.9\",\"last_price\":\"7845.6886\",\"low\":\"7564.33\",\"high\":\"7860.8157\",\"volume\":\"238061.3136\",\"timestamp\":\"1\"}");
		td.add("{\"exchangeName\":\"bitstamp\",\"exchangeType\":\"coin2cash\",\"coinA\":\"btc\",\"coinB\":\"usd\",\"midUSD\":\"0.0\",\"mid\":\"7825.89805\",\"bid\":\"174.5\",\"ask\":\"175.9\",\"last_price\":\"7845.6886\",\"low\":\"7564.33\",\"high\":\"7860.8157\",\"volume\":\"238061.3136\",\"timestamp\":\"1\"}");
		testCases(td);
	}

	public static void testPairArbitrageCase4(){
		ArrayList<String> td = new ArrayList<String>();
		td.add("{\"exchangeName\":\"livecoin\",\"exchangeType\":\"coin2cash\",\"coinA\":\"btc\",\"coinB\":\"usd\",\"midUSD\":\"0.0\",\"mid\":\"7825.89805\",\"bid\":\"100.0\",\"ask\":\"150.0\",\"last_price\":\"7845.6886\",\"low\":\"7564.33\",\"high\":\"7860.8157\",\"volume\":\"238061.3136\",\"timestamp\":\"54321\"}");
		td.add("{\"exchangeName\":\"bitstamp\",\"exchangeType\":\"coin2cash\",\"coinA\":\"btc\",\"coinB\":\"usd\",\"midUSD\":\"0.0\",\"mid\":\"7825.89805\",\"bid\":\"200.0\",\"ask\":\"250.0\",\"last_price\":\"7845.6886\",\"low\":\"7564.33\",\"high\":\"7860.8157\",\"volume\":\"238061.3136\",\"timestamp\":\"54321\"}");
		
		td.add("{\"exchangeName\":\"livecoin\",\"exchangeType\":\"coin2coin\",\"coinA\":\"eth\",\"coinB\":\"btc\",\"midUSD\":\"0.0\",\"mid\":\"7825.89805\",\"bid\":\"175.0\",\"ask\":\"176.0\",\"last_price\":\"7845.6886\",\"low\":\"7564.33\",\"high\":\"7860.8157\",\"volume\":\"238061.3136\",\"timestamp\":\"1\"}");
		td.add("{\"exchangeName\":\"bitstamp\",\"exchangeType\":\"coin2coin\",\"coinA\":\"eth\",\"coinB\":\"btc\",\"midUSD\":\"0.0\",\"mid\":\"7825.89805\",\"bid\":\"174.0\",\"ask\":\"175.0\",\"last_price\":\"7845.6886\",\"low\":\"7564.33\",\"high\":\"7860.8157\",\"volume\":\"238061.3136\",\"timestamp\":\"1\"}");

		td.add("{\"exchangeName\":\"livecoin\",\"exchangeType\":\"coin2cash\",\"coinA\":\"btc\",\"coinB\":\"usd\",\"midUSD\":\"0.0\",\"mid\":\"7825.89805\",\"bid\":\"140.0\",\"ask\":\"160.0\",\"last_price\":\"7845.6886\",\"low\":\"7564.33\",\"high\":\"7860.8157\",\"volume\":\"238061.3136\",\"timestamp\":\"1\"}");
		td.add("{\"exchangeName\":\"bitstamp\",\"exchangeType\":\"coin2cash\",\"coinA\":\"btc\",\"coinB\":\"usd\",\"midUSD\":\"0.0\",\"mid\":\"7825.89805\",\"bid\":\"190.0\",\"ask\":\"200.0\",\"last_price\":\"7845.6886\",\"low\":\"7564.33\",\"high\":\"7860.8157\",\"volume\":\"238061.3136\",\"timestamp\":\"1\"}");
		
		td.add("{\"exchangeName\":\"livecoin\",\"exchangeType\":\"coin2coin\",\"coinA\":\"eth\",\"coinB\":\"btc\",\"midUSD\":\"0.0\",\"mid\":\"7825.89805\",\"bid\":\"160.0\",\"ask\":\"170.0\",\"last_price\":\"7845.6886\",\"low\":\"7564.33\",\"high\":\"7860.8157\",\"volume\":\"238061.3136\",\"timestamp\":\"1\"}");
		td.add("{\"exchangeName\":\"bitstamp\",\"exchangeType\":\"coin2coin\",\"coinA\":\"eth\",\"coinB\":\"btc\",\"midUSD\":\"0.0\",\"mid\":\"7825.89805\",\"bid\":\"180.0\",\"ask\":\"190.0\",\"last_price\":\"7845.6886\",\"low\":\"7564.33\",\"high\":\"7860.8157\",\"volume\":\"238061.3136\",\"timestamp\":\"1\"}");

		td.add("{\"exchangeName\":\"livecoin\",\"exchangeType\":\"coin2cash\",\"coinA\":\"btc\",\"coinB\":\"usd\",\"midUSD\":\"0.0\",\"mid\":\"7825.89805\",\"bid\":\"160.0\",\"ask\":\"170.0\",\"last_price\":\"7845.6886\",\"low\":\"7564.33\",\"high\":\"7860.8157\",\"volume\":\"238061.3136\",\"timestamp\":\"1\"}");
		td.add("{\"exchangeName\":\"bitstamp\",\"exchangeType\":\"coin2cash\",\"coinA\":\"btc\",\"coinB\":\"usd\",\"midUSD\":\"0.0\",\"mid\":\"7825.89805\",\"bid\":\"180.0\",\"ask\":\"190.0\",\"last_price\":\"7845.6886\",\"low\":\"7564.33\",\"high\":\"7860.8157\",\"volume\":\"238061.3136\",\"timestamp\":\"1\"}");

		td.add("{\"exchangeName\":\"livecoin\",\"exchangeType\":\"coin2coin\",\"coinA\":\"eth\",\"coinB\":\"btc\",\"midUSD\":\"0.0\",\"mid\":\"7825.89805\",\"bid\":\"140.0\",\"ask\":\"160.0\",\"last_price\":\"7845.6886\",\"low\":\"7564.33\",\"high\":\"7860.8157\",\"volume\":\"238061.3136\",\"timestamp\":\"1\"}");
		td.add("{\"exchangeName\":\"bitstamp\",\"exchangeType\":\"coin2coin\",\"coinA\":\"eth\",\"coinB\":\"btc\",\"midUSD\":\"0.0\",\"mid\":\"7825.89805\",\"bid\":\"190.0\",\"ask\":\"200.0\",\"last_price\":\"7845.6886\",\"low\":\"7564.33\",\"high\":\"7860.8157\",\"volume\":\"238061.3136\",\"timestamp\":\"1\"}");

		
		td.add("{\"exchangeName\":\"livecoin\",\"exchangeType\":\"coin2cash\",\"coinA\":\"btc\",\"coinB\":\"usd\",\"midUSD\":\"0.0\",\"mid\":\"7825.89805\",\"bid\":\"175.0\",\"ask\":\"176.0\",\"last_price\":\"7845.6886\",\"low\":\"7564.33\",\"high\":\"7860.8157\",\"volume\":\"238061.3136\",\"timestamp\":\"1\"}");
		td.add("{\"exchangeName\":\"bitstamp\",\"exchangeType\":\"coin2cash\",\"coinA\":\"btc\",\"coinB\":\"usd\",\"midUSD\":\"0.0\",\"mid\":\"7825.89805\",\"bid\":\"174.0\",\"ask\":\"175.0\",\"last_price\":\"7845.6886\",\"low\":\"7564.33\",\"high\":\"7860.8157\",\"volume\":\"238061.3136\",\"timestamp\":\"1\"}");

		td.add("{\"exchangeName\":\"livecoin\",\"exchangeType\":\"coin2coin\",\"coinA\":\"eth\",\"coinB\":\"btc\",\"midUSD\":\"0.0\",\"mid\":\"7825.89805\",\"bid\":\"100.0\",\"ask\":\"150.0\",\"last_price\":\"7845.6886\",\"low\":\"7564.33\",\"high\":\"7860.8157\",\"volume\":\"238061.3136\",\"timestamp\":\"54321\"}");
		td.add("{\"exchangeName\":\"bitstamp\",\"exchangeType\":\"coin2coin\",\"coinA\":\"eth\",\"coinB\":\"btc\",\"midUSD\":\"0.0\",\"mid\":\"7825.89805\",\"bid\":\"200.0\",\"ask\":\"250.0\",\"last_price\":\"7845.6886\",\"low\":\"7564.33\",\"high\":\"7860.8157\",\"volume\":\"238061.3136\",\"timestamp\":\"54321\"}");

		td.add("{\"exchangeName\":\"livecoin\",\"exchangeType\":\"coin2cash\",\"coinA\":\"btc\",\"coinB\":\"usd\",\"midUSD\":\"0.0\",\"mid\":\"7825.89805\",\"bid\":\"175.5\",\"ask\":\"176.9\",\"last_price\":\"7845.6886\",\"low\":\"7564.33\",\"high\":\"7860.8157\",\"volume\":\"238061.3136\",\"timestamp\":\"1\"}");
		td.add("{\"exchangeName\":\"bitstamp\",\"exchangeType\":\"coin2cash\",\"coinA\":\"btc\",\"coinB\":\"usd\",\"midUSD\":\"0.0\",\"mid\":\"7825.89805\",\"bid\":\"174.5\",\"ask\":\"175.9\",\"last_price\":\"7845.6886\",\"low\":\"7564.33\",\"high\":\"7860.8157\",\"volume\":\"238061.3136\",\"timestamp\":\"1\"}");

	
	


		for(int j = 0; j < 100; j++){
			for (int i = 0; i < td.size(); i++){
				ArrayList<String> td2 = new ArrayList<String>();
				td2.add(td.get(i));
				testCases(td2);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}
}

