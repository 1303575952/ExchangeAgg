package com.zdx.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class TickerFormat {


	public static void format(String tickerJsonString, String exchangeName, TickerStandardFormat x){
		JSONObject jsonObject = JSON.parseObject(tickerJsonString);
		if ("bitfinex".equals(exchangeName)){
			BitfinexFormat(jsonObject, x);
		}else if ("gate-io".equals(exchangeName)) {
			System.out.println("format111");
			GateFormat(jsonObject, x);
			System.out.println("format222");
		}
	}

	private static void BitfinexFormat(JSONObject jsonObject, TickerStandardFormat x){
		String tmp = jsonObject.getString("timestamp");
		x.timestamp = Long.parseLong(tmp.substring(0, tmp.indexOf(".")));
		x.bid = jsonObject.getDouble("bid");
		x.ask = jsonObject.getDouble("ask");
		x.mid = jsonObject.getDouble("mid");
		x.low = jsonObject.getDouble("low");
		x.high = jsonObject.getDouble("high");
		x.volume = jsonObject.getDouble("volume");
		x.last_price = jsonObject.getDouble("last_price");
		x.setExchangeType();
		x.setMidUSD();
	}

	public static void BitstampFormat(JSONObject jsonObject, TickerStandardFormat x){
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

	public static void OkcoinFormat(JSONObject jsonObject, TickerStandardFormat x){
		x.timestamp = jsonObject.getLong("date");
		JSONObject tickerJsonObject = JSON.parseObject(jsonObject.getString("ticker"));
		x.bid = tickerJsonObject.getDouble("buy");
		x.ask = tickerJsonObject.getDouble("sell");
		x.mid = (x.bid + x.ask) / 2;
		x.low = tickerJsonObject.getDouble("low");
		x.high = tickerJsonObject.getDouble("high");
		x.volume = tickerJsonObject.getDouble("vol");
		x.last_price = tickerJsonObject.getDouble("last");
		x.setExchangeType();
		x.setMidUSD();
	}
	
	public static void QuadrigacxFormat(JSONObject jsonObject, TickerStandardFormat x){
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
	
	public static void GateFormat(JSONObject jsonObject, TickerStandardFormat x){
		x.timestamp = System.currentTimeMillis();
		x.bid = jsonObject.getDouble("highestBid");
		x.ask = jsonObject.getDouble("lowestAsk");
		x.mid = (x.bid + x.ask) / 2;
		x.low = jsonObject.getDouble("low24hr");
		x.high = jsonObject.getDouble("high24hr");
		x.volume = jsonObject.getDouble("baseVolume");
		x.last_price = jsonObject.getDouble("last");
		x.setExchangeType();
		System.out.println("x.exchangetype:"+x.exchangeType);
		x.setMidUSD();
	}
}
