package com.zdx.producer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.zdx.common.FileIO;

import io.parallec.core.RequestProtocol;

public class TickerProducerConf {
	private static Logger logger = Logger.getLogger(TickerProducerConf.class);
	public static HashMap<String, ArrayList<String>>  exchangeTickerListMap = new HashMap<String, ArrayList<String>>();

	public static HashMap<String, String> exchangeSymbolMap = new HashMap<String, String>();

	public static HashMap<String, String> pathSymbolMap = new HashMap<String, String>();

	public static List<String> targetHosts = new ArrayList<String>();

	public static List<List<String>> replaceLists = new ArrayList<List<String>>();

	public static ArrayList<String> tickerNames = new ArrayList<String>();

	public static String confFilePath = "";

	public static int validInterval = Integer.MAX_VALUE;
	public static String serverUrl = "";
	public static String producerGroup = "";

	public static String influxURL = "";
	public static String influxDbName = "";
	public static String influxRpName = "";
	public static RequestProtocol httpProtocol = RequestProtocol.HTTPS;
	public static int httpPort = 443;

	public TickerProducerConf(){

	}

	public static void loadConfigFromFile(String confFilePath){
		String fileContent = FileIO.readFile(confFilePath);
		if (fileContent.isEmpty()){
			logger.error("Producer Config File Is Empty, Please check it. confFilePath=" +confFilePath );
		}
		JSONObject j1 = JSON.parseObject(fileContent);
		serverUrl = String.valueOf(j1.get("GatewayURL"));
		validInterval = Integer.valueOf(j1.getString("ValidInterval"));
		producerGroup = String.valueOf(j1.get("ProducerGroup"));
		influxURL = String.valueOf(j1.get("InfluxDBURL"));
		influxDbName = String.valueOf(j1.get("InfluxDbName"));
		influxRpName = String.valueOf(j1.get("InfluxRpName"));
		if (j1.containsKey("HttpProtocol") && j1.getString("HttpProtocol").toLowerCase().equals("http")){
			httpProtocol = RequestProtocol.HTTP;
		} else if (j1.containsKey("HttpProtocol") && j1.getString("HttpProtocol").toLowerCase().equals("https")){
			httpProtocol = RequestProtocol.HTTPS;
		}
		if (j1.containsKey("HttpPort")){
			httpPort = j1.getIntValue("HttpPort");
		}

		String tickerConf = String.valueOf(j1.get("TickerConf"));
		ArrayList<String> exchangeTickerList = JSON.parseObject(tickerConf, new TypeReference<ArrayList<String>>(){});
		for (String e : exchangeTickerList){
			ArrayList<String> epList = new ArrayList<String>();
			JSONObject j2 = JSON.parseObject(e);
			String exchangeURL = String.valueOf(j2.get("exchangeURL"));
			String exchangeName = String.valueOf(j2.get("exchangeName"));
			targetHosts.add(exchangeURL);
			exchangeSymbolMap.put(exchangeURL, exchangeName);
			String tickerData = String.valueOf(j2.get("endpoints"));
			ArrayList<String> exchangeTickerList2 = JSON.parseObject(tickerData, new TypeReference<ArrayList<String>>(){});
			for (String e2 : exchangeTickerList2){
				JSONObject j3 = JSON.parseObject(e2);
				String pairPath = String.valueOf(j3.get("pairPath"));
				String pairName = String.valueOf(j3.get("pairName"));
				pathSymbolMap.put(pairPath, pairName);
				epList.add(pairPath);
			}
			exchangeTickerListMap.put(exchangeURL, epList);
			replaceLists.add(epList);			
		}
		for (int i = 0; i < targetHosts.size(); i ++){
			String host = targetHosts.get(i);
			List<String> urls = replaceLists.get(i);
			for (String url : urls){
				tickerNames.add(host + "/" + url);
			}
		}
		logger.info(toLogString());
	}

	public static String toLogString(){

		String s1 = exchangeTickerListMap.toString();

		String s2 = exchangeSymbolMap.toString();

		String s3 = pathSymbolMap.toString();

		String s4 = targetHosts.toString();

		String s5 = replaceLists.toString();

		return "confFilePath = " + confFilePath
				+ "producerGroup = " + producerGroup + "\n"
				+ "GatewayURL = " + serverUrl + "\n"
				+ "influxURL = " + influxURL + "\n"
				+ "influxDbName = " + influxDbName + "\n"
				+ "influxRpName = " + influxRpName + "\n"
				+ "httpProtocol = " + httpProtocol.toString() + "\n"
				+ "httpPort = " + httpPort + "\n"
				+ s1 + "\n" + s2 + "\n" + s3 + "\n" + s4 + "\n" + s5;
	}
}
