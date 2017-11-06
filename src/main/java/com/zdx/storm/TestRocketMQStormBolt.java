package com.zdx.storm;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Collections;

import org.apache.commons.collections4.ListUtils;
import org.java_websocket.drafts.Draft_6455;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.jstorm.esotericsoftware.minlog.Log;
import com.alibaba.jstorm.metric.MetaType;
import com.zdx.common.SortByPrice;
import com.zdx.common.TickerFormat;
import com.zdx.common.TickerPair;
import com.zdx.rocketmq.WebSocketLocalClient;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class TestRocketMQStormBolt implements IRichBolt {

	private static final long serialVersionUID = 2495121976857546346L;
	private static final Logger logger = LoggerFactory.getLogger(TestRocketMQStormBolt.class);
	protected OutputCollector collector;
	java.text.DecimalFormat   df   =new   java.text.DecimalFormat("#.00"); 

	WebSocketLocalClient wsClient = null;

	Map<String, Map<String, TickerFormat>> coinPrices = new HashMap<String,  Map<String, TickerFormat>>();	
	ArrayList<TickerFormat> coinPricesList = new ArrayList<TickerFormat>();
	ArrayList<TickerPair> tickerPairList = new ArrayList<TickerPair>();
	
	public TestRocketMQStormBolt(){


	}

	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {


		WebSocketLocalClient wsClient = null;
		try {
			wsClient = new WebSocketLocalClient( new URI( "ws://" + AggConfig.webSocketServerIP + ":" + AggConfig.webSocketServerPort), new Draft_6455() );
			wsClient.connectBlocking();
			this.wsClient = wsClient;
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.collector = collector;
	}

	@SuppressWarnings({ "null", "unchecked" })
	public void execute(Tuple tuple) {
		// TODO Auto-generated method stub
		//MetaType metaTuple = (MetaType)tuple.getValue(0);
		System.out.println(tuple.getValue(0).toString());
		System.out.println(tuple.getValue(1).toString());
		System.out.println("Exception11 ==================================================================");
		System.out.println("Exception11 ==================================================================");
		System.out.println("Exception11 ==================================================================");
		logger.info("Exception11 ==================================================================");
		logger.info("Exception11 ==================================================================");

		int max=5;
		int min=1;
		Random random = new Random();
		int s1 = random.nextInt(max)%(max-min+1);
		
		
		String tickerType = tuple.getValue(0).toString();
		String tickerInfo = tuple.getValue(1).toString();
		TickerFormat tickerData = new TickerFormat();

		tickerData.formatJsonString(tickerInfo);

		logger.info("put data = " + s1 + "----" + tickerInfo);
		logger.info("put type = " + s1 + "----" + tickerType);
		/*
		 * 打印每个coinPrices中key对应数据，例
		 * btc_usd	{}
		 * 			{}
		 * eth_btc	{}
		 * 			{}
		 * 			{}
		 * */
		for (Entry<String, Map<String, TickerFormat>> entry : coinPrices.entrySet()) {
			logger.info("coinPrices before merge = " + s1 + "----" + entry.getKey());  
			for (Map.Entry<String, TickerFormat> entry2 : entry.getValue().entrySet()) {
				logger.info("coinPrices content= " + s1 + "----" + entry2.getKey() + ", Value = " + entry2.getValue().toJsonString());  
			}
		}
		/*
		 * cp拿到coinPrices中所有tickerType对应的数据，例
		 * {}
		 * {}
		 * {}
		 * */
		Map<String, TickerFormat> cp = new HashMap<String, TickerFormat>();
		if (coinPrices.containsKey(tickerType) ){
			cp = coinPrices.get(tickerType);
		}
		/*
		 * 打印tickertType对应的所有交易所数据
		 */
		for (Map.Entry<String, TickerFormat> entry : cp.entrySet()) {
			logger.info("CP before put Key = " + s1 + "----" + entry.getKey() + ", Value = " + entry.getValue().toJsonString());  
		}
		/*
		 * 新的tickerType数据对应的tickerData放入（更新）cp
		 * */
		cp.put(tickerData.exchangeName, tickerData);
		/*
		 * 打印更新后的cp数据
		 * */
		for (Map.Entry<String, TickerFormat> entry : cp.entrySet()) {
			logger.info("CP after put Key = " + s1 + "----" + entry.getKey() + ", Value = " + entry.getValue().toJsonString());  
		}
		/*
		 * coinPricesList clear 前
		 * */
		for (TickerFormat x: coinPricesList ){
			logger.info("before clear = "+ s1 + "----" + x.toJsonString() ); 
		}
		coinPricesList.clear();
		/*
		 * coinPricesList clear 后
		 * */
		for (TickerFormat x: coinPricesList ){
			logger.info("after clear = "+ s1 + "----" + x.toJsonString() ); 
		}
		/*
		 * coinPricesList放入cp的所有value
		 * */
		coinPricesList.addAll(cp.values());
		/*coinPricesList更新后数据*/
		for (TickerFormat x: coinPricesList ){
			logger.info("after add all= "+ s1 + "----" + x.toJsonString() ); 
		}
		Collections.sort(coinPricesList, new SortByPrice());
		for (TickerFormat x: coinPricesList ){
			logger.info("after sort= "+ s1 + "----" + x.toJsonString() ); 
		}
		for (Map.Entry<String, TickerFormat> entry : cp.entrySet()) {
			logger.info("after Key = " + s1 + "----" + entry.getKey() + ", Value = " + entry.getValue().toJsonString());  
		}
		
		coinPrices.put(tickerType, cp);
		for (Entry<String, Map<String, TickerFormat>> entry : coinPrices.entrySet()) {
			logger.info("coinPrices after put = " + s1 + "----" + entry.getKey());  
			for (Map.Entry<String, TickerFormat> entry2 : entry.getValue().entrySet()) {
				logger.info("coinPrices content= " + s1 + "----" + entry2.getKey() + ", Value = " + entry2.getValue().toJsonString());  
			}
		}
		for (TickerPair x: tickerPairList){
			logger.info("tickerPairList before clear = "+ s1 + "----" + x.toJsonString() ); 
		}
		tickerPairList.clear();
		for (TickerPair x: tickerPairList){
			logger.info("tickerPairList after clear = "+ s1 + "----" + x.toJsonString() ); 
		}
		for (TickerFormat x: coinPricesList ){
			logger.info("coinPricesList before compute pair = "+ s1 + "----" + x.toJsonString() ); 
		}
		
		if(coinPricesList.size() > 1){
			for (int i1 = 0; i1 < coinPricesList.size(); i1 ++){
				for (int i2 = i1+1; i2 < coinPricesList.size(); i2 ++){
					TickerPair tp = new TickerPair();
					tp.formatTickerPair(coinPricesList.get(i1), coinPricesList.get(i2));
					System.out.println("111tickerPairList size is:"+tickerPairList.size());
					System.out.println("tptptptptp:"+tp.toJsonString());
					tickerPairList.add(tp);
					System.out.println("222tickerPairList size is:"+tickerPairList.size());
					for(int i4 = 0;i4<tickerPairList.size();i4++){
						System.out.println("after added,tickerPairList "+"i4"+tickerPairList.get(i4).toJsonString());
					}
					
					//这里的tickerPairList数据被覆盖！！！！！
				}
			}
			//下面这里的tickerPairList怎么就不一样了？？每一个和最后一个add进来的一样
			for(int i3 = 0;i3<tickerPairList.size();i3++){
				System.out.println("tickerPairList"+"&&&&&"+i3+"\t"+tickerPairList.get(i3).toJsonString());
			}
		}
		for (TickerPair x: tickerPairList){
			logger.info("tickerPairList after add pair= "+ s1 + "----" + x.toJsonString() ); 
		}

		/*ArrayList<String> tmpData = new ArrayList<String>();
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
		int max=tmpData.size();
		int min=1;
		Random random = new Random();
		s = random.nextInt(max)%(max-min+1);
		System.out.println("sssssssssssssssssssssssssssss=============================="+s);
		System.out.println(tmpData.get(s));
		wsClient.send(tmpData.get(s));*/
		logger.info("Exception11 ==================================================================");
		logger.info("Exception11 ==================================================================");
		System.out.println("Exception11 ==================================================================");
		System.out.println("Exception11 ==================================================================");
		System.out.println("tickerPairList size is:"+tickerPairList.size());
		if (this.wsClient != null){
			if (tickerPairList.size() >= 1){
				StringBuilder sb = new StringBuilder();
				/*sb.append("[");
				for (int i1 = 0; i1 < tickerPairList.size(); i1++){
					sb.append(tp.toJsonString());
					if ( (i1 + 1) < tickerPairList.size()){
						sb.append(",");
					}
				}
				sb.append("]");*/
				sb.append("{\"data\":[");
				for(int i1 = 0;i1 < tickerPairList.size();i1++){
					sb.append(tickerPairList.get(i1).toJsonString());
					if((i1+1)<tickerPairList.size()){
						sb.append(",");
					}
				}
				sb.append("]}");
				String pair = sb.toString();
				System.out.println(pair);
				collector.emit(new Values(tickerType,pair));
				Log.info("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
				
				wsClient.send(pair);
				}
		}else{
			System.out.println("wsClient is null");
		}


		/*try {

			//logger.info("Messages:" + metaTuple.toString());
		} catch (Exception e) {
			collector.fail(tuple);
			return ;
			//throw new FailedException(e);
		}*/
		collector.ack(tuple);
	}

	public void cleanup() {
		// TODO Auto-generated method stub
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("tickerType","pair"));

	}

	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}
}