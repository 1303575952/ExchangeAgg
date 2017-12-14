package com.zdx.pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Point.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	InfluxDB influxDB = null;
	String dbName = "pair";
	String rpName = "aRetentionPolicy";
	public PairBolt1(){


	}

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map conf, TopologyContext context,
			OutputCollector collector) {
		influxDB = InfluxDBFactory.connect("http://182.92.150.57:8086");

		//influxDB.createDatabase(dbName);

		influxDB.createRetentionPolicy(rpName, dbName, "30d", "30m", 2, true);
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
		logger.debug("PairBolt1 Key = " + pairOpport.replace("@@", "__"));
		EnterPrice ep = new EnterPrice(tuple.getValue(1).toString());
		int status = 0;
		if ((ep.priceDiff > 0.2) & (!pairSendMap.containsKey(pairOpport) || !pairSendMap.get(pairOpport))){
			//介入交易
			status = 1;
			pairSendMap.put(pairOpport, true);
			ep.tradeFlag = "open";
			collector.emit(new Values(pairOpport, ep.toJsonString()));
		}  else	if (ep.priceDiff < 0.1 && pairSendMap.containsKey(pairOpport) && pairSendMap.get(pairOpport)){
			//退出交易
			status = -1;
			pairSendMap.put(pairOpport, false);
			ep.tradeFlag = "close";
			collector.emit(new Values(pairOpport, ep.toJsonString()));
		}
		Point point1 = Point.measurement(ep.sellExchangeName + "_" + ep.buyExchangeName)
				.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
				.addField("sellExchangeName", ep.sellExchangeName)	
				.tag("sellPath", ep.sellPath)
				.addField("sellPrice", ep.bid1)
				.addField("buyExchangeName", ep.buyExchangeName)
				.tag("buyPath", ep.buyPath)
				.addField("buyPrice", ep.ask2)
				.addField("priceDiff", ep.priceDiff)
				.addField("status", status)
				
				.build();
		
		logger.debug("dbName = " + dbName);
		logger.debug("rpName = " + rpName);
		logger.debug("point1 = " + point1.toString());
		influxDB.write(dbName, rpName, point1);
		collector.ack(tuple);
		logger.debug("===========================PairBolt1 End=======================================");
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("pairArbitrage","pair"));

	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}
}