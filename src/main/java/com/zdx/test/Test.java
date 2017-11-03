package com.zdx.test;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.java_websocket.drafts.Draft_6455;

import com.zdx.rocketmq.WebSocketLocalClient;
import com.zdx.storm.AggConfig;

public class Test {
	public static void main(String[] args) {
		testWebSocket();
	}
	public static void testWebSocket(){
		WebSocketLocalClient wsClient = null;
		try {
			wsClient = new WebSocketLocalClient( new URI( "ws://" + AggConfig.webSocketServerIP + ":" + AggConfig.webSocketServerPort), new Draft_6455() );
			wsClient.connectBlocking();
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(true){
			ArrayList<String> tmpData = new ArrayList<String>();
			String s1 = "[{\"mid\":\"0.01\",\"bid\":\"0.05\",\"ask\":\"0.039627\",\"last_price\":\"0.039626\",\"low\":\"0.037625\",\"high\":\"0.04622\",\"volume\":\"202898.86695984\",\"timestamp\":\"1509630586.0410173\"}]";
			tmpData.add(s1);
			String s2 = "[{\"mid\":\"0.02\",\"bid\":\"0.04\",\"ask\":\"0.039627\",\"last_price\":\"0.039626\",\"low\":\"0.037625\",\"high\":\"0.04622\",\"volume\":\"202898.86695984\",\"timestamp\":\"1509630586.0410173\"}]";
			tmpData.add(s2);
			String s3 = "[{\"mid\":\"0.03\",\"bid\":\"0.03\",\"ask\":\"0.039627\",\"last_price\":\"0.039626\",\"low\":\"0.037625\",\"high\":\"0.04622\",\"volume\":\"202898.86695984\",\"timestamp\":\"1509630586.0410173\"}]";
			tmpData.add(s3);
			String s4 = "[{\"mid\":\"0.04\",\"bid\":\"0.02\",\"ask\":\"0.039627\",\"last_price\":\"0.039626\",\"low\":\"0.037625\",\"high\":\"0.04622\",\"volume\":\"202898.86695984\",\"timestamp\":\"1509630586.0410173\"}]";
			tmpData.add(s4);
			String s5 = "[{\"mid\":\"0.05\",\"bid\":\"0.01\",\"ask\":\"0.039627\",\"last_price\":\"0.039626\",\"low\":\"0.037625\",\"high\":\"0.04622\",\"volume\":\"202898.86695984\",\"timestamp\":\"1509630586.0410173\"}]";
			tmpData.add(s5);
			int max=tmpData.size()-1;
			int min=0;
			Random random = new Random();
			int s = random.nextInt(max)%(max-min+1) + min;
			System.out.println(tmpData.get(s));
			wsClient.send(tmpData.get(s));
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}

