package com.zdx.tri;


import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;

public class TriBolt implements IRichBolt{
	private static final Logger logger = LoggerFactory.getLogger(TriBolt.class);
	private static final long serialVersionUID = 1L;
	protected OutputCollector collector;
	
	@Override
	@SuppressWarnings("rawtypes") 
	public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
		this.collector  = collector;
	}

	@Override
	public void execute(Tuple tuple) {		
		logger.debug("------------------TriBolt Begin -----------------");
		logger.debug("key = " + tuple.getValue(0));
		logger.debug("key = " + tuple.getValue(1));
		//String key = tuple.getValue(0).toString();
		//JSONObject value = JSON.parseObject((String) tuple.getValue(1));
	
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

