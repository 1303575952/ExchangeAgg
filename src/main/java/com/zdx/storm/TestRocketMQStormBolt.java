package com.zdx.storm;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Collections;

import org.apache.commons.collections4.ListUtils;
import org.java_websocket.drafts.Draft_6455;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.jstorm.metric.MetaType;
import com.zdx.common.SortByPrice;
import com.zdx.common.TickerFormat;
import com.zdx.common.TickerPair;
import com.zdx.rocketmq.WebSocketLocalClient;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;

public class TestRocketMQStormBolt implements IRichBolt {

	private static final long serialVersionUID = 2495121976857546346L;
	private static final Logger logger = LoggerFactory.getLogger(TestRocketMQStormBolt.class);
	protected OutputCollector collector;
	java.text.DecimalFormat   df   =new   java.text.DecimalFormat("#.00"); 

	WebSocketLocalClient wsClient = null;

	Map<String, Map<String, TickerFormat>> coinPrices = new HashMap<String,  Map<String, TickerFormat>>();	
	ArrayList<TickerFormat> coinPricesList = new ArrayList<TickerFormat>();
	ArrayList<TickerPair> tickerPairList = new ArrayList<TickerPair>();
	int s = 0;
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


		String tickerType = tuple.getValue(0).toString();
		String tickerInfo = tuple.getValue(1).toString();
		TickerFormat tickerData = new TickerFormat();

		tickerData.formatJsonString(tickerInfo);

		logger.info("put data = " + s + "----" + tickerInfo);
		logger.info("put type = " + s + "----" + tickerType);
		Map<String, TickerFormat> cp = new HashMap<String, TickerFormat>();

		if (coinPrices.containsKey(tickerType) ){
			cp = coinPrices.get(tickerType);
		}

		for (Map.Entry<String, TickerFormat> entry : cp.entrySet()) {
			logger.info("before Key = " + s + "----" + entry.getKey() + ", Value = " + entry.getValue().toJsonString());  
		}		
		cp.put(tickerData.exchangeName, tickerData);
		coinPricesList.clear();
		coinPricesList.addAll(cp.values());
		Collections.sort(coinPricesList, new SortByPrice());
		for (Map.Entry<String, TickerFormat> entry : cp.entrySet()) {
			logger.info("after Key = " + s + "----" + entry.getKey() + ", Value = " + entry.getValue().toJsonString());  
		}
		for (TickerFormat x: coinPricesList ){
			logger.info("after Sorting = " + x.toJsonString() ); 
		}
		coinPrices.put(tickerType, cp);

		tickerPairList.clear();
		TickerPair tp = new TickerPair();
		for (int i1 = 0; i1 < coinPricesList.size(); i1 ++){
			for (int i2 = i1+1; i2 < coinPricesList.size(); i2 ++){
				tp.formatTickerPair(coinPricesList.get(i1), coinPricesList.get(i2));				
				tickerPairList.add(tp);
			}
		}
		
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
		int max=tmpData.size();
		int min=1;
		Random random = new Random();
		s = random.nextInt(max)%(max-min+1) + min;
		
		System.out.println(tmpData.get(s));
		wsClient.send(tmpData.get(s));
		logger.info("Exception11 ==================================================================");
		logger.info("Exception11 ==================================================================");
		System.out.println("Exception11 ==================================================================");
		System.out.println("Exception11 ==================================================================");
		if (this.wsClient != null){
			if (tickerPairList.size() > 1){
				StringBuilder sb = new StringBuilder();
				sb.append("[");
				for (int i1 = 0; i1 < tickerPairList.size(); i1++){
					sb.append(tp.toJsonString());
					if ( (i1 + 1) < tickerPairList.size()){
						sb.append(",");
					}
				}
				sb.append("]");
				String pair = sb.toString();
				System.out.println(pair);
				System.out.println("{\"key\":\"pair\",\"val\":\"" + pair + "\"}");
				
				wsClient.send(pair);
			}

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
		// TODO Auto-generated method stub

	}

	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}
}