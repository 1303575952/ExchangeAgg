package com.zdx.pair;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Collections;

import org.java_websocket.drafts.Draft_6455;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.jstorm.esotericsoftware.minlog.Log;
import com.zdx.common.SortByPrice;
import com.zdx.common.TickerPair;
import com.zdx.common.TickerStandardFormat;
import com.zdx.rocketmq.WebSocketLocalClient;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class PairBolt1 implements IRichBolt {

	private static final long serialVersionUID = 2495121976857546346L;
	private static final Logger logger = LoggerFactory.getLogger(PairBolt1.class);
	protected OutputCollector collector;
	java.text.DecimalFormat  df  = new java.text.DecimalFormat("#.00"); 

	WebSocketLocalClient wsClient = null;

	Map<String, Map<String, TickerStandardFormat>> coinPrices = new HashMap<String,  Map<String, TickerStandardFormat>>();	
	ArrayList<TickerStandardFormat> coinPricesList = new ArrayList<TickerStandardFormat>();
	ArrayList<TickerPair> tickerPairList = new ArrayList<TickerPair>();
	
	HashMap<String, Boolean> pairSendMap = new HashMap<String, Boolean>(); 
	public PairBolt1(){


	}

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map conf, TopologyContext context,
			OutputCollector collector) {
		
		//WebSocketLocalClient wsClient = null;
		/*try {
			wsClient = new WebSocketLocalClient( new URI( "ws://" + (String)conf.get("GatewayURL")), new Draft_6455() );
			wsClient.connectBlocking();
			this.wsClient = wsClient;
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		this.collector = collector;
	}
	
	@Override
	public void execute(Tuple tuple) {
		// TODO Auto-generated method stub
		//MetaType metaTuple = (MetaType)tuple.getValue(0);
		logger.debug("===========================PairBolt1 Begin=======================================");
		logger.debug("PairBolt1 Key = " + tuple.getValue(0).toString());
		logger.debug("PairBolt1 Val = " + tuple.getValue(1).toString());
		String pairOpport = tuple.getValue(0).toString();
		EnterPrice ep = new EnterPrice(tuple.getValue(1).toString());
		if (ep.priceDiff > 0.2){
			//介入交易
			logger.debug("PairBolt1 Opportunity Window Open: " + ep.toJsonString());
			collector.emit(new Values(pairOpport, ep));			
			if (!pairSendMap.containsKey(pairOpport) || !pairSendMap.get(pairOpport)){
				pairSendMap.put(pairOpport, true);
			}
		} 
		if (ep.priceDiff < 0.1 && pairSendMap.containsKey(pairOpport) && pairSendMap.get(pairOpport)){
			//退出交易
			logger.debug("PairBolt1 Opportunity Window Colse: " + ep.toJsonString());
			collector.emit(new Values(pairOpport, ep));	
			pairSendMap.put(pairOpport, false);
		}
		logger.debug("===========================PairBolt1 End=======================================");
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		//declarer.declare(new Fields("tickerType","pair"));

	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}
}