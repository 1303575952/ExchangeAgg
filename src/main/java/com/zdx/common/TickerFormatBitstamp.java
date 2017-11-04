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
		x.volume = jsonObject.getDouble("volume");
		x.last_price = jsonObject.getDouble("last");
		x.setExchangeType();
		x.setMidUSD();
	}
	public static void BitstampTest(){
		TickerFormat tickerData = new TickerFormat();
		String tickerinfo = "{\"high\": \"0.04188990\", \"last\": \"0.04086000\", \"timestamp\": \"1509731854\", \"bid\": \"0.04078001\", \"vwap\": \"0.04070972\", \"volume\": \"7098.22041892\", \"low\": \"0.03930000\", \"ask\": \"0.04107129\", \"open\": \"0.04069880\"}";
		String path = "eth_btc";
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
