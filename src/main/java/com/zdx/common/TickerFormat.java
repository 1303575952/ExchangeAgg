package com.zdx.common;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import clojure.main;

public class TickerFormat {
	private static Logger logger = Logger.getLogger(TickerFormat.class);

	public static void format(String tickerJsonString, String exchangeName, TickerStandardFormat x){
		JSONObject jsonObject = JSON.parseObject(tickerJsonString);
		if (ExchangeName.BITFINEX.equals(exchangeName)){
			bitfinexFormat(jsonObject, x);
		}else if (ExchangeName.BITSTAMP.equals(exchangeName)) {
			bitstampFormat(jsonObject, x);
		}else if (ExchangeName.OKCOIN.equals(exchangeName)) {
			okcoinFormat(jsonObject, x);
		}else if(ExchangeName.QUADRIGACX.equals(exchangeName)){
			quadrigacxFormat(jsonObject, x);
		}else if (ExchangeName.GATEIO.equals(exchangeName)) {
			gateFormat(jsonObject, x);
		}else if(ExchangeName.TIDEX.equals(exchangeName)){
			tidexFormat(jsonObject, x);
		}else if(ExchangeName.ZAIF.equals(exchangeName)){
			zaifFormat(jsonObject, x);
		}else if(ExchangeName.BITSO.equals(exchangeName)){
			bitsoFormat(jsonObject, x);
		}else if(ExchangeName.COINROOM.equals(exchangeName)){
			coinroomFormat(jsonObject, x);
		}else if(ExchangeName.LIVECOIN.equals(exchangeName)){
			livecoinFormat(jsonObject, x);
		}
	}

	private static void bitfinexFormat(JSONObject jsonObject, TickerStandardFormat x){
		String tmp = jsonObject.getString("timestamp");
		x.timestamp = Long.parseLong(tmp.substring(0, tmp.indexOf(".")));
		x.bid = jsonObject.getDouble("bid");
		x.ask = jsonObject.getDouble("ask");
		x.mid = jsonObject.getDouble("mid");
		x.low = jsonObject.getDouble("low");
		x.high = jsonObject.getDouble("high");
		x.volume = jsonObject.getDouble("volume");
		x.lastPrice = jsonObject.getDouble("last_price");
		x.setExchangeType();
		x.setMidUSD();
	}

	public static void bitstampFormat(JSONObject jsonObject, TickerStandardFormat x){
		x.timestamp = jsonObject.getLong("timestamp");
		x.bid = jsonObject.getDouble("bid");
		x.ask = jsonObject.getDouble("ask");
		x.mid = (x.bid + x.ask) / 2;
		x.low = jsonObject.getDouble("low");
		x.high = jsonObject.getDouble("high");
		x.volume = jsonObject.getDouble("volume");
		x.lastPrice = jsonObject.getDouble("last");
		x.setExchangeType();
		x.setMidUSD();
	}

	public static void okcoinFormat(JSONObject jsonObject, TickerStandardFormat x){
		x.timestamp = jsonObject.getLong("date");
		JSONObject tickerJsonObject = JSON.parseObject(jsonObject.getString("ticker"));
		x.bid = tickerJsonObject.getDouble("buy");
		x.ask = tickerJsonObject.getDouble("sell");
		x.mid = (x.bid + x.ask) / 2;
		x.low = tickerJsonObject.getDouble("low");
		x.high = tickerJsonObject.getDouble("high");
		x.volume = tickerJsonObject.getDouble("vol");
		x.lastPrice = tickerJsonObject.getDouble("last");
		x.setExchangeType();
		x.setMidUSD();
	}
	
	public static void quadrigacxFormat(JSONObject jsonObject, TickerStandardFormat x){
		x.timestamp = jsonObject.getLong("timestamp");
		x.bid = jsonObject.getDouble("bid");
		x.ask = jsonObject.getDouble("ask");
		x.mid = (x.bid + x.ask) / 2;
		x.low = jsonObject.getDouble("low");
		x.high = jsonObject.getDouble("high");
		x.volume = jsonObject.getDouble("volume");
		x.lastPrice = jsonObject.getDouble("last");
		x.setExchangeType();
		x.setMidUSD();
	}
	
	public static void gateFormat(JSONObject jsonObject, TickerStandardFormat x){
		x.timestamp = System.currentTimeMillis();
		x.bid = jsonObject.getDouble("highestBid");
		x.ask = jsonObject.getDouble("lowestAsk");
		x.mid = (x.bid + x.ask) / 2;
		x.low = jsonObject.getDouble("low24hr");
		x.high = jsonObject.getDouble("high24hr");
		x.volume = jsonObject.getDouble("baseVolume");
		x.lastPrice = jsonObject.getDouble("last");
		x.setExchangeType();
		logger.info("x.exchangetype:"+x.exchangeType);
		x.setMidUSD();
	}
	
	public static void tidexFormat(JSONObject jsonObject, TickerStandardFormat x){
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
		x.lastPrice = tickerJsonObject.getDouble("last");
		x.setExchangeType();
		x.setMidUSD();
	}
	
	public static void zaifFormat(JSONObject jsonObject, TickerStandardFormat x){
		x.timestamp = System.currentTimeMillis();
		x.bid = jsonObject.getDouble("bid");
		x.ask = jsonObject.getDouble("ask");
		x.mid = (x.bid + x.ask) / 2;
		x.low = jsonObject.getDouble("low");
		x.high = jsonObject.getDouble("high");
		x.volume = jsonObject.getDouble("volume");
		x.lastPrice = jsonObject.getDouble("last");
		x.setExchangeType();
		x.setMidUSD();
	}
	
	public static void bitsoFormat(JSONObject jsonObject, TickerStandardFormat x){
		x.timestamp = System.currentTimeMillis();
		x.bid = jsonObject.getDouble("bid");
		x.ask = jsonObject.getDouble("ask");
		x.mid = (x.bid + x.ask) / 2;
		x.low = jsonObject.getDouble("low");
		x.high = jsonObject.getDouble("high");
		x.volume = jsonObject.getDouble("volume");
		x.lastPrice = jsonObject.getDouble("last");
		x.setExchangeType();
		x.setMidUSD();
	}
	
	public static void coinroomFormat(JSONObject jsonObject, TickerStandardFormat x){
		x.timestamp = System.currentTimeMillis();
		x.bid = jsonObject.getDouble("bid");
		x.ask = jsonObject.getDouble("ask");
		x.mid = (x.bid + x.ask) / 2;
		x.low = jsonObject.getDouble("low");
		x.high = jsonObject.getDouble("high");
		x.volume = jsonObject.getDouble("volume");
		x.lastPrice = jsonObject.getDouble("last");
		x.setExchangeType();
		x.setMidUSD();
	}
	
	public static void cryptopiaFormat(JSONObject jsonObject, TickerStandardFormat x){
		x.timestamp = System.currentTimeMillis();
		JSONObject tickerJsonObject = JSON.parseObject(jsonObject.getString("Data"));
		x.bid = tickerJsonObject.getDouble("BidPrice");
		x.ask = tickerJsonObject.getDouble("AskPrice");
		x.mid = (x.bid + x.ask) / 2;
		x.low = tickerJsonObject.getDouble("Low");
		x.high = tickerJsonObject.getDouble("High");
		x.volume = tickerJsonObject.getDouble("Volume");
		x.lastPrice = tickerJsonObject.getDouble("LastPrice");
		x.setExchangeType();
		x.setMidUSD();
	}
	
	public static void livecoinFormat(JSONObject jsonObject, TickerStandardFormat x){
		x.timestamp = System.currentTimeMillis();
		x.bid = jsonObject.getDouble("best_bid");
		x.ask = jsonObject.getDouble("best_ask");
		x.mid = (x.bid + x.ask) / 2;
		x.low = jsonObject.getDouble("low");
		x.high = jsonObject.getDouble("high");
		x.volume = jsonObject.getDouble("volume");
		x.lastPrice = jsonObject.getDouble("last");
		x.setExchangeType();
		x.setMidUSD();
	}
}
