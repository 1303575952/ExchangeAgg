package com.zdx.storm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;

import com.alibaba.jstorm.utils.JStormUtils;

/**
 * MonitorTopology
 * 
 * @author longda/zhiyuan.ls
 * 
 */
public class TestStormTopology {

	private static Logger LOG = Logger.getLogger(TestStormTopology.class);

	public static void main(String[] args) throws Exception {
		
		if (args.length == 0) {
			System.err.println("Please input configuration file");
			System.exit(-1);
		}
		LoadConf(args[0]);
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
		IRichSpout spout = new TestRocketMQStormSpout();
		builder.setSpout("TestRocketMQStormSpout", spout, spoutParallel);

		builder.setBolt("TestRocketMQStormBolt", new TestRocketMQStormBolt(), 
				boltParallel).fieldsGrouping("TestRocketMQStormSpout", 
						new Fields("tickerType"));
		builder.setBolt("TestRocketMQStormBolt2", new TestRocketMQStormBolt2(), 
				boltParallel).allGrouping("TestRocketMQStormBolt");
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

	private static Map conf = new HashMap<Object, Object>();

	private static void LoadProperty(String prop) {
		Properties properties = new Properties();

		try {
			InputStream stream = new FileInputStream(prop);
			properties.load(stream);
		} catch (FileNotFoundException e) {
			System.out.println("No such file " + prop);
		} catch (Exception e1) {
			e1.printStackTrace();

			return;
		}

		conf.putAll(properties);
	}

	private static void LoadYaml(String confPath) {

		Yaml yaml = new Yaml();

		try {
			InputStream stream = new FileInputStream(confPath);

			conf = (Map) yaml.load(stream);
			if (conf == null || conf.isEmpty() == true) {
				throw new RuntimeException("Failed to read config file");
			}

		} catch (FileNotFoundException e) {
			System.out.println("No such file " + confPath);
			throw new RuntimeException("No config file");
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new RuntimeException("Failed to read config file");
		}

		return;
	}

	private static void LoadConf(String arg) {
		if (arg.endsWith("yaml")) {
			LoadYaml(arg);
		} else {
			LoadProperty(arg);
		}
	}

	public static boolean local_mode(Map conf) {
		String mode = (String) conf.get(Config.STORM_CLUSTER_MODE);
		if (mode != null) {
			if (mode.equals("local")) {
				return true;
			}
		}

		return false;

	}

}