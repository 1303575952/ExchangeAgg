package com.zdx.pair;

import java.util.HashMap;
import java.util.Map;


import org.apache.log4j.Logger;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;

import com.alibaba.jstorm.client.ConfigExtension;
import com.alibaba.jstorm.utils.JStormUtils;
import com.zdx.common.CommonConst;
import com.zdx.common.LoadConfig;


public class PairTopology {
	
	private static Map<Object, Object> conf = new HashMap<Object, Object>();

	private static Logger logger = Logger.getLogger(PairTopology.class);

	public static void main(String[] args) throws Exception {

		if (args.length == 0) {
			System.err.println("Please input configuration file");
			System.exit(-1);
		}
		conf = LoadConfig.loadConf(args[0]);

		TopologyBuilder builder = setupBuilder();

		submitTopology(builder);

	}

	private static TopologyBuilder setupBuilder() throws Exception {
		TopologyBuilder builder = new TopologyBuilder();

		int spoutParallel = JStormUtils.parseInt(
				conf.get("topology.spout.parallel"), 1);
		//int boltParallel = JStormUtils.parseInt(conf.get("topology.bolt.parallel"), 1);

		IRichSpout spout = new PairSpout();
		builder.setSpout("PairSpout", spout, spoutParallel);
		//builder.setBolt("PairBolt1", new PairBolt1(), boltParallel).fieldsGrouping("PairSpout", new Fields("pairArbitrage"));
		//builder.setBolt("PairBolt2", new PairBolt2(), boltParallel).allGrouping("PairBolt1");
		return builder;
	}

	private static void submitTopology(TopologyBuilder builder) {
		try {
			if (localMode(conf)) {
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
			logger.error(e.getMessage(), e.getCause());
		}
	}

	@SuppressWarnings("rawtypes")
	public static boolean localMode(Map conf) {
		String mode = (String) conf.get(Config.STORM_CLUSTER_MODE);
		if (mode != null) {
			if (CommonConst.JSTORM_LOCAL_MODE.equals(mode)) {
				return true;
			}
		}
		return false;
	}

}