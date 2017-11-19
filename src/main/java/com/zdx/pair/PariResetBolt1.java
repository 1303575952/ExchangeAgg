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
import com.zdx.common.TickerStandardFormat;

public class PariResetBolt1 {

	public HashMap<String, ArrayList<String>> exchangePairListMap = new HashMap<String, ArrayList<String>> ();
	
	//compute delta2
	public HashMap<String, ArrayList<String>> pairPathMap = new HashMap<String, ArrayList<String>> ();
	public HashMap<String, PathPrice> pathPriceMap = new HashMap<String, PathPrice> ();
	public HashMap<String, LowestPrice> pairPriceMap = new HashMap<String, LowestPrice> ();
	//compute delta 1
	public HashMap<String, ArrayList<String>> pairFourthMap = new HashMap<String, ArrayList<String>>();
	public HashMap<String, EnterPrice> fourthPriceMap = new HashMap<String, EnterPrice>();
	
	public void loadExchangePair(String filePath){
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

		if (!s1.isEmpty()){
			JSONArray fourthList = JSON.parseArray(s1);
			for (Object jsonObject : fourthList) {
				JSONObject js = JSONObject.parseObject(jsonObject.toString());
				String exchangeNameA = (String)js.get("exchangeA");
				String exchangeNameB = (String)js.get("exchangeB");
				String pair = (String)js.get("pair");
				String[] tmp = pair.split("/");
				String coinA = tmp[0].toLowerCase();
				String coinB = tmp[1].toLowerCase();
				String info = exchangeNameA + "_" + exchangeNameB + "_" + coinA + "_" + coinB;
				
				ArrayList<String> s11 = new ArrayList<String>();
				String key1 = exchangeNameA + "_" + coinA + "_" + coinB;
				if (pairFourthMap.containsKey(key1)){
					s11 = pairFourthMap.get(key1);
				}
				s11.add(info);
				pairFourthMap.put(key1, s11);
				ArrayList<String> s12 = new ArrayList<String>();
				String key2 = exchangeNameB + "_" + coinA + "_" + coinB;
				if (pairFourthMap.containsKey(key2)){
					s12 = pairFourthMap.get(key2);
				}
				s11.add(info);
				pairFourthMap.put(key2, s12);
				EnterPrice ep = new EnterPrice();
				ep.exchangeName1 = "";
				ep.exchangeName2 = "";
				ep.path1 = "";
				ep.path2 = "";
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
						System.out.println(path);
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
	
	public void updatePrice(TickerStandardFormat tsf){
		String pair = tsf.coinA.toLowerCase() + "_" + tsf.coinB.toLowerCase();		
		String exchangePairName = tsf.exchangeName.toLowerCase() + "_" + pair;		
		//用ticker直接值更新最低价，如BTC-USD
		updateLowestPrice(exchangePairName, pair, tsf.ask);
		if (pairPathMap.containsKey(exchangePairName)){
			ArrayList<String> pathNameList = pairPathMap.get(exchangePairName);
			for (String pathName : pathNameList){
				PathPrice pp =  pathPriceMap.get(pathName);
				if (pair.equals(pp.path1)){
					pp.ask1 = tsf.ask;
				}
				if (pair.equals(pp.path2)){
					pp.ask2 = tsf.ask;
				}
				pp.price = (1 + pp.fee1) * pp.ask1 * (1 + pp.fee2) * pp.ask2;				
				//更新etc-eth-usd间接价格
				pathPriceMap.put(pathName, pp);
				//用间接价格更新最低价，如etc-usd
				String exchangePairName2 = tsf.exchangeName.toLowerCase() + pp.pair;
				updateLowestPrice(exchangePairName2, pp.pair, pp.price);
			}
		}
		
		if (pairFourthMap.containsKey(exchangePairName)){
			ArrayList<String> tmp1 = pairFourthMap.get(exchangePairName);
			for (String x : tmp1){
				if (fourthPriceMap.containsKey(x)){
					EnterPrice ep = fourthPriceMap.get(x);
					if (ep.exchangeName1.equals(tsf.exchangeName.toLowerCase())){
						ep.ask1 = 0.0;
						
					} else if (ep.exchangeName2.equals(tsf.exchangeName.toLowerCase())){
						ep.ask2 = 0.0;
					}
					ep.profit = 1;
					if (ep.profit > 1.05){
						//report
						enterOportunity();
					}
					fourthPriceMap.put(x, ep);
				}
				
			}
		}
	}
	
	

	public void updateLowestPrice(String tickerName, String lowestPath, double lowestPrice){
		if (pairPriceMap.containsKey(tickerName)){
			LowestPrice lp = pairPriceMap.get(tickerName);
			if (lowestPrice < lp.lowestPrice){
				lp.lowestPath = lowestPath;
				lp.lowestPrice = lowestPrice;
			}
			pairPriceMap.put(tickerName, lp);
		} else {
			LowestPrice lp = pairPriceMap.get(tickerName);
			lp.lowestPath = lowestPath;
			lp.lowestPrice = lowestPrice;
			pairPriceMap.put(tickerName, lp);
		}
	}

	public LowestPricePair getPairResetInfo(String exchangeTicker1, String exchangeTicker2){
		LowestPricePair lpp = new LowestPricePair();		
		LowestPrice lp1 = new LowestPrice();
		LowestPrice lp2 = new LowestPrice();
		if (pairPriceMap.containsKey(exchangeTicker1) && pairPriceMap.containsKey(exchangeTicker2)){
			lp1 = pairPriceMap.get(exchangeTicker1);
			lp2 = pairPriceMap.get(exchangeTicker2);
		}
		lpp.lowestPath1 = lp1.lowestPath;
		lpp.lowestPrice1 = lp1.lowestPrice;
		lpp.lowestPath2 = lp2.lowestPath;
		lpp.lowestPrice2 = lp2.lowestPrice;
		if (lpp.lowestPrice2 < Integer.MAX_VALUE){
			lpp.resetFee = lpp.lowestPrice1 / lpp.lowestPrice2 - 1;
		}
		return lpp;
	}
	
	public void enterOportunity(){
		String exchangeTicker1 = "";
		String exchangeTicker2 = "";
		LowestPricePair lpp = getPairResetInfo(exchangeTicker1, exchangeTicker2);
		double delta2 = lpp.resetFee;
	}
}

class LowestPrice{
	String lowestPath = "";
	double lowestPrice = 0.0;
}

class LowestPricePair{
	String lowestPath1 = "";
	double lowestPrice1 = 0.0;
	String lowestPath2 = "";
	double lowestPrice2 = 0.0;
	double resetFee = 0.0;
}

class PathPrice{
	String exchangeName = "";
	String path1 = "";//A-B
	String path2 = "";//B-C
	String pair = "";//A-C
	double ask1 = 0.0;
	double ask2 = 0.0;
	double fee1 = 0.0;
	double fee2 = 0.0;
	double price = 0.0;
}

class EnterPrice{
	String exchangeName1 = "";
	String exchangeName2 = "";
	String path1 = "";//A-B
	String path2 = "";//B-C	
	double ask1 = 0.0;
	double ask2 = 0.0;
	double fee1 = 0.0;
	double fee2 = 0.0;
	double price = 0.0;
	double profit = 0.0;
}