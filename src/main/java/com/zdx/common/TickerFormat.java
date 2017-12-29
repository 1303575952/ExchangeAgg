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
		}else if(ExchangeName.BITKONAN.equals(exchangeName)){
			bitkonanFormat(jsonObject, x);
		}else if(ExchangeName.BITSO.equals(exchangeName)){
			bitsoFormat(jsonObject, x);
		}else if (ExchangeName.BITSTAMP.equals(exchangeName)) {
			bitstampFormat(jsonObject, x);
		}else if (ExchangeName.BTCALPHA.equals(exchangeName)) {
			btcalphaFormat(jsonObject, x);
		}else if(ExchangeName.CEXIO.equals(exchangeName)){
			cexFormat(jsonObject, x);
		}else if(ExchangeName.COINROOM.equals(exchangeName)){
			coinroomFormat(jsonObject, x);
		}else if(ExchangeName.EASYCOIN.equals(exchangeName)){
			easycoinFormat(jsonObject, x);
		}else if (ExchangeName.GATEIO.equals(exchangeName)) {
			gateFormat(jsonObject, x);
		}else if(ExchangeName.LIVECOIN.equals(exchangeName)){
			livecoinFormat(jsonObject, x);
		}else if (ExchangeName.OKCOIN.equals(exchangeName)) {
			okcoinFormat(jsonObject, x);
		}else if (ExchangeName.OKEX.equals(exchangeName)) {
			okexFormat(jsonObject, x);
		}else if(ExchangeName.QUADRIGACX.equals(exchangeName)){
			quadrigacxFormat(jsonObject, x);
		}else if(ExchangeName.THEROCKTRADING.equals(exchangeName)){
			therocktradingFormat(jsonObject, x);
		}else if(ExchangeName.TIDEX.equals(exchangeName)){
			tidexFormat(jsonObject, x);
		}else if(ExchangeName.WEX.equals(exchangeName)){
			wexFormat(jsonObject, x);
		}else if(ExchangeName.ZAIF.equals(exchangeName)){
			zaifFormat(jsonObject, x);
		}else if(ExchangeName.BITHUMB.equals(exchangeName)){
			bithumbFormat(jsonObject, x);
		}else if(ExchangeName.BINANCE.equals(exchangeName)){
			binanceFormat(jsonObject, x);
		}else if(ExchangeName.BITTREX.equals(exchangeName)){
			bittrexFormat(jsonObject, x);
		}else if(ExchangeName.POLONIEX.equals(exchangeName)){
			poloniexFormat(jsonObject, x);
		}else if(ExchangeName.GDAX.equals(exchangeName)){
			gdaxFormat(jsonObject, x);
		}else if(ExchangeName.HUOBI.equals(exchangeName)){
			huobiFormat(jsonObject, x);
		}else if(ExchangeName.HITBTC.equals(exchangeName)){
			hitbtcFormat(jsonObject, x);
		}else if(ExchangeName.COINONE.equals(exchangeName)){
			coinoneFormat(jsonObject, x);
		}
	}
	private static void bithumbFormat(JSONObject jsonObject, TickerStandardFormat x){
		//{"status":"0000","data":{"opening_price":"458800","closing_price":"453200","min_price":"450000","max_price":"494900","average_price":"467505.6145","units_traded":"70743.7257746","volume_1day":"70743.7257746","volume_7day":"714723.516295370000000000","buy_price":"453200","sell_price":"453300","date":"1514544065677"}}
		JSONObject tickerJsonObject = JSON.parseObject(jsonObject.getString("data"));
		x.timestamp = System.currentTimeMillis();
		//x.timestamp = tickerJsonObject.getString("date");
		x.bid = tickerJsonObject.getDouble("buy_price");
		x.ask = jsonObject.getDouble("sell_price");
		x.mid = (x.bid + x.ask) / 2;
		x.low = jsonObject.getDouble("min_price");
		x.high = jsonObject.getDouble("max_price");
		x.volume = jsonObject.getDouble("volume_1day");
		//x.lastPrice = jsonObject.getDouble("last_price");
		x.setExchangeType();
		x.setMidUSD();
	}
	private static void binanceFormat(JSONObject jsonObject, TickerStandardFormat x){
		//todo
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
	private static void bittrexFormat(JSONObject jsonObject, TickerStandardFormat x){
		//todo
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
	private static void poloniexFormat(JSONObject jsonObject, TickerStandardFormat x){
		//todo
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
	private static void gdaxFormat(JSONObject jsonObject, TickerStandardFormat x){
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
	private static void huobiFormat(JSONObject jsonObject, TickerStandardFormat x){
		//{"status":"ok","ch":"market.ethusdt.detail.merged","ts":1514547024042,"tick":{"amount":45957.304744189411111707,"open":668.510000000000000000,"close":720.000000000000000000,"high":745.000000000000000000,"id":880684657,"count":36712,"low":658.000000000000000000,"version":880684657,"ask":[720.000000000000000000,44.548320432738661691],"vol":32702954.096220878944713067540000000000000000,"bid":[719.430000000000000000,0.580000000000000000]}}
		JSONObject tickerJsonObject = JSON.parseObject(jsonObject.getString("tick"));
		String tmp = jsonObject.getString("timestamp");
		x.timestamp = Long.parseLong(tmp.substring(0, tmp.indexOf(".")));
		String t1 = jsonObject.getString("bid");
		t1 = t1.substring(t1.indexOf("["), t1.indexOf(","));
		x.bid = jsonObject.getDouble("bid");
		String t2 = jsonObject.getString("ask");
		t2 = t2.substring(t2.indexOf("["), t2.indexOf(","));
		x.ask = Double.valueOf(t2);
		x.mid = (x.bid + x.ask) / 2;
		x.low = jsonObject.getDouble("low");
		x.high = jsonObject.getDouble("high");
		x.volume = jsonObject.getDouble("volume");
		x.lastPrice = jsonObject.getDouble("last_price");
		x.setExchangeType();
		x.setMidUSD();
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

	public static void hitbtcFormat(JSONObject jsonObject, TickerStandardFormat x){
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
	public static void coinoneFormat(JSONObject jsonObject, TickerStandardFormat x){
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

	public static void bitkonanFormat(JSONObject jsonObject, TickerStandardFormat x){
		x.timestamp = jsonObject.getLong("date");
		JSONObject tickerJsonObject = JSON.parseObject(jsonObject.getString("ticker"));
		x.bid = tickerJsonObject.getDouble("buy");
		x.ask = tickerJsonObject.getDouble("sell");
		x.mid = (x.bid + x.ask) / 2;
		x.low = jsonObject.getDouble("low");
		x.high = jsonObject.getDouble("high");
		x.volume = jsonObject.getDouble("vol");
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
	
	public static void btcalphaFormat(JSONObject jsonObject, TickerStandardFormat x){
		//TODO
		/*
		 * 请求地址不对
		 */
	}
	public static void cexFormat(JSONObject jsonObject, TickerStandardFormat x){
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
	public static void easycoinFormat(JSONObject jsonObject, TickerStandardFormat x){
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
	public static void okexFormat(JSONObject jsonObject, TickerStandardFormat x){
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

	public static void therocktradingFormat(JSONObject jsonObject, TickerStandardFormat x){
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
	public static void wexFormat(JSONObject jsonObject, TickerStandardFormat x){
		//TODO
		/*
		 * wex.nz访问太慢
		 */
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


}
