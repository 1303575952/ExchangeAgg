package com.zdx.rocketmq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TickerConfInfo {
	public HashMap<String, ArrayList<String>>  exchangeTickerListMap = new HashMap<String, ArrayList<String>>();

	public HashMap<String, String> exchangeSymbolMap = new HashMap<String, String>();

	public HashMap<String, String> pathSymbolMap = new HashMap<String, String>();

	public List<String> targetHosts = new ArrayList<String>();

	public List<List<String>> replaceLists = new ArrayList<List<String>>();
	
	public String toString(){
		String s1 = exchangeTickerListMap.toString();
		
		String s2 = exchangeSymbolMap.toString();
		
		String s3 = pathSymbolMap.toString();
		
		String s4 = targetHosts.toString();
		
		String s5 = replaceLists.toString();
		
		return s1 + "\n" + s2 + "\n" + s3 + "\n" + s4 + "\n" + s5;
	}
}
