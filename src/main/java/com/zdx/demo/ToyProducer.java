
package com.zdx.demo;

import java.util.ArrayList;
import java.util.List;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;



public class ToyProducer {


	public static void main(String[] args) throws Exception {

		testCases(getTickerDataCase1());
		//testCases(getTickerDataCase2());


	}

	public static void testCases(ArrayList<String> td){
		DefaultMQProducer producer = new DefaultMQProducer("TopProducer");	
		String serverUrl = "182.92.150.57:9876";
		producer.setNamesrvAddr(serverUrl);
		try {
			producer.start();
		} catch (MQClientException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		for (int i = 0; i < td.size(); i++) {
			System.out.println("Send ---- " + i + "-----begin" );
			Message msg = new Message();
			msg.setTopic("toyTickerTest");
			msg.setTags("TagA");
			JSONObject jsonObject = JSON.parseObject(td.get(i));			
			String tmp = (String) jsonObject.get("coinA") + "-" + jsonObject.get("coinB");
			msg.setBody(td.get(i).getBytes());
			try {
				int id = 1;//tmp.hashCode()%10;
				producer.sendOneway(msg, new MessageQueueSelector(){
					@Override
					public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
						// TODO Auto-generated method stub
						Integer id = (Integer) arg;
						System.out.println("mqs.size" + mqs.size());
						int index = id % mqs.size();
						System.out.println("index" + index);
						System.out.println("mqs.get(index)" + mqs.get(index));
						return mqs.get(index);
					}
				}, id);
			} catch (MQClientException | RemotingException | InterruptedException e1) {
				e1.printStackTrace();
			}			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Send ---- " + i + "-----done" );
		}
		producer.shutdown();

	}

	public static ArrayList<String> getTickerDataCase1(){
		ArrayList<String> td = new ArrayList<String>();
		td.add("{\"exchangeName\":\"gate-io\",\"exchangeType\":\"coin2cash\",\"coinA\":\"eth\",\"coinB\":\"usdt\",\"midUSD\":\"0.0\",\"mid\":\"332.021\",\"bid\":\"1\",\"ask\":\"2\",\"last_price\":\"332.1136\",\"low\":\"316.72\",\"high\":\"336.0008\",\"volume\":\"605576.4685\",\"timestamp\":\"1510740493123\"}");
		td.add("{\"exchangeName\":\"gate-io\",\"exchangeType\":\"coin2cash\",\"coinA\":\"eth\",\"coinB\":\"usdt\",\"midUSD\":\"0.0\",\"mid\":\"332.021\",\"bid\":\"3\",\"ask\":\"4\",\"last_price\":\"332.1136\",\"low\":\"316.72\",\"high\":\"336.0008\",\"volume\":\"605576.4685\",\"timestamp\":\"1510740493123\"}");
		td.add("{\"exchangeName\":\"gate-io\",\"exchangeType\":\"coin2cash\",\"coinA\":\"eth\",\"coinB\":\"usdt\",\"midUSD\":\"0.0\",\"mid\":\"332.021\",\"bid\":\"5\",\"ask\":\"6\",\"last_price\":\"332.1136\",\"low\":\"316.72\",\"high\":\"336.0008\",\"volume\":\"605576.4685\",\"timestamp\":\"1510740493123\"}");
		td.add("{\"exchangeName\":\"gate-io\",\"exchangeType\":\"coin2cash\",\"coinA\":\"eth\",\"coinB\":\"usdt\",\"midUSD\":\"0.0\",\"mid\":\"332.021\",\"bid\":\"7\",\"ask\":\"8\",\"last_price\":\"332.1136\",\"low\":\"316.72\",\"high\":\"336.0008\",\"volume\":\"605576.4685\",\"timestamp\":\"1510740493123\"}");
		td.add("{\"exchangeName\":\"gate-io\",\"exchangeType\":\"coin2cash\",\"coinA\":\"eth\",\"coinB\":\"usdt\",\"midUSD\":\"0.0\",\"mid\":\"332.021\",\"bid\":\"9\",\"ask\":\"10\",\"last_price\":\"332.1136\",\"low\":\"316.72\",\"high\":\"336.0008\",\"volume\":\"605576.4685\",\"timestamp\":\"1510740493123\"}");
		td.add("{\"exchangeName\":\"gate-io\",\"exchangeType\":\"coin2cash\",\"coinA\":\"eth\",\"coinB\":\"usdt\",\"midUSD\":\"0.0\",\"mid\":\"332.021\",\"bid\":\"11\",\"ask\":\"12\",\"last_price\":\"332.1136\",\"low\":\"316.72\",\"high\":\"336.0008\",\"volume\":\"605576.4685\",\"timestamp\":\"1510740493123\"}");
		td.add("{\"exchangeName\":\"gate-io\",\"exchangeType\":\"coin2cash\",\"coinA\":\"eth\",\"coinB\":\"usdt\",\"midUSD\":\"0.0\",\"mid\":\"332.021\",\"bid\":\"13\",\"ask\":\"14\",\"last_price\":\"332.1136\",\"low\":\"316.72\",\"high\":\"336.0008\",\"volume\":\"605576.4685\",\"timestamp\":\"1510740493123\"}");
		td.add("{\"exchangeName\":\"gate-io\",\"exchangeType\":\"coin2cash\",\"coinA\":\"eth\",\"coinB\":\"usdt\",\"midUSD\":\"0.0\",\"mid\":\"332.021\",\"bid\":\"15\",\"ask\":\"16\",\"last_price\":\"332.1136\",\"low\":\"316.72\",\"high\":\"336.0008\",\"volume\":\"605576.4685\",\"timestamp\":\"1510740493123\"}");
		td.add("{\"exchangeName\":\"gate-io\",\"exchangeType\":\"coin2cash\",\"coinA\":\"eth\",\"coinB\":\"usdt\",\"midUSD\":\"0.0\",\"mid\":\"332.021\",\"bid\":\"17\",\"ask\":\"18\",\"last_price\":\"332.1136\",\"low\":\"316.72\",\"high\":\"336.0008\",\"volume\":\"605576.4685\",\"timestamp\":\"1510740493123\"}");
		return td;
	}

	public static ArrayList<String> getTickerDataCase2(){
		ArrayList<String> td = new ArrayList<String>();
		td.add("{\"exchangeName\":\"gate-io\",\"exchangeType\":\"coin2cash\",\"coinA\":\"eth\",\"coinB\":\"usdt\",\"midUSD\":\"0.0\",\"mid\":\"332.021\",\"bid\":\"1\",\"ask\":\"2\",\"last_price\":\"332.1136\",\"low\":\"316.72\",\"high\":\"336.0008\",\"volume\":\"605576.4685\",\"timestamp\":\"1510740493123\"}");
		td.add("{\"exchangeName\":\"gate-io\",\"exchangeType\":\"coin2cash\",\"coinA\":\"btc\",\"coinB\":\"usdt\",\"midUSD\":\"0.0\",\"mid\":\"332.021\",\"bid\":\"3\",\"ask\":\"4\",\"last_price\":\"332.1136\",\"low\":\"316.72\",\"high\":\"336.0008\",\"volume\":\"605576.4685\",\"timestamp\":\"1510740493123\"}");
		td.add("{\"exchangeName\":\"gate-io\",\"exchangeType\":\"coin2cash\",\"coinA\":\"eth\",\"coinB\":\"btc\",\"midUSD\":\"0.0\",\"mid\":\"332.021\",\"bid\":\"5\",\"ask\":\"6\",\"last_price\":\"332.1136\",\"low\":\"316.72\",\"high\":\"336.0008\",\"volume\":\"605576.4685\",\"timestamp\":\"1510740493123\"}");		
		return td;
	}
}

