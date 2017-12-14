package com.zdx.test;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.java_websocket.drafts.Draft_6455;

import com.zdx.common.TickerStandardFormat;
import com.zdx.demo.ToyConsumer;
import com.zdx.pair.ExchangeTopPairs;
import com.zdx.pair.PariConfig;
import com.zdx.producer.TickerProducer;
import com.zdx.rocketmq.WebSocketLocalClient;
import com.zdx.tri.TickerIndexBuilder;

public class Test {
	private static Logger logger = Logger.getLogger(Test.class);
	public static void main(String[] args) throws InterruptedException {
		//testWebSocket();
		
		//PariConfig prb = new PariConfig();
		/*PairBolt pb = new PairBolt();
		pb.open();
		TickerStandardFormat tsf = new TickerStandardFormat();
		String ticker0 = "{\"exchangeName\":\"livecoin\",\"exchangeType\":\"coin2cash\",\"coinA\":\"btc\",\"coinB\":\"usd\",\"midUSD\":\"0.0\",\"mid\":\"7825.89805\",\"bid\":\"100.0\",\"ask\":\"200.0\",\"last_price\":\"7845.6886\",\"low\":\"7564.33\",\"high\":\"7860.8157\",\"volume\":\"238061.3136\",\"timestamp\":\"1\"}";
		pb.updatePairPrice(tsf.formatJsonString(ticker0));
		String ticker1 = "{\"exchangeName\":\"bitstamp\",\"exchangeType\":\"coin2cash\",\"coinA\":\"btc\",\"coinB\":\"eur\",\"midUSD\":\"0.0\",\"mid\":\"7825.89805\",\"bid\":\"250.0\",\"ask\":\"350.0\",\"last_price\":\"7845.6886\",\"low\":\"7564.33\",\"high\":\"7860.8157\",\"volume\":\"238061.3136\",\"timestamp\":\"1\"}";
		pb.updatePairPrice(tsf.formatJsonString(ticker1));
		String ticker2 = "{\"exchangeName\":\"bitstamp\",\"exchangeType\":\"coin2cash\",\"coinA\":\"btc\",\"coinB\":\"usd\",\"midUSD\":\"0.0\",\"mid\":\"7825.89805\",\"bid\":\"150.0\",\"ask\":\"250.0\",\"last_price\":\"7845.6886\",\"low\":\"7564.33\",\"high\":\"7860.8157\",\"volume\":\"238061.3136\",\"timestamp\":\"1\"}";
		pb.updatePairPrice(tsf.formatJsonString(ticker2));*/
		/*
		prb.loadExchangePair("C:\\Users\\zdx\\git\\ExchangeAgg\\conf\\topVol100M.json");
		prb.buildExchangePath();
		
		
		String ticker2 = "{\"exchangeName\":\"gdax\",\"exchangeType\":\"coin2cash\",\"coinA\":\"btc\",\"coinB\":\"usd\",\"midUSD\":\"0.0\",\"mid\":\"7825.89805\",\"bid\":\"300.0\",\"ask\":\"400.0\",\"last_price\":\"7845.6886\",\"low\":\"7564.33\",\"high\":\"7860.8157\",\"volume\":\"238061.3136\",\"timestamp\":\"1\"}";
		String ticker3 = "{\"exchangeName\":\"gdax\",\"exchangeType\":\"coin2cash\",\"coinA\":\"btc\",\"coinB\":\"usd\",\"midUSD\":\"0.0\",\"mid\":\"7825.89805\",\"bid\":\"500.0\",\"ask\":\"600.0\",\"last_price\":\"7845.6886\",\"low\":\"7564.33\",\"high\":\"7860.8157\",\"volume\":\"238061.3136\",\"timestamp\":\"1\"}";
		String ticker4 = "{\"exchangeName\":\"livecoin\",\"exchangeType\":\"coin2coin\",\"coinA\":\"btc\",\"coinB\":\"etc\",\"midUSD\":\"0.0\",\"mid\":\"7825.89805\",\"bid\":\"10.0\",\"ask\":\"20.0\",\"last_price\":\"7845.6886\",\"low\":\"7564.33\",\"high\":\"7860.8157\",\"volume\":\"238061.3136\",\"timestamp\":\"1\"}";
		String ticker5 = "{\"exchangeName\":\"livecoin\",\"exchangeType\":\"coin2coin\",\"coinA\":\"etc\",\"coinB\":\"usd\",\"midUSD\":\"0.0\",\"mid\":\"7825.89805\",\"bid\":\"10.0\",\"ask\":\"20.0\",\"last_price\":\"7845.6886\",\"low\":\"7564.33\",\"high\":\"7860.8157\",\"volume\":\"238061.3136\",\"timestamp\":\"1\"}";
		String ticker6 = "{\"exchangeName\":\"gdax\",\"exchangeType\":\"coin2cash\",\"coinA\":\"usd\",\"coinB\":\"etc\",\"midUSD\":\"0.0\",\"mid\":\"7825.89805\",\"bid\":\"0.1\",\"ask\":\"0.2\",\"last_price\":\"7845.6886\",\"low\":\"7564.33\",\"high\":\"7860.8157\",\"volume\":\"238061.3136\",\"timestamp\":\"1\"}";
		String ticker7 = "{\"exchangeName\":\"gdax\",\"exchangeType\":\"coin2cash\",\"coinA\":\"usd\",\"coinB\":\"etc\",\"midUSD\":\"0.0\",\"mid\":\"7825.89805\",\"bid\":\"0.1\",\"ask\":\"0.2\",\"last_price\":\"7845.6886\",\"low\":\"7564.33\",\"high\":\"7860.8157\",\"volume\":\"238061.3136\",\"timestamp\":\"1\"}";
		TickerStandardFormat tsf0 = new TickerStandardFormat();
		TickerStandardFormat tsf1 = new TickerStandardFormat();
		TickerStandardFormat tsf2 = new TickerStandardFormat();
		TickerStandardFormat tsf3 = new TickerStandardFormat();
		TickerStandardFormat tsf4 = new TickerStandardFormat();
		TickerStandardFormat tsf5 = new TickerStandardFormat();
		TickerStandardFormat tsf6 = new TickerStandardFormat();
		TickerStandardFormat tsf7 = new TickerStandardFormat();
		prb.updatePrice(tsf0.formatJsonString(ticker0));
		prb.updatePrice(tsf1.formatJsonString(ticker1));
		prb.updatePrice(tsf2.formatJsonString(ticker2));
		prb.updatePrice(tsf3.formatJsonString(ticker3));
		prb.updatePrice(tsf4.formatJsonString(ticker4));
		prb.updatePrice(tsf5.formatJsonString(ticker5));
		prb.updatePrice(tsf6.formatJsonString(ticker6));
		prb.updatePrice(tsf7.formatJsonString(ticker7));
		*/
		
		
	}
	
	public static void testExchangeTopPairs(){
		ExchangeTopPairs etp = new ExchangeTopPairs();
		etp.buildTopVol100MPairFile("C:\\ZDX\\code\\ExchangeAgg\\conf\\topVol100M.json");
	}
	
	public static void run1(){
		//String pairPath = "C:\\Users\\zdx\\git\\ExchangeAgg\\conf\\t1.json";
		String triPath = "C:\\ZDX\\code\\ExchangeAgg\\conf\\t2.json";
		String tickerIndexPath = "C:\\ZDX\\code\\ExchangeAgg\\conf\\t3.json";
		//TriListBuilder.buildTriListFromPairFile(pairPath, triPath);
		TickerIndexBuilder tib = new TickerIndexBuilder();
		tib.buildIndexFromFile(triPath);
		tib.saveToFile(tickerIndexPath);
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

