package com.zdx.pair;

import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import com.zdx.common.FileIO;

public class PairStormConf {
	private static final Logger logger = LoggerFactory.getLogger(PairStormConf.class);

	public HashMap<Object, Object> loadConfigFromFile(String confFilePath){
		String fileContent = FileIO.readFile(confFilePath);
		if (fileContent.isEmpty()){
			logger.error("Pair Consumer Config File Is Empty, Please check it. confFilePath=" +confFilePath );
		}
		HashMap<Object, Object> conf = new HashMap<Object, Object> ();
		JSONObject j1 = JSON.parseObject(fileContent);
		String stormData = j1.getString("StormData");
		ArrayList<String> stormDataList = JSON.parseObject(stormData, new TypeReference<ArrayList<String>>(){});
		for (String e : stormDataList){
			JSONObject j2 = JSON.parseObject(e);
			conf.put("topology.name", j2.get("topology.name"));
			conf.put("storm.cluster.mode", j2.get("storm.cluster.mode"));
			conf.put("topology.bolt.parallel", j2.get("topology.bolt.parallel"));
			conf.put("topology.spout.parallel", j2.get("topology.spout.parallel"));
			conf.put("topology.optimize", j2.get("topology.optimize"));
		}
		conf.put("SpoutData", j1.get("SpoutData"));
		return conf;
	}


}