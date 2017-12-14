package com.zdx.pair;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zdx.common.TickerFormat;
import com.zdx.demo.ToyConsumer;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;

public class PairBolt2 implements IRichBolt{

	/**
	 * 
	 */
	private static Logger logger = Logger.getLogger(PairBolt2.class);
	private static final long serialVersionUID = 1L;
	protected OutputCollector collector;
	Map<String, Object> coin2coinORcoin2cashDetail = new HashMap<String,Object>();
	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector  = collector;

	}

	@Override
	public void execute(Tuple tuple) {
		logger.info("===========================PairBolt2 Begin=======================================");
		logger.info(tuple.getValue(0));
		logger.info(tuple.getValue(1));
		String pairOpport = tuple.getValue(0).toString();
		EnterPrice ep = new EnterPrice(tuple.getValue(1).toString());
		if ("open".equals(ep.tradeFlag)){
			logger.info("PairBolt2 Opportunity Window Open: " + ep.toJsonString());
		} else if ("close".equals(ep.tradeFlag)){
			logger.info("PairBolt2 Opportunity Window Colse: " + ep.toJsonString());
		}
		logger.info("===========================PairBolt2 End=======================================");


		collector.ack(tuple);
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

}
