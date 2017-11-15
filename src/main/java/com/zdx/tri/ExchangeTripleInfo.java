package com.zdx.tri;

import java.util.ArrayList;

import com.alibaba.fastjson.JSONObject;

public class ExchangeTripleInfo {

	String exchangeName = "";
	ArrayList<ArrayList<JSONObject>> triList = new ArrayList<ArrayList<JSONObject>>();

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