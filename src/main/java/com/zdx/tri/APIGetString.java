package com.zdx.tri;
import java.util.ArrayList;
import java.util.HashMap;

import com.alibaba.fastjson.JSONObject;

public class APIGetString {
	public static ArrayList<String> getPath(String host){
		ArrayList<String> apiPath = new ArrayList<String>();
		if(host.equals("gate-io")){
			apiPath.add("http://data.gate.io/api2/1/ticker/");
			apiPath.add("_");
		}
		return apiPath;
	}
	/*
	 * 把每一个交易所的三元组构造成get请求形式
	 */
	public static ArrayList<ArrayList<String>> OneExchTriDataGetOneByOne(String host,ArrayList<ArrayList<JSONObject>> oneExchTri){
		ArrayList<ArrayList<String>> oneExchAllTriGetString = new ArrayList<ArrayList<String>>();
		for(int i=0;i<oneExchTri.size();i++){
			ArrayList<String> oneTriGetString = new ArrayList<String>();
			for(int j=0;j<3;j++){
				//获取host-根据host规范化
				String getString = APIGetString.getPath(host).get(0)+oneExchTri.get(i).get(j).getString("coinA")+APIGetString.getPath(host).get(1)+oneExchTri.get(i).get(j).getString("coinB");
				//请求
				oneTriGetString.add(getString);
			}
			oneExchAllTriGetString.add(oneTriGetString);
		}
		
		return oneExchAllTriGetString;
		
	}
	
	/*
	 * 把所有交易所的三元组构造成请求形式
	 */
	public static HashMap<String,ArrayList<ArrayList<String>>> allExchTriDataGetOneByOne(HashMap<String,ArrayList<ArrayList<JSONObject>>> allExchTri){
		HashMap<String,ArrayList<ArrayList<String>>> allExchAllTriGetString = new HashMap<String,ArrayList<ArrayList<String>>>();
		for(String key:allExchTri.keySet()){
			ArrayList<ArrayList<JSONObject>> value = allExchTri.get(key);
			allExchAllTriGetString.put(key,APIGetString.OneExchTriDataGetOneByOne(key, value));
		}
		
		return allExchAllTriGetString;
	}
	
	public static void main(String[] args) {
		
	}

}
