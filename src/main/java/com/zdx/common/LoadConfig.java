package com.zdx.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.yaml.snakeyaml.Yaml;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.zdx.rocketmq.TickerConfInfo;

public class LoadConfig {
	
	public static TickerConfInfo loadTickerConf(String path){
		TickerConfInfo tconf = new TickerConfInfo();
		HashMap<String, ArrayList<String>>  exchangeTickerListMap = new HashMap<String, ArrayList<String>>();

		HashMap<String, String> exchangeSymbolMap = new HashMap<String, String>();

		HashMap<String, String> pathSymbolMap = new HashMap<String, String>();

		List<String> targetHosts = new ArrayList<String>();

		List<List<String>> replaceLists = new ArrayList<List<String>>();

		String text = FileIO.ReadFile(path);
		if (text.isEmpty()){
			return tconf ;
		}
		ArrayList<String> epStringList = JSON.parseObject(text, new TypeReference<ArrayList<String>>(){});
		for (String e : epStringList){
			ArrayList<String> epList = new ArrayList<String>();
			JSONObject jsonObj = JSON.parseObject(e);
			String exchangeURL = String.valueOf(jsonObj.get("exchangeURL"));
			String exchangeName = String.valueOf(jsonObj.get("exchangeName"));
			targetHosts.add(exchangeURL);
			exchangeSymbolMap.put(exchangeURL, exchangeName);
			String tmp = String.valueOf(jsonObj.get("endpoints"));
			
			if (tmp.contains("[")){
				tmp = tmp.replaceAll("\\[", "");
			}
			if (tmp.contains("]")){
				tmp = tmp.replaceAll("\\]", "");
			}
			String[] tmp2 = tmp.split("}");
			for (String tmp3 : tmp2){
				if (!tmp3.contains("}")){
					tmp3 = tmp3 + "}";
				}
				if (tmp3.contains(",{")){
					tmp3 = tmp3.substring(1);
				}
				JSONObject js = JSON.parseObject(tmp3);

				String pairName =  String.valueOf(js.get("pairName"));
				String pairPath = String.valueOf(js.get("pairPath"));
				pathSymbolMap.put(pairPath, pairName);
				epList.add(pairPath);
			}
			exchangeTickerListMap.put(exchangeURL, epList);
			replaceLists.add(epList);
		}
		tconf.exchangeSymbolMap = exchangeSymbolMap;
		tconf.exchangeTickerListMap = exchangeTickerListMap;
		tconf.pathSymbolMap = pathSymbolMap;
		tconf.targetHosts = targetHosts;
		tconf.replaceLists = replaceLists;

		return tconf;
	}
	
	private static HashMap<Object, Object> LoadProperty(String prop) {
		HashMap<Object, Object> ret = null;
		Properties properties = new Properties();

		try {
			InputStream stream = new FileInputStream(prop);
			properties.load(stream);
			ret = new HashMap<Object, Object>();
			ret.putAll(properties);
		} catch (FileNotFoundException e) {
			System.out.println("No such file " + prop);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	private static HashMap<Object, Object> LoadYaml(String confPath) {
		HashMap<Object, Object> ret = null;
		Yaml yaml = new Yaml();
		try {
			InputStream stream = new FileInputStream(confPath);

			ret = (HashMap<Object, Object>) yaml.load(stream);
			if (ret == null || ret.isEmpty() == true) {
				throw new RuntimeException("Failed to read config file");
			}

		} catch (FileNotFoundException e) {
			System.out.println("No such file " + confPath);
			throw new RuntimeException("No config file");
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new RuntimeException("Failed to read config file");
		}

		return ret;
	}

	public static HashMap<Object, Object> LoadConf(String arg) {
		HashMap<Object, Object> ret = null;

		if (arg.endsWith("yaml")) {
			ret = LoadYaml(arg);
		} else {
			ret = LoadProperty(arg);
		}
		return ret;
	}
}

