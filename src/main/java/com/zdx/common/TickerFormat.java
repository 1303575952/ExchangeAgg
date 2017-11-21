package com.zdx.common;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import clojure.main;

public class TickerFormat {
	private static Logger logger = Logger.getLogger(TickerFormat.class);

	public static void format(String tickerJsonString, String exchangeName, TickerStandardFormat x){
		JSONObject jsonObject = JSON.parseObject(tickerJsonString);
		if ("bitfinex".equals(exchangeName)){
			BitfinexFormat(jsonObject, x);
		}else if ("bitstamp".equals(exchangeName)) {
			BitstampFormat(jsonObject, x);
		}else if ("okcoin".equals(exchangeName)) {
			OkcoinFormat(jsonObject, x);
		}else if("quadrigacx".equals(exchangeName)){
			QuadrigacxFormat(jsonObject, x);
		}else if ("gate-io".equals(exchangeName)) {
			GateFormat(jsonObject, x);
		}else if("tidex".equals(exchangeName)){
			TidexFormat(jsonObject, x);
		}else if("zaif".equals(exchangeName)){
			ZaifFormat(jsonObject, x);
		}else if("bitso".equals(exchangeName)){
			BitsoFormat(jsonObject, x);
		}else if("coinroom".equals(exchangeName)){
			CoinroomFormat(jsonObject, x);
		}else if("coinroom".equals(exchangeName)){
			CryptopiaFormat(jsonObject, x);
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
		logger.info("x.exchangetype:"+x.exchangeType);
		x.setMidUSD();
	}
	
	public static void TidexFormat(JSONObject jsonObject, TickerStandardFormat x){
		String[] str = jsonObject.toString().split("\\{");
		String jsonStr = ("{"+str[2]).substring(0, ("{"+str[2]).length()-1);
		JSONObject tickerJsonObject = JSON.parseObject(jsonStr);
		x.timestamp = tickerJsonObject.getLong("updated");
		x.bid = tickerJsonObject.getDouble("buy");
		x.ask = tickerJsonObject.getDouble("sell");
		x.mid = (x.bid + x.ask) / 2;
		x.low = tickerJsonObject.getDouble("low");
		x.high = tickerJsonObject.getDouble("high");
		x.volume = tickerJsonObject.getDouble("vol_cur");
		x.last_price = tickerJsonObject.getDouble("last");
		x.setExchangeType();
		x.setMidUSD();
	}
	
	public static void ZaifFormat(JSONObject jsonObject, TickerStandardFormat x){
		x.timestamp = System.currentTimeMillis();
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
	
	public static void BitsoFormat(JSONObject jsonObject, TickerStandardFormat x){
		x.timestamp = System.currentTimeMillis();
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
	
	public static void CoinroomFormat(JSONObject jsonObject, TickerStandardFormat x){
		x.timestamp = System.currentTimeMillis();
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
	
	public static void CryptopiaFormat(JSONObject jsonObject, TickerStandardFormat x){
		x.timestamp = System.currentTimeMillis();
		JSONObject tickerJsonObject = JSON.parseObject(jsonObject.getString("Data"));
		x.bid = tickerJsonObject.getDouble("BidPrice");
		x.ask = tickerJsonObject.getDouble("AskPrice");
		x.mid = (x.bid + x.ask) / 2;
		x.low = tickerJsonObject.getDouble("Low");
		x.high = tickerJsonObject.getDouble("High");
		x.volume = tickerJsonObject.getDouble("Volume");
		x.last_price = tickerJsonObject.getDouble("LastPrice");
		x.setExchangeType();
		x.setMidUSD();
	}
}
