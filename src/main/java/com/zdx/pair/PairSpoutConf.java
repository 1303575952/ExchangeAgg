package com.zdx.pair;

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
import com.zdx.common.CoinCashCommon;
import com.zdx.common.CommonConst;


public class PairSpoutConf {
	private static final Logger logger = LoggerFactory.getLogger(PairSpoutConf.class);
	public static HashMap<String, ArrayList<String>> exchangePairListMap = new HashMap<String, ArrayList<String>> ();

	//compute delta2
	//KEY: exchangeName + "_" + coinA + "_" + coinB
	//Value :<exchangeName + "_" + coinA + "_" + coinB + "_" + coinY; exchangeName + "_" + coinA + "_" + coinB + "_" + coinY;>
	public static HashMap<String, ArrayList<String>> pairPathMap = new HashMap<String, ArrayList<String>> ();
	//KEY:exchangeName + "_" + coinA + "_" + coinB + "_" + coinY;
	//Value: PathPrice
	public static HashMap<String, PathPrice> pathPriceMap = new HashMap<String, PathPrice> ();

	//compute delta 1
	//KEY: exchangeNameA + "_" + coinA + "_" + coinB
	//Value: <exchangeNameA + "_" + coinA + "_" + coinB + "@@" + exchangeNameB + "_" + coinX + "_" + coinY;
	//	exchangeNameA + "_" + coinA + "_" + coinB + "@@" + exchangeNameB + "_" + coinX + "_" + coinY;
	public static HashMap<String, ArrayList<String>> pairFourthMap = new HashMap<String, ArrayList<String>>();
	//KEY: exchangeNameA + "_" + coinA + "_" + coinB + "@@" + exchangeNameB + "_" + coinX + "_" + coinY
	//Value: EnterPrice
	public static HashMap<String, EnterPrice> fourthPriceMap = new HashMap<String, EnterPrice>();


	public static HashMap<String, ArrayList<String>> topVol100MMap = new HashMap<String, ArrayList<String>>(); 
	public static HashMap<String, ArrayList<String>> pairExchangeMap = new HashMap<String, ArrayList<String>> ();
	public static HashMap<String, ArrayList<String>> pairExchangePairMap = new HashMap<String, ArrayList<String>> ();	

	public static String mqAddress = "";
	public static String consumerGroup = "";
	public static ArrayList<String> topicList = null;


	public static String influxURL = "";
	public static String influxDbName = "";
	public static String influxRpName = "";	

	public static double threshold = 0.0;


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
			String tickerData = String.valueOf(j1.get("TickerData"));
			ArrayList<String> exchangeTickerList2 = JSON.parseObject(tickerData, new TypeReference<ArrayList<String>>(){});
			for (String e2 : exchangeTickerList2){
				JSONObject j2 = JSON.parseObject(e2);
				String exchangeName = String.valueOf(j2.get("exchangeName"));
				ArrayList<String> tickerSet = new ArrayList<String>();
				JSONArray tickerPairList = (JSONArray) j2.get("tickerPair");				
				for (Object jsonObject2 : tickerPairList) {
					tickerSet.add(String.valueOf(jsonObject2));
				}
				topVol100MMap.put(exchangeName, tickerSet);
			}
			buildpairExchangeMap();
			buildTopVol100MPair();
			exchangePairListMap = topVol100MMap;
			buildExchangePath();
			buildFourthTuple();
		}
	}

	public static void buildpairExchangeMap(){
		HashSet<String> cashSet = CoinCashCommon.getCashSet();

		for (Entry<String, ArrayList<String>> x: topVol100MMap.entrySet()){
			String exchangeName = x.getKey();
			ArrayList<String> pairSet = x.getValue();
			for (String pair : pairSet){
				String[] x3 = pair.split("/");
				if (x3.length == 2){
					String coinA = x3[0];
					String coinB = x3[1];
					boolean isCash = false;
					if (cashSet.contains(coinB)){
						isCash = true;
					}
					String label = "";
					String val = "";
					if (isCash){
						label = coinA + "/" + "CASH";
						val = exchangeName + "_" + pair;
					} else {
						label = coinA + "/" + coinB;
						val = exchangeName + "_" + pair;
					}
					ArrayList<String> tmp = new ArrayList<String>();
					if (pairExchangeMap.containsKey(label)){
						tmp = pairExchangeMap.get(label);
					}					
					tmp.add(val);
					pairExchangeMap.put(label, tmp);
				}
			}
		}
	}

	public static void buildTopVol100MPair(){
		/*Output Format  topVol100MPair.json
		 * {
			      "pair":"NXT/BTC",
			      "exchangeA":"bittrex",
			      "exchangeB":"hitbtc"
			   },
			   {
			      "pair":"XMR/CASH",
			      "exchangeA":"poloniex_XMR/USDT",
			      "exchangeB":"bithumb_XMR/KRW"
			   }*/

		for (Entry<String, ArrayList<String>> tmp : pairExchangeMap.entrySet()){
			String pair = tmp.getKey();			
			ArrayList<String> exchangeList = tmp.getValue();			
			ArrayList<String> exchangePairList = new ArrayList<String>();
			exchangePairList.clear();
			if (exchangeList.size() >= 2){
				for (int i1 = 0; i1 < exchangeList.size(); i1++ ){
					String exchangeA = exchangeList.get(i1);
					for (int i2 = 0; i2 < exchangeList.size(); i2++ ){					
						String exchangeB = exchangeList.get(i2);
						if (!exchangeA.equals(exchangeB)){
							exchangePairList.add(exchangeA + "@@" + exchangeB);
						}
					}
				}
				if (exchangePairList.size() >= 1){
					pairExchangePairMap.put(pair, exchangePairList);
				}
			}
		}
	}



	public static void buildFourthTuple(){
		for (Entry<String, ArrayList<String>> x : pairExchangePairMap.entrySet()){
			
			ArrayList<String> exchangeTickerPairList = x.getValue();
			for (String x2: exchangeTickerPairList){
				x2 = x2.toLowerCase();
				String[] t1 = x2.split("@@");
				if (t1.length == 2){
					String[] t11 = t1[0].split("_");
					String exchangeNameA = "";
					String coinA = "";
					String coinB = "";
					if (t11.length == 2){
						exchangeNameA = t11[0];
						String[] t12 = t11[1].split("/");
						if (t12.length == 2){
							coinA = t12[0];
							coinB = t12[1];
						}
					}
					String exchangeNameB = "";
					String coinX = "";
					String coinY = "";
					String[] t21 = t1[1].split("_");
					if (t21.length == 2){
						exchangeNameB = t21[0];
						String[] t22 = t21[1].split("/");
						if (t22.length == 2){
							coinX = t22[0];
							coinY = t22[1];
						}
					}
					EnterPrice ep = new EnterPrice();
					ep.sellExchangeName = exchangeNameA;
					ep.sellPath = coinA + "_" + coinB;
					ep.buyExchangeName = exchangeNameB;
					ep.buyPath = coinX + "_" + coinY;
					String info1 = exchangeNameA + "_" + coinA + "_" + coinB;
					String info2 = exchangeNameB + "_" + coinX + "_" + coinY;

					String info = info1 + "@@" + info2;

					ArrayList<String> s31 = new ArrayList<String>();
					if (pairFourthMap.containsKey(info1)){
						s31 = pairFourthMap.get(info1);
					}		
					s31.add(info);				
					pairFourthMap.put(info1, s31);

					ArrayList<String> s32 = new ArrayList<String>();
					if (pairFourthMap.containsKey(info2)){
						s32 = pairFourthMap.get(info2);
					}
					s32.add(info);
					pairFourthMap.put(info2, s32);	

					fourthPriceMap.put(info, ep);
				}
			}
		}
	}


	public static void buildExchangePath(){
		//pathPriceMap
		//ticker -> path
		for (Entry<String, ArrayList<String>> entry: exchangePairListMap.entrySet()){
			String exchangeName = entry.getKey();
			ArrayList<String> pairList = entry.getValue();
			for (int i1 = 0; i1 < pairList.size(); i1++){
				String[] tmp1 = pairList.get(i1).toLowerCase().split("/");
				String coinA = tmp1[0];
				String coinB = tmp1[1];
				for (int i2 = 0; i2 < pairList.size(); i2++){					
					String[] tmp2 = pairList.get(i2).toLowerCase().split("/");
					String coinX = tmp2[0];
					String coinY = tmp2[1];
					if (coinB.equals(coinX) && !coinA.equals(coinY)){
						String path = exchangeName + "_" + coinA + "_" + coinB + "_" + coinY;						
						PathPrice pp = new PathPrice();
						pp.path1 = coinA + "_" + coinB;
						pp.path2 = coinB + "_" + coinY;
						pp.pair = coinA + "_" + coinY;
						pp.exchangeName = exchangeName;
						pathPriceMap.put(path, pp);
						String exchangePairName1 = exchangeName + "_" + pp.path1;
						ArrayList<String> s1 = new ArrayList<String> ();
						if (pairPathMap.containsKey(exchangePairName1)){
							s1 = pairPathMap.get(exchangePairName1);
						}
						s1.add(path);
						pairPathMap.put(exchangePairName1, s1);
						String exchangePairName2 = exchangeName + "_" + pp.path2;
						ArrayList<String> s2 = new ArrayList<String> ();
						if (pairPathMap.containsKey(exchangePairName2)){
							s2 = pairPathMap.get(exchangePairName2);
						}
						s2.add(path);
						pairPathMap.put(exchangePairName2, s2);
					}
				}
			}			
		}
	}

	public void pathPriceMapToString(){
		for (Entry<String, PathPrice> x: pathPriceMap.entrySet()){
			System.out.println(x.getKey());
			PathPrice y = x.getValue();
			System.out.println(y.toString());
		}
	}

	public void pairPathMapToString(){

		for (Entry<String, ArrayList<String>> x: pairPathMap.entrySet()){
			System.out.println(x.getKey());
			ArrayList<String> y = x.getValue();
			for (String z :y){
				System.out.println(z);	
			}
		}
	}
}


class LowestPrice{
	String lowestPath = "";
	double lowestPrice = CommonConst.MAXPRICE;
}

class LowestPricePair{
	String lowestPath1 = "";
	double lowestPrice1 = CommonConst.MAXPRICE;
	String lowestPath2 = "";
	double lowestPrice2 = CommonConst.MAXPRICE;
	double resetFee = 0.0;
}

class PathPrice{
	String exchangeName = "";
	//A-B
	String path1 = "";
	//B-C
	String path2 = "";
	//A-C
	String pair = "";
	double ask1 = CommonConst.MAXPRICE;
	double ask2 = CommonConst.MAXPRICE;
	double bid1 = CommonConst.MAXPRICE;
	double bid2 = CommonConst.MAXPRICE;
	double fee1 = 0.002;
	double fee2 = 0.002;
	double price = 0.0;


	public String toJsonString(){
		return "{\"exchangeName\":\"" + exchangeName + 
				"\",\"path1\":\"" + path1 +
				"\",\"path2\":\"" + path2 + 
				"\",\"pair\":\"" + pair +
				"\",\"ask1\":\"" + ask1 +
				"\",\"bid1\":\"" + bid1 +
				"\",\"ask2\":\"" + ask2 +
				"\",\"bid2\":\"" + bid2 +
				"\",\"fee1\":\"" + fee1 +
				"\",\"fee2\":\"" + fee2 +
				"\",\"price\":\"" + price +
				"\"}";
	}
}

class EnterPrice{
	String sellExchangeName = "";
	String buyExchangeName = "";
	String sellPath = "";
	String buyPath = "";
	double ask1 = 0.0;
	double ask2 = 0.0;
	double bid1 = 0.0;
	double bid2 = 0.0;	
	double fee1 = 0.002;
	double fee2 = 0.002;
	double price = 0.0;
	double priceDiff = 0.0;
	double maxPriceDiff = 0.0;
	long timeStamp = 0; 
	boolean isSend = false;
	String tradeFlag = "";

	public EnterPrice(){

	}
	public EnterPrice(String jsonString){
		JSONObject jsonObject = JSON.parseObject(jsonString);
		if (jsonObject.containsKey("sellExchangeName")){
			sellExchangeName = jsonObject.getString("sellExchangeName");
		}
		if (jsonObject.containsKey("buyExchangeName")){
			buyExchangeName = jsonObject.getString("buyExchangeName");
		}
		if (jsonObject.containsKey("sellPath")){
			sellPath = jsonObject.getString("sellPath");
		}
		if (jsonObject.containsKey("buyPath")){
			buyPath = jsonObject.getString("buyPath");
		}
		if (jsonObject.containsKey("bid1")){
			bid1 = jsonObject.getDouble("bid1");
		}
		if (jsonObject.containsKey("ask1")){
			ask1 = jsonObject.getDouble("ask1");
		}
		if (jsonObject.containsKey("bid2")){
			bid2 = jsonObject.getDouble("bid2");
		}
		if (jsonObject.containsKey("ask2")){
			ask2 = jsonObject.getDouble("ask2");
		}
		if (jsonObject.containsKey("fee1")){
			fee1 = jsonObject.getDouble("fee1");
		}
		if (jsonObject.containsKey("fee2")){
			fee2 = jsonObject.getDouble("fee2");
		}
		if (jsonObject.containsKey("priceDiff")){
			priceDiff = jsonObject.getDouble("priceDiff");
		}
		if (jsonObject.containsKey("maxPriceDiff")){
			maxPriceDiff = jsonObject.getDouble("maxPriceDiff");
		}
		if (jsonObject.containsKey("isSend")){
			isSend = jsonObject.getBooleanValue("isSend");
		}
		if (jsonObject.containsKey("tradeFlag")){
			tradeFlag = jsonObject.getString("tradeFlag");
		}
		if (jsonObject.containsKey("timestamp")){
			timeStamp = jsonObject.getLong("timestamp");
		}
	}

	public String toJsonString(){
		return "{\"sellExchangeName\":\"" + sellExchangeName + 
				"\",\"sellPath\":\"" + sellPath +
				"\",\"buyExchangeName\":\"" + buyExchangeName + 
				"\",\"buyPath\":\"" + buyPath +
				"\",\"ask1\":\"" + ask1 +
				"\",\"bid1\":\"" + bid1 +
				"\",\"ask2\":\"" + ask2 +
				"\",\"bid2\":\"" + bid2 +
				"\",\"fee1\":\"" + fee1 +
				"\",\"fee2\":\"" + fee2 +
				"\",\"price\":\"" + price +
				"\",\"priceDiff\":\"" + priceDiff +
				"\",\"timeStamp\":\"" + timeStamp +
				"\",\"isSend\":\"" + isSend +
				"\",\"tradeFlag\":\"" + tradeFlag +
				"\"}";
	}
}