package com.zdx.pair;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zdx.common.CommonConst;

public class PariConfig {

	public HashMap<String, ArrayList<String>> exchangePairListMap = new HashMap<String, ArrayList<String>> ();

	//compute delta2
	//KEY: exchangeName + "_" + coinA + "_" + coinB
	//Value :<exchangeName + "_" + coinA + "_" + coinB + "_" + coinY; exchangeName + "_" + coinA + "_" + coinB + "_" + coinY;>
	public HashMap<String, ArrayList<String>> pairPathMap = new HashMap<String, ArrayList<String>> ();
	//KEY:exchangeName + "_" + coinA + "_" + coinB + "_" + coinY;
	//Value: PathPrice
	public HashMap<String, PathPrice> pathPriceMap = new HashMap<String, PathPrice> ();

	//compute delta 1
	//KEY: exchangeNameA + "_" + coinA + "_" + coinB
	//Value: <exchangeNameA + "_" + coinA + "_" + coinB + "@@" + exchangeNameB + "_" + coinX + "_" + coinY;
	//	exchangeNameA + "_" + coinA + "_" + coinB + "@@" + exchangeNameB + "_" + coinX + "_" + coinY;
	public HashMap<String, ArrayList<String>> pairFourthMap = new HashMap<String, ArrayList<String>>();
	//KEY: exchangeNameA + "_" + coinA + "_" + coinB + "@@" + exchangeNameB + "_" + coinX + "_" + coinY
	//Value: EnterPrice
	public HashMap<String, EnterPrice> fourthPriceMap = new HashMap<String, EnterPrice>();

	public void initPairConfig(String filePath1, String filePath2){
		loadExchangePair(filePath1);
		buildExchangePath();
		loadFourthTuple(filePath2);
	}

	public void loadExchangePair(String filePath){
		/* Output Format
		 * {
			  "exchangeName":"huobi",
			   "tickerPair":
			      [
			         "ETH/USDT",
			         "BTC/USDT",
			         "LTC/BTC",
			         "ETH/BTC",
			         "BCC/BTC",
			         "ETC/BTC",
			         "BCC/USDT",
			         "LTC/USDT",
			         "ETC/USDT"
			      ]

			   },*/
		ExchangeTopPairs etp = new ExchangeTopPairs();
		HashMap<String, HashSet<String>> topVol100MMap = etp.loadTopVol100MSetFromFile(filePath);
		for(Entry<String, HashSet<String>> x : topVol100MMap.entrySet()){
			String exchangeName = x.getKey();
			HashSet<String> y = x.getValue();
			ArrayList<String> z = new ArrayList<String>();
			for (String a : y){
				z.add(a);
			}
			exchangePairListMap.put(exchangeName, z);
		}
	}

	public void loadFourthTuple(String filePath){
		StringBuffer sb = new StringBuffer();
		InputStream is;
		try {
			is = new FileInputStream(filePath);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));			
			String y ="";
			try {
				while((y = br.readLine())!=null){
					sb.append(y);
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		String s1 = sb.toString();
		s1 = s1.toLowerCase();

		if (!s1.isEmpty()){
			JSONArray fourthList = JSON.parseArray(s1);

			for (Object jsonObject : fourthList) {				
				JSONObject js = JSONObject.parseObject(jsonObject.toString());
				//String pair = (String)js.get("pair");
				String s11 = (String)js.get("exchangea");				
				String exchangeNameA = "";
				String coinA = "";
				String coinB = "";
				if (s11.contains("_")){
					String[] t1 = s11.split("_");
					exchangeNameA = t1[0];					
					String[] t2 = t1[1].split("/");
					coinA = t2[0];
					coinB = t2[1];
				}

				String s22 = (String)js.get("exchangeb");				
				String exchangeNameB = "";
				String coinX = "";
				String coinY = "";
				if (s22.contains("_")){
					String[] t1 = s22.split("_");
					exchangeNameB = t1[0];					
					String[] t2 = t1[1].split("/");
					coinX = t2[0];
					coinY = t2[1];
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

	public void buildExchangePath(){
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