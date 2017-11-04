package com.zdx.storm;

import java.util.HashMap;
import java.util.Map;

import com.zdx.common.TickerFormat;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;

public class TestRocketMQStormBolt2 implements IRichBolt{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected OutputCollector collector;
	
	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector  = collector;
		
	}

	@Override
	public void execute(Tuple tuple) {
		System.out.println("Exception22 111111111111111111111111111111111111111111111111111111111111111111");
		System.out.println("Exception22 111111111111111111111111111111111111111111111111111111111111111111");
		System.out.println("Exception22 111111111111111111111111111111111111111111111111111111111111111111");
		System.out.println(tuple);
		
		System.out.println("Exception22 222222222222222222222222222222222222222222222222222222222222222222");
		System.out.println("Exception22 222222222222222222222222222222222222222222222222222222222222222222");
		System.out.println("Exception22 222222222222222222222222222222222222222222222222222222222222222222");
		
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
