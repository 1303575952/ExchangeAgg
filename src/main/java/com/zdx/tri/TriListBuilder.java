package com.zdx.tri;
import java.util.ArrayList;
import java.util.HashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.zdx.common.FileIO;
import com.zdx.common.JsonFormatTool;

public class TriListBuilder {
	/*
	 * 通过单个交易所的所有pair获取单个交易所得所有三元组，传入参数是单个交易所所有pair
	 */
	public static ArrayList<ArrayList<JSONObject>> triSearch(ArrayList<JSONObject> al) {
		ArrayList<ArrayList<JSONObject>> allTriSearch = new ArrayList<ArrayList<JSONObject>>();


		for(int i=0;i<al.size();i++){//遍历寻找与这俩coin匹配的coin
			for(int j=i+1;j<al.size();j++){//查找第i个是否与第j个的俩coin其中一个匹配
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
					//System.out.println("can not be a pair");
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
			allExchTri.put(key, TriListBuilder.triSearch(al));
		}

		return allExchTri;
	}
	
	public static void buildTriListFromPairFile(String pairPath, String triPath){
		ArrayList<ExchangePairsInfo> epList = loadExchangePairs(pairPath);
		ArrayList<ExchangeTripleInfo> etList = buildTriLists(epList);
		StringBuffer sb = new StringBuffer();
		for (ExchangeTripleInfo e : etList){
			if (e.triList.size() > 0){
				sb.append(e.toString());
				sb.append(",");
			}
		}
		String tmp = sb.toString();
		tmp = tmp.substring(0, tmp.lastIndexOf(","));
		String result = "[" + tmp + "]";
		

		FileIO.writeFile(triPath, JsonFormatTool.formatJson(result));
	}

	public static ArrayList<ExchangeTripleInfo> buildTriLists(ArrayList<ExchangePairsInfo> epList){
		ArrayList<ExchangeTripleInfo> etList = new ArrayList<ExchangeTripleInfo>();
		for (ExchangePairsInfo e : epList){
			ExchangeTripleInfo et = new ExchangeTripleInfo();
			et.exchangeName = e.exchangeName;
			et.triList = TriListBuilder.triSearch(e.pairsJsonList);
			etList.add(et);
		}
		return etList;
	}

	public static ArrayList<ExchangePairsInfo> loadExchangePairs(String path){
		ArrayList<ExchangePairsInfo> epList = new ArrayList<ExchangePairsInfo>();
		String text = FileIO.ReadFile(path);
		if (text.isEmpty()){
			return epList ;
		}
		ArrayList<String> epStringList = JSON.parseObject(text, new TypeReference<ArrayList<String>>(){});
		for (String e : epStringList){			
			ExchangePairsInfo x = new ExchangePairsInfo();
			JSONObject jsonObj = JSON.parseObject(e);
			x.exchangeName = String.valueOf(jsonObj.get("exchangeName"));
			String tmp = String.valueOf(jsonObj.get("pairs"));
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
				String[] tmp4 = tmp3.split("/");
				if (tmp4.length == 2){
					JSONObject jo = JSON.parseObject("{\"coinA\":\"" + tmp4[0] + "\",\"coinB\":\"" + tmp4[1] + "\"}");
					x.pairsJsonList.add(jo);					
				}
			}
			epList.add(x);
		}
		return epList;
	}
}