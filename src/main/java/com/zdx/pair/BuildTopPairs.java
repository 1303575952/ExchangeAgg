package com.zdx.pair;

import java.io.BufferedReader;
import java.io.File;
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
import com.zdx.common.FileHandler;
import com.zdx.common.JsonFormatTool;

public class BuildTopPairs {

	public static void main(String[] args)throws Exception {

		if (args.length == 0) {
			System.err.println("Please input data file");
			System.exit(-1);
		}
		BuildTopPairs etp = new BuildTopPairs();
		etp.buildTopVol100MPairFile(args[0]);
	}
	
	public HashMap<String, HashSet<String>> loadTopVol100MSetFromFile(String filePath){
		/* Example Data topVol100M.json
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

		   }*/
		HashMap<String, HashSet<String>> topVol100MMap = new HashMap<String, HashSet<String>>(); 
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
			JSONArray platformList = JSON.parseArray(s1);

			for (Object jsonObject : platformList) {
				JSONObject js = JSONObject.parseObject(jsonObject.toString());
				String exchangeName = (String)js.get("exchangeName");
				HashSet<String> tickerSet = new HashSet<String>();
				JSONArray tickerPairList = (JSONArray) js.get("tickerPair");				
				for (Object jsonObject2 : tickerPairList) {
					tickerSet.add(String.valueOf(jsonObject2));
				}
				topVol100MMap.put(exchangeName, tickerSet);
			}
		}
		return topVol100MMap;
	}

	public void buildTopVol100MPairFile(String filePath){
		HashMap<String, HashSet<String>> topVol100MMap = loadTopVol100MSetFromFile(filePath);
		HashSet<String> cashSet = buildCashSet();
		HashMap<String, ArrayList<String>> pairExchangeMap = buildpairExchangeMap(topVol100MMap, cashSet);
		HashMap<String, ArrayList<String>> pairExchangePairMap = buildTopVol100MPair(pairExchangeMap);
		FileHandler.writeFile(filePath.substring(0, filePath.lastIndexOf(File.separator) + 1)+ "toyTopVol100MPair.json", topVol100MPairToString(pairExchangePairMap));
	}

	public HashSet<String> buildCashSet(){
		HashSet<String> cashSet = new HashSet<String>();
		cashSet.add("USD");
		cashSet.add("USDT");
		cashSet.add("JPY");
		cashSet.add("EUR");
		cashSet.add("KRW");
		cashSet.add("BRL");
		cashSet.add("GBP");
		cashSet.add("ZAR");
		cashSet.add("CAD");
		cashSet.add("PLN");
		cashSet.add("AUD");
		cashSet.add("THB");
		cashSet.add("TRY");
		cashSet.add("IDR");
		cashSet.add("RUB");
		return cashSet;
	}

	public HashMap<String, ArrayList<String>> buildpairExchangeMap(HashMap<String, HashSet<String>> topVol100MMap, HashSet<String> cashSet){
		HashMap<String, ArrayList<String>> pairExchangeMap = new HashMap<String, ArrayList<String>> ();
		for (Entry<String, HashSet<String>> x: topVol100MMap.entrySet()){
			String exchangeName = x.getKey();
			HashSet<String> pairSet = x.getValue();
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
						val = exchangeName;
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
		return pairExchangeMap;
	}

	public HashMap<String, ArrayList<String>> buildTopVol100MPair(HashMap<String, ArrayList<String>> pairExchangeMap){
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
		HashMap<String, ArrayList<String>> pairExchangePairMap = new HashMap<String, ArrayList<String>> ();		
		for (Entry<String, ArrayList<String>> tmp : pairExchangeMap.entrySet()){
			String pair = tmp.getKey();			
			ArrayList<String> exchangeList = tmp.getValue();			
			ArrayList<String> exchangePairList = new ArrayList<String>();   
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
		return pairExchangePairMap;
	}

	public static String topVol100MPairToString(HashMap<String, ArrayList<String>> pairExchangePairMap){		
		String sbTmp = "";
		StringBuffer sb = new StringBuffer();
		for (Entry<String, ArrayList<String>> tmp : pairExchangePairMap.entrySet()){
			String pair = tmp.getKey();
			ArrayList<String> exchangePairList = tmp.getValue();


			for (String x : exchangePairList){				
				String[] tt = x.split("@@");

				if (tt.length == 2){
					System.out.println(tt[0] + "@@" + tt[1]);
					//System.out.println(tt[1]);
					sb.append("{");
					sb.append("\"pair\":\"" + pair + "\",");
					sb.append("\"exchangeA\":\"" + tt[0] + "\",");
					sb.append("\"exchangeB\":\"" + tt[1] + "\"},");
				}
			}
		}
		sbTmp = sb.toString();
		if (!sbTmp.isEmpty()){
			sbTmp = sbTmp.substring(0, sbTmp.lastIndexOf(","));
		}
		sbTmp = "[" + sbTmp + "]";
		sbTmp = JsonFormatTool.formatJson(sbTmp);
		return sbTmp;
	}
}