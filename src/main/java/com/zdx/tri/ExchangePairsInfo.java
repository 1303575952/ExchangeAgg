package com.zdx.tri;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.zdx.demo.ToyConsumer;

public class ExchangePairsInfo {
	private static Logger logger = Logger.getLogger(ExchangePairsInfo.class);
	String exchangeName = "";	
	ArrayList<JSONObject> pairsJsonList = new ArrayList<JSONObject>();
}
