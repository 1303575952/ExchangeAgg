package com.zdx.tri;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;

import com.alibaba.jstorm.utils.JStormUtils;
import com.zdx.common.LoadConfig;


public class TriTopology {

	private static Logger LOG = Logger.getLogger(TriTopology.class);
	private static Map<Object, Object> conf = new HashMap<Object, Object>();
	
	public static void main(String[] args) throws Exception {
		
		if (args.length == 0) {
			System.err.println("Please input configuration file");
			System.exit(-1);
		}
		conf = LoadConfig.LoadConf(args[0]);
		/*
		URL url = TestStormTopology.class.getClassLoader().getResource("stormtest.yaml");
		System.out.println(url.getFile());
		LoadConf(url.getFile());
		*/
		TopologyBuilder builder = setupBuilder();

		submitTopology(builder);

	}

	private static TopologyBuilder setupBuilder() throws Exception {
		TopologyBuilder builder = new TopologyBuilder();

		int boltParallel = JStormUtils.parseInt(
				conf.get("topology.bolt.parallel"), 1);

		int spoutParallel = JStormUtils.parseInt(
				conf.get("topology.spout.parallel"), 1);
		IRichSpout spout = new TriSpout();
		builder.setSpout("TriSpout", spout, spoutParallel);
		builder.setBolt("TriBolt", new TriBolt(), 
				boltParallel).fieldsGrouping("TriSpout", 
						new Fields("exchangeName"));
		System.out.println("topologybuilder");
		return builder;
	}

	private static void submitTopology(TopologyBuilder builder) {
		try {
			if (local_mode(conf)) {

				LocalCluster cluster = new LocalCluster();

				cluster.submitTopology(
						String.valueOf(conf.get("topology.name")), conf,
						builder.createTopology());

				Thread.sleep(200000);

				cluster.shutdown();
			} else {
				StormSubmitter.submitTopology(
						String.valueOf(conf.get("topology.name")), conf,
						builder.createTopology());
			}

		} catch (Exception e) {
			LOG.error(e.getMessage(), e.getCause());
		}
	}

	

	

	public static boolean local_mode(Map<Object, Object> conf) {
		String mode = (String) conf.get(Config.STORM_CLUSTER_MODE);
		if (mode != null) {
			if (mode.equals("local")) {
				return true;
			}
		}
		return false;

	}
	
	

}