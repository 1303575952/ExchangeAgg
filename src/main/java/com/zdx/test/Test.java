package com.zdx.test;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.java_websocket.drafts.Draft_6455;

import com.zdx.demo.ToyConsumer;
import com.zdx.pair.ExchangeTopPairs;
import com.zdx.pair.PariResetBolt1;
import com.zdx.producer.TickerProducer;
import com.zdx.rocketmq.WebSocketLocalClient;
import com.zdx.tri.TickerIndexBuilder;

public class Test {
	private static Logger logger = Logger.getLogger(Test.class);
	public static void main(String[] args) {
		//runProducer();
		//testWebSocket();
		
		PariResetBolt1 prb = new PariResetBolt1();
		prb.loadExchangePair("C:\\ZDX\\code\\CoinMarkCapImport\\Exchange\\topVol100M.json");
		prb.buildExchangePath();
	}
	
	public static void testExchangeTopPairs(){
		ExchangeTopPairs etp = new ExchangeTopPairs();
		etp.buildTopVol100MPairFile("C:\\ZDX\\code\\CoinMarkCapImport\\Exchange\\1511079825594\\topVol100M.json");
	}
	
	public static void run1(){
		//String pairPath = "C:\\Users\\zdx\\git\\ExchangeAgg\\conf\\t1.json";
		String triPath = "C:\\Users\\zdx\\git\\ExchangeAgg\\conf\\t2.json";
		String tickerIndexPath = "C:\\Users\\zdx\\git\\ExchangeAgg\\conf\\t3.json";
		//TriListBuilder.buildTriListFromPairFile(pairPath, triPath);
		TickerIndexBuilder tib = new TickerIndexBuilder();
		tib.buildIndexFromFile(triPath);
		tib.saveToFile(tickerIndexPath);
	}
	
	public static void runProducer(){
		String confPath = "C:\\Users\\zdx\\git\\ExchangeAgg\\conf\\TickerProducer.conf";
		try {
			TickerProducer.execute(confPath);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*public static void testWebSocket(){
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
			logger.info(tmpData.get(s));
			wsClient.send(tmpData.get(s));
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}*/
}

