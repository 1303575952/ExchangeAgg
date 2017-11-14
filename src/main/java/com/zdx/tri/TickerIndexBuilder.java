package com.zdx.tri;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.zdx.common.FileIO;
import com.zdx.common.JsonFormatTool;



public class TickerIndexBuilder {
	static HashMap<String, ArrayList<String>> tickerIndex = new HashMap<String, ArrayList<String>>();
	
	public void buildIndexFromFile(String path){
		HashMap<String, ArrayList<String>> etListMap =  loadTriListFromFile(path);
		buildIndex(etListMap);
	}
	
	public void saveToFile(String triPath){
		String result = toString();
		FileIO.writeFile(triPath, JsonFormatTool.formatJson(result));
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		for (java.util.Map.Entry<String, ArrayList<String>> x : tickerIndex.entrySet()){
			StringBuffer sb2 = new StringBuffer();
			sb2.append("{\"tickerPair\":\"" +  x.getKey() + "\",");
			sb2.append("\"triList\":[" );
			ArrayList<String> res = x.getValue();
			for (String x2 : res){
				sb2.append("\"" + x2 + "\",");
			}
			String s2 = sb2.toString();
			if (s2.contains(",")){
				s2 = s2.substring(0, s2.lastIndexOf(","));
			}
			sb.append(s2);
			sb.append("]},");
		}
		String s1 = sb.toString();
		if (s1.contains(",")){
			s1 = s1.substring(0, s1.lastIndexOf(","));
		}
		return "[" + s1 + "]";
	}
	
	public static void buildIndex(HashMap<String, ArrayList<String>> etListMap){		
		HashMap<String, HashSet<String>> tickerPairMap = new HashMap<String, HashSet<String>>();
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
				for (String s2 : trList){
					if (s2.contains(s1)){
						String flag = regulateTriList(s2);
						res.add(exchangeName + "@@" + s2 +"@@" +flag);
					}
				}
				tickerIndex.put(newKey, res);				
			}			
		}
		
		//regulateTriList();
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
			System.out.println("ERROR ERROR ERROR");
		}
		for(int i=0;i<3;i++){
			for(int j=0;j<2;j++){
				flag = flag+flags[i][j];
			}
		}
		return flag;
	}
	public static void main(String[] args) {
		regulateTriList("ETH/USDT-DPY/USDT-DPY/ETH");
	}

	public static HashMap<String, ArrayList<String>> loadTriListFromFile(String path){
		HashMap<String, ArrayList<String>>  etListMap = new HashMap<String, ArrayList<String>>();
		String text = FileIO.ReadFile(path);
		if (text.isEmpty()){
			return etListMap ;
		}
		ArrayList<String> epStringList = JSON.parseObject(text, new TypeReference<ArrayList<String>>(){});
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
		return etListMap;
	}
}
