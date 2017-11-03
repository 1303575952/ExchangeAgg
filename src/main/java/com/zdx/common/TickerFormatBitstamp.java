package com.zdx.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class TickerFormatBitstamp {
	public static void format(String tickerinfo, TickerFormat x){
		JSONObject jsonObject = JSON.parseObject(tickerinfo);

		x.timestamp = jsonObject.getLong("timestamp");
		x.bid = jsonObject.getDouble("bid");
		x.ask = jsonObject.getDouble("ask");
		x.mid = (x.bid + x.ask) / 2;
		x.low = jsonObject.getDouble("low");
		x.high = jsonObject.getDouble("high");
		x.volume = jsonObject.getDouble("vol");
		x.last_price = jsonObject.getDouble("last");
		x.setExchangeType();
		x.setMidUSD();
	}
	public static void BitstampTest(){
		TickerFormat tickerData = new TickerFormat();
		String tickerinfo = "{\"high\":\"7500.00\",\"last\":\"7307.21\",\"timestamp\":\"1509726692\",\"bid\":\"7306.12\",\"vwap\":\"7167.06\",\"volume\":\"17203.29047620\",\"low\":\"6822.00\",\"ask\":\"7307.21\",\"open\":\"7030.00\"}";
		String path = "btc_usd";
		tickerData.exchangeName = "bitstamp";
		String[] coinAB = path.split("_");
		tickerData.coinA = coinAB[0];
		tickerData.coinB = coinAB[1];
		format(tickerinfo, tickerData);
	}
	public static void main(String[] args){
		BitstampTest();
	}
}
