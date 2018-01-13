package com.zdx.tri;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;


public class TriSpoutConf {
	private static final Logger logger = LoggerFactory.getLogger(TriSpoutConf.class);

	private static ArrayList<ExchangePairsInfo> epList = new ArrayList<ExchangePairsInfo>();
	private static ArrayList<ExchangeTripleInfo> etList = new ArrayList<ExchangeTripleInfo>();
	private static HashMap<String, ArrayList<String>>  etListMap = new HashMap<String, ArrayList<String>>();
	private static HashMap<String, HashSet<String>> tickerPairMap = new HashMap<String, HashSet<String>>();
	public static final HashMap<String, ArrayList<String>> TICKER_TRIPLE_MAP = new HashMap<String, ArrayList<String>>();
	public static final HashMap<String, TriArbitrageInfo> TRIPLE_INFO_MAP = new HashMap<String, TriArbitrageInfo>();

	public static String mqAddress = "";
	public static String consumerGroup = "";
	public static ArrayList<String> topicList = null;


	public static String influxURL = "";
	public static String influxDbName = "";
	public static String influxRpName = "";
	public static int validInterval = Integer.MAX_VALUE;

	public static void buildSpoutConfig(String spoutData){
		ArrayList<String> spoutDataList = JSON.parseObject(spoutData, new TypeReference<ArrayList<String>>(){});
		for (String e : spoutDataList){
			JSONObject j1 = JSON.parseObject(e);
			mqAddress = String.valueOf(j1.get("RocketMQNameServerAddress"));
			consumerGroup = String.valueOf(j1.get("ConsumerGroup"));
			String topicListStr = String.valueOf(j1.get("TopicList"));
			topicList = JSON.parseObject(topicListStr, new TypeReference<ArrayList<String>>(){});
			influxURL = String.valueOf(j1.get("InfluxDBURL"));
			influxDbName = String.valueOf(j1.get("InfluxDbName"));
			influxRpName = String.valueOf(j1.get("InfluxRpName"));
			validInterval = Integer.valueOf(j1.getString("ValidInterval"));
			String tickerData = String.valueOf(j1.get("TickerData"));
			ArrayList<String> exchangeTickerList2 = JSON.parseObject(tickerData, new TypeReference<ArrayList<String>>(){});
			for (String e2 : exchangeTickerList2){
				ExchangePairsInfo x = new ExchangePairsInfo();
				JSONObject j2 = JSON.parseObject(e2);
				x.exchangeName = String.valueOf(j2.get("exchangeName"));
				JSONArray tickerPairList = (JSONArray) j2.get("tickerPair");				
				for (Object jsonObject2 : tickerPairList) {					
					String[] tmp = String.valueOf(jsonObject2).split("/");
					if (tmp.length == 2){
						JSONObject jo = JSON.parseObject("{\"coinA\":\"" + tmp[0] + "\",\"coinB\":\"" + tmp[1] + "\"}");
						x.pairsJsonList.add(jo);					
					}
				}
				epList.add(x);			
			}
		}


		buildTriLists();
		reLoadTriList();
		buildIndex();
		buildTripleInfoMap();
		logger.info("-------------------Content of TICKER_TRIPLE_MAP---------------------");
		for (Entry<String, ArrayList<String>> x: TICKER_TRIPLE_MAP.entrySet()){
			logger.info("---------TICKER_TRIPLE_MAP.key=" + x.getKey());
			logger.info("---------TICKER_TRIPLE_MAP.val=" + x.getValue().toString());
		}
		logger.info("----------------------------------------");
		logger.info("-------------------Content of TRIPLE_INFO_MAP---------------------");
		for (Entry<String, TriArbitrageInfo> x: TRIPLE_INFO_MAP.entrySet()){
			logger.info("---------TRIPLE_INFO_MAP.key=" + x.getKey());
			logger.info("---------TRIPLE_INFO_MAP.val=" + x.getValue().toString());
		}
		logger.info("----------------------------------------");
	}

	public static void buildTriLists(){
		for (ExchangePairsInfo e : epList){
			ExchangeTripleInfo et = new ExchangeTripleInfo();
			et.exchangeName = e.exchangeName;
			et.triList = triSearch(e.pairsJsonList);
			etList.add(et);
		}
	}

	public static void reLoadTriList(){
		StringBuffer sb = new StringBuffer();
		for (ExchangeTripleInfo e : etList){
			if (e.triList.size() > 0){
				sb.append(e.toString());
				sb.append(",");
			}
		}
		String t1 = sb.toString();
		t1 = t1.substring(0, t1.lastIndexOf(","));
		String result = "[" + t1 + "]";

		ArrayList<String> epStringList = JSON.parseObject(result, new TypeReference<ArrayList<String>>(){});
		for (String e : epStringList){	
			ArrayList<String> trList = new ArrayList<String>();
			JSONObject jsonObj = JSON.parseObject(e);
			String exchangeName = String.valueOf(jsonObj.get("exchangeName"));
			String tmp = String.valueOf(jsonObj.get("triList"));
			if (tmp.contains("[")){
				tmp = tmp.replaceAll("\\[", "");
			}
			if (tmp.contains("]")){
				tmp = tmp.replaceAll("\\]", "");
			}
			if (tmp.contains("\"")){
				tmp = tmp.replaceAll("\"", "");
			}
			String[] tmp2 = tmp.split(",");
			for(String tmp3 : tmp2){
				trList.add(tmp3);
			}
			etListMap.put(exchangeName, trList);
		}
	}

	public static void buildIndex(){		
		for (java.util.Map.Entry<String, ArrayList<String>> e: etListMap.entrySet() ){			
			ArrayList<String> trList = e.getValue();
			HashSet<String> tickerPairSet = new HashSet<String>();
			for (String t : trList){
				String[] tmp = t.split("-");
				for (String tt: tmp){
					tickerPairSet.add(tt);
				}
			}
			tickerPairMap.put(e.getKey(), tickerPairSet);
		}
		for (java.util.Map.Entry<String, HashSet<String>> e1 : tickerPairMap.entrySet()){
			HashSet<String> tickerPairSet = e1.getValue();
			ArrayList<String> trList = etListMap.get(e1.getKey());
			String exchangeName = e1.getKey();

			for (String s1 : tickerPairSet){
				ArrayList<String> res = new ArrayList<String> ();
				String newKey = exchangeName + "@@" + s1;
				newKey = newKey.toLowerCase();
				for (String s2 : trList){
					if (s2.contains(s1)){
						String flag = regulateTriList(s2);
						String t1 = exchangeName + "@@" + s2 +"@@" +flag;
						res.add(t1.toLowerCase());
					}
				}
				TICKER_TRIPLE_MAP.put(newKey, res);				
			}			
		}
	}

	private static String regulateTriList(String str) {
		//tickerIndex;
		String flag = "";
		String[] pairs = str.split("-");
		String[][] coins = new String[pairs.length][2];
		char[][] flags = new char[pairs.length][2];
		for(int i=0;i<pairs.length;i++){
			coins[i] = pairs[i].split("/");
		}
		flags[0][0] = 'a';
		flags[0][1] = 'b';
		if(coins[0][1].equals(coins[1][0])){
			flags[1][0] = 'b';
			flags[1][1] = 'c';
			if(coins[1][1].equals(coins[2][0])){
				flags[2][0] = 'c';
				flags[2][1] = 'a';
			}else if(coins[1][1].equals(coins[2][1])){
				flags[2][1] = 'c';
				flags[2][0] = 'a';
			}
		}else if(coins[0][1].equals(coins[1][1])){
			flags[1][1] = 'b';
			flags[1][0] = 'c';
			if(coins[1][0].equals(coins[2][0])){
				flags[2][0] = 'c';
				flags[2][1] = 'a';
			}else if(coins[1][0].equals(coins[2][1])){
				flags[2][0] = 'a';
				flags[2][1] = 'c';
			}
		}else if(coins[0][1].equals(coins[2][0])){
			flags[2][0] = 'b';
			flags[2][1] = 'c';
			if(coins[2][1].equals(coins[1][0])){
				flags[1][0] = 'c';
				flags[1][1] = 'a';
			}else if(coins[2][1].equals(coins[1][1])){
				flags[1][0] = 'a';
				flags[1][1] = 'c';
			}
		}else if(coins[0][1].equals(coins[2][1])){
			flags[2][1] = 'b';
			flags[2][0] = 'c';
			if(coins[2][0].equals(coins[1][0])){
				flags[1][0] = 'c';
				flags[1][1] = 'a';
			}else if(coins[2][0].equals(coins[1][1])){
				flags[1][0] = 'a';
				flags[1][1] = 'c';
			}
		}else {
			logger.error("ERROR ERROR ERROR");
		}
		for(int i=0;i<3;i++){
			for(int j=0;j<2;j++){
				flag = flag+flags[i][j];
			}
		}
		return flag;
	}

	public static void buildTripleInfoMap(){
		for ( Entry<String, ArrayList<String>> e : TICKER_TRIPLE_MAP.entrySet()){
			ArrayList<String> val1 = e.getValue();
			for (String s : val1){							
				TriArbitrageInfo m = new TriArbitrageInfo();
				TRIPLE_INFO_MAP.put(s, m);
			}
		}

	}

	/*
	 * 通过单个交易所的所有pair获取单个交易所的所有三元组，传入参数是单个交易所所有pair
	 */
	public static ArrayList<ArrayList<JSONObject>> triSearch(ArrayList<JSONObject> al) {

		ArrayList<ArrayList<JSONObject>> allTriSearch = new ArrayList<ArrayList<JSONObject>>();

		//遍历寻找与这俩coin匹配的coin
		for(int i=0;i<al.size();i++){
			//查找第i个是否与第j个的俩coin其中一个匹配
			for(int j=i+1;j<al.size();j++){
				if(al.get(i).getString("coinA").equals(al.get(j).getString("coinA"))){
					for(int k=j+1;k<al.size();k++){
						if((al.get(i).getString("coinB").equals(al.get(k).getString("coinA"))&&al.get(j).getString("coinB").equals(al.get(k).getString("coinB")))||
								((al.get(i).getString("coinB").equals(al.get(k).getString("coinB"))&&al.get(j).getString("coinA").equals(al.get(k).getString("coinA"))))){
							ArrayList<JSONObject> eachTriCoinList = new ArrayList<JSONObject>();
							eachTriCoinList.add(al.get(i));
							eachTriCoinList.add(al.get(j));
							eachTriCoinList.add(al.get(k));

							allTriSearch.add(eachTriCoinList);
						}
					}
				}else if(al.get(i).getString("coinA").equals(al.get(j).getString("coinB"))){
					for(int k=j+1;k<al.size();k++){
						if((al.get(i).getString("coinB").equals(al.get(k).getString("coinA"))&&al.get(j).getString("coinA").equals(al.get(k).getString("coinB")))||
								((al.get(i).getString("coinB").equals(al.get(k).getString("coinA"))&&al.get(j).getString("coinA").equals(al.get(k).getString("coinB"))))){

							ArrayList<JSONObject> eachTriCoinList = new ArrayList<JSONObject>();
							eachTriCoinList.add(al.get(i));
							eachTriCoinList.add(al.get(j));
							eachTriCoinList.add(al.get(k));
							allTriSearch.add(eachTriCoinList);
						}
					}
				}else if(al.get(i).getString("coinB").equals(al.get(j).getString("coinA"))){
					for(int k=j+1;k<al.size();k++){
						if((al.get(i).getString("coinA").equals(al.get(k).getString("coinA"))&&al.get(j).getString("coinB").equals(al.get(k).getString("coinB")))||
								((al.get(i).getString("coinA").equals(al.get(k).getString("coinB"))&&al.get(j).getString("coinB").equals(al.get(k).getString("coinA"))))){

							ArrayList<JSONObject> eachTriCoinList = new ArrayList<JSONObject>();
							eachTriCoinList.add(al.get(i));
							eachTriCoinList.add(al.get(j));
							eachTriCoinList.add(al.get(k));
							allTriSearch.add(eachTriCoinList);
						}
					}
				}else if(al.get(i).getString("coinB").equals(al.get(j).getString("coinB"))){
					for(int k=j+1;k<al.size();k++){
						if((al.get(i).getString("coinA").equals(al.get(k).getString("coinA"))&&al.get(j).getString("coinA").equals(al.get(k).getString("coinB")))||
								((al.get(i).getString("coinA").equals(al.get(k).getString("coinB"))&&al.get(j).getString("coinA").equals(al.get(k).getString("coinA"))))){

							ArrayList<JSONObject> eachTriCoinList = new ArrayList<JSONObject>();
							eachTriCoinList.add(al.get(i));
							eachTriCoinList.add(al.get(j));
							eachTriCoinList.add(al.get(k));
							allTriSearch.add(eachTriCoinList);
						}
					}
				}else {
					//logger.info("can not be a pair");
				}

			}
		}		
		return allTriSearch;
	}
	/*
	 * 拿到所有交易所得三元组，参数是交易所和对应的所有pair
	 * 
	 */
	public static HashMap<String,ArrayList<ArrayList<JSONObject>>> allTriSearch(HashMap<String,ArrayList<JSONObject>> hal) {
		HashMap<String,ArrayList<ArrayList<JSONObject>>> allExchTri = new HashMap<String,ArrayList<ArrayList<JSONObject>>>();
		for(String key:hal.keySet()){
			ArrayList<JSONObject> al = hal.get(key);
			allExchTri.put(key, triSearch(al));
		}

		return allExchTri;
	}

}

class ExchangePairsInfo {
	String exchangeName = "";	
	ArrayList<JSONObject> pairsJsonList = new ArrayList<JSONObject>();
}

class ExchangeTripleInfo {
	String exchangeName = "";
	ArrayList<ArrayList<JSONObject>> triList = new ArrayList<ArrayList<JSONObject>>();

	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		for (ArrayList<JSONObject> e: triList){									
			String tmp = "\""; 
			for(JSONObject e2 : e){
				String cA = String.valueOf(e2.get("coinA"));
				String cB = String.valueOf(e2.get("coinB"));
				tmp = tmp + cA + "/" + cB + "-" ;				
			}
			tmp = tmp.substring(0, tmp.lastIndexOf("-"));
			tmp = tmp + "\",";			
			sb.append(tmp);
		}
		String s2 = sb.toString();
		if (s2.contains(",")){
			s2 = s2.substring(0, sb.lastIndexOf(","));
		}
		String s1 = "{\"exchangeName\":\"" + exchangeName + 
				"\",\"triList\":[" + s2 +
				"]}";
		return s1;
	}
}