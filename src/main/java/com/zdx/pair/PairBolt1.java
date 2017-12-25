package com.zdx.pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zdx.common.DataFormat;
import com.zdx.common.TickerPair;
import com.zdx.common.TickerStandardFormat;
import com.zdx.rocketmq.WebSocketLocalClient;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.FailedException;
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
	//InfluxDB influxDB = null;
	String influxURL = "";
	String influxDbName = "";
	String influxRpName = "";

	public PairBolt1(){


	}

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map conf, TopologyContext context,
			OutputCollector collector) {

		logger.debug("===========================PairBolt1 prepare Begin=======================================");
		influxURL = (String) conf.get("InfluxDBURL");
		influxDbName = (String) conf.get("InfluxDbName");
		influxRpName = (String) conf.get("InfluxRpName");
		//influxDB = getInfluxDB();

		//this.influxDB.createRetentionPolicy(influxRpName, influxDbName, "30d", "30m", 2, true);
		this.collector = collector;
		logger.debug("===========================PairBolt1 prepare End=======================================");
	}

	//  public InfluxDB getInfluxDB() {
	//influxDB = InfluxDBFactory.connect(influxURL);
	//if (!influxDB.databaseExists(influxDbName)){
	//	influxDB.createDatabase(influxDbName);
	//}
	// return influxDB;
	// }

	@Override
	public void execute(Tuple tuple) {
		try{
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
			String tableName = DataFormat.removeShortTerm(ep.sellExchangeName) + "_" + DataFormat.removeShortTerm(ep.buyExchangeName);
			Point point1 = Point.measurement(tableName)
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
			logger.debug("===========================Point-1=======================================");
			logger.debug("   =" + point1.toString());
			logger.debug("===========================Point-1=======================================");
			logger.debug("dbName = " + influxDbName);
			logger.debug("rpName = " + influxRpName);
			logger.debug("point1 = " + point1.toString());
			/*12241723*/
			/*influxDB.write(influxDbName, influxRpName, point1);
			Query query = new Query("SELECT * FROM " + tableName + " GROUP BY *", influxDbName);
			QueryResult result = this.influxDB.query(query);
			if (result.getResults().get(0).getSeries().get(0).getTags().isEmpty() == true){
				logger.debug("===========================InfluxDB Insert Failed=======================================");
				influxDB.close();
				influxDB = getInfluxDB();
			}*/
			collector.ack(tuple);
			logger.debug("===========================PairBolt1 End=======================================");
		}catch(FailedException e){
			logger.debug("===========================PairBolt1 failed=======================================");
			collector.fail(tuple);
		}
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