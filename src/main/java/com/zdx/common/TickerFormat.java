package com.zdx.common;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class TickerFormat {
	private static Logger logger = Logger.getLogger(TickerFormat.class);

	public static void format(String tickerJsonString, String exchangeName, TickerStandardFormat x) {
		JSONObject jsonObject = JSON.parseObject(tickerJsonString);
		if (ExchangeName.BINANCE.equals(exchangeName)) {
			binanceFormat(jsonObject, x);
		} else if (ExchangeName.BITFINEX.equals(exchangeName)) {
			bitfinexFormat(jsonObject, x);
		} else if (ExchangeName.BITHUMB.equals(exchangeName)) {
			bithumbFormat(jsonObject, x);
		} else if (ExchangeName.BITKONAN.equals(exchangeName)) {
			bitkonanFormat(jsonObject, x);
		} else if (ExchangeName.BITSO.equals(exchangeName)) {
			bitsoFormat(jsonObject, x);
		} else if (ExchangeName.BITSTAMP.equals(exchangeName)) {
			bitstampFormat(jsonObject, x);
		} else if (ExchangeName.BITTREX.equals(exchangeName)) {
			logger.debug("============bittrex===========");
			bittrexFormat(jsonObject, x);
		} else if (ExchangeName.BTCALPHA.equals(exchangeName)) {
			btcalphaFormat(jsonObject, x);
		} else if (ExchangeName.CEXIO.equals(exchangeName)) {
			cexFormat(jsonObject, x);
		} else if (ExchangeName.COINONE.equals(exchangeName)) {
			coinoneFormat(jsonObject, x);
		} else if (ExchangeName.COINROOM.equals(exchangeName)) {
			coinroomFormat(jsonObject, x);
		} else if (ExchangeName.DSX.equals(exchangeName)) {
			dsxFormat(jsonObject, x);
		} else if (ExchangeName.EASYCOIN.equals(exchangeName)) {
			easycoinFormat(jsonObject, x);
		} else if (ExchangeName.GATEIO.equals(exchangeName)) {
			gateFormat(jsonObject, x);
		} else if (ExchangeName.GDAX.equals(exchangeName)) {
			gdaxFormat(jsonObject, x);
		} else if (ExchangeName.HITBTC.equals(exchangeName)) {
			hitbtcFormat(jsonObject, x);
		} else if (ExchangeName.HUOBI.equals(exchangeName)) {
			huobiFormat(jsonObject, x);
		} else if (ExchangeName.KORBIT.equals(exchangeName)) {
			korbitFormat(jsonObject, x);
		} else if (ExchangeName.KRAKEN.equals(exchangeName)) {
			krakenFormat(jsonObject, x);
		} else if (ExchangeName.LIVECOIN.equals(exchangeName)) {
			livecoinFormat(jsonObject, x);
		} else if (ExchangeName.OKCOIN.equals(exchangeName)) {
			okcoinFormat(jsonObject, x);
		} else if (ExchangeName.OKEX.equals(exchangeName)) {
			okexFormat(jsonObject, x);
		} else if (ExchangeName.POLONIEX.equals(exchangeName)) {
			poloniexFormat(jsonObject, x);
		} else if (ExchangeName.QUADRIGACX.equals(exchangeName)) {
			quadrigacxFormat(jsonObject, x);
		} else if (ExchangeName.THEROCKTRADING.equals(exchangeName)) {
			therocktradingFormat(jsonObject, x);
		} else if (ExchangeName.TIDEX.equals(exchangeName)) {
			tidexFormat(jsonObject, x);
		} else if (ExchangeName.WEX.equals(exchangeName)) {
			wexFormat(jsonObject, x);
		} else if (ExchangeName.ZAIF.equals(exchangeName)) {
			zaifFormat(jsonObject, x);
		}
	}

	private static void bithumbFormat(JSONObject jsonObject, TickerStandardFormat x) {
		// {"status":"0000","data":{"opening_price":"458800","closing_price":"453200","min_price":"450000","max_price":"494900","average_price":"467505.6145","units_traded":"70743.7257746","volume_1day":"70743.7257746","volume_7day":"714723.516295370000000000","buy_price":"453200","sell_price":"453300","date":"1514544065677"}}
		JSONObject tickerJsonObject = JSON.parseObject(jsonObject.getString("data"));
		x.timestamp = System.currentTimeMillis();
		x.bid = tickerJsonObject.getDouble("buy_price");
		x.ask = tickerJsonObject.getDouble("sell_price");
		x.mid = (x.bid + x.ask) / 2;
		x.low = tickerJsonObject.getDouble("min_price");
		x.high = tickerJsonObject.getDouble("max_price");
		x.volume = tickerJsonObject.getDouble("volume_1day");
		x.lastPrice = tickerJsonObject.getDouble("closing_price");
		x.setExchangeType();
		x = setToUSD(x);
	}

	private static void binanceFormat(JSONObject jsonObject, TickerStandardFormat x) {
		x.timestamp = System.currentTimeMillis();
		x.bid = jsonObject.getDouble("bidPrice");
		x.ask = jsonObject.getDouble("askPrice");
		x.mid = (x.bid + x.ask) / 2;
		x.low = jsonObject.getDouble("low");
		x.high = jsonObject.getDouble("high");
		x.volume = jsonObject.getDouble("volume");
		x.lastPrice = jsonObject.getDouble("last");
		x.setExchangeType();
		x = setToUSD(x);
	}

	private static void bittrexFormat(JSONObject jsonObject, TickerStandardFormat x) {
		logger.debug("=======bittrexFormat begin========");
		logger.debug("jsonObject = " + jsonObject.toJSONString());
		String str_00 = jsonObject.toJSONString();
		String[] str1 = str_00.split("\\{");
		String[] str2 = str1[2].split("\\}");
		String str_11 = "{" + str2[0] + "}";
		JSONObject tickerJsonObject = JSON.parseObject(str_11);
		logger.debug("tickerJsonObject = " + tickerJsonObject.toJSONString());
		x.timestamp = System.currentTimeMillis();
		x.bid = tickerJsonObject.getDouble("Bid");
		x.ask = tickerJsonObject.getDouble("Ask");
		x.mid = (x.bid + x.ask) / 2;
		x.low = tickerJsonObject.getDouble("Low");
		x.high = tickerJsonObject.getDouble("High");
		x.volume = tickerJsonObject.getDouble("BaseVolume");
		x.lastPrice = tickerJsonObject.getDouble("Last");
		x.setExchangeType();
		x = setToUSD(x);
	}

	private static void poloniexFormat(JSONObject jsonObject, TickerStandardFormat x) {
		x.timestamp = System.currentTimeMillis();
		String bids_00 = jsonObject.get("bids").toString();
		String[] bidss = bids_00.split("\"");
		x.bid = Double.parseDouble(bidss[1]);
		String asks_00 = jsonObject.get("asks").toString();
		String[] askss = asks_00.split("\"");
		x.ask = Double.parseDouble(askss[1]);
		x.mid = (x.bid + x.ask) / 2;
		x.low = jsonObject.getDouble("low");
		x.high = jsonObject.getDouble("high");
		x.volume = jsonObject.getDouble("volume");
		x.lastPrice = jsonObject.getDouble("last");
		x.setExchangeType();
		x = setToUSD(x);
	}

	private static void bitfinexFormat(JSONObject jsonObject, TickerStandardFormat x) {
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
		x = setToUSD(x);
	}

	public static void bitstampFormat(JSONObject jsonObject, TickerStandardFormat x) {
		x.timestamp = jsonObject.getLong("timestamp");
		x.bid = jsonObject.getDouble("bid");
		x.ask = jsonObject.getDouble("ask");
		x.mid = (x.bid + x.ask) / 2;
		x.low = jsonObject.getDouble("low");
		x.high = jsonObject.getDouble("high");
		x.volume = jsonObject.getDouble("volume");
		x.lastPrice = jsonObject.getDouble("last");
		x.setExchangeType();
		x = setToUSD(x);
	}

	public static void coinoneFormat(JSONObject jsonObject, TickerStandardFormat x) {
		x.timestamp = jsonObject.getLong("timestamp");
		x.bid = jsonObject.getDouble("bid");
		x.ask = jsonObject.getDouble("ask");
		x.mid = (x.bid + x.ask) / 2;
		x.low = jsonObject.getDouble("low");
		x.high = jsonObject.getDouble("high");
		x.volume = jsonObject.getDouble("volume");
		x.lastPrice = jsonObject.getDouble("last");
		x.setExchangeType();
		x = setToUSD(x);
	}

	public static void bitkonanFormat(JSONObject jsonObject, TickerStandardFormat x) {
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
		x = setToUSD(x);
	}

	public static void bitsoFormat(JSONObject jsonObject, TickerStandardFormat x) {
		x.timestamp = System.currentTimeMillis();
		x.bid = jsonObject.getDouble("bid");
		x.ask = jsonObject.getDouble("ask");
		x.mid = (x.bid + x.ask) / 2;
		x.low = jsonObject.getDouble("low");
		x.high = jsonObject.getDouble("high");
		x.volume = jsonObject.getDouble("volume");
		x.lastPrice = jsonObject.getDouble("last");
		x.setExchangeType();
		x = setToUSD(x);
	}

	public static void btcalphaFormat(JSONObject jsonObject, TickerStandardFormat x) {
		// TODO
		/*
		 * 请求地址不对
		 */
	}

	public static void cexFormat(JSONObject jsonObject, TickerStandardFormat x) {
		x.timestamp = jsonObject.getLong("timestamp");
		x.bid = jsonObject.getDouble("bid");
		x.ask = jsonObject.getDouble("ask");
		x.mid = (x.bid + x.ask) / 2;
		x.low = jsonObject.getDouble("low");
		x.high = jsonObject.getDouble("high");
		x.volume = jsonObject.getDouble("volume");
		x.lastPrice = jsonObject.getDouble("last");
		x.setExchangeType();
		x = setToUSD(x);
	}

	public static void coinroomFormat(JSONObject jsonObject, TickerStandardFormat x) {
		x.timestamp = System.currentTimeMillis();
		x.bid = jsonObject.getDouble("bid");
		x.ask = jsonObject.getDouble("ask");
		x.mid = (x.bid + x.ask) / 2;
		x.low = jsonObject.getDouble("low");
		x.high = jsonObject.getDouble("high");
		x.volume = jsonObject.getDouble("volume");
		x.lastPrice = jsonObject.getDouble("last");
		x.setExchangeType();
		x = setToUSD(x);
	}

	public static void cryptopiaFormat(JSONObject jsonObject, TickerStandardFormat x) {
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
		x = setToUSD(x);
	}

	public static void dsxFormat(JSONObject jsonObject, TickerStandardFormat x) {
		String[] str = jsonObject.toString().split("\\{");
		String jsonStr = ("{" + str[2]).substring(0, ("{" + str[2]).length() - 1);
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
		x = setToUSD(x);
	}

	public static void easycoinFormat(JSONObject jsonObject, TickerStandardFormat x) {
		x.timestamp = System.currentTimeMillis();
		x.bid = jsonObject.getDouble("bid");
		x.ask = jsonObject.getDouble("ask");
		x.mid = (x.bid + x.ask) / 2;
		x.low = jsonObject.getDouble("low");
		x.high = jsonObject.getDouble("high");
		x.volume = jsonObject.getDouble("volume");
		x.lastPrice = jsonObject.getDouble("last");
		x.setExchangeType();
		x = setToUSD(x);
	}

	public static void gateFormat(JSONObject jsonObject, TickerStandardFormat x) {
		x.timestamp = System.currentTimeMillis();
		x.bid = jsonObject.getDouble("highestBid");
		x.ask = jsonObject.getDouble("lowestAsk");
		x.mid = (x.bid + x.ask) / 2;
		x.low = jsonObject.getDouble("low24hr");
		x.high = jsonObject.getDouble("high24hr");
		x.volume = jsonObject.getDouble("baseVolume");
		x.lastPrice = jsonObject.getDouble("last");
		x.setExchangeType();
		logger.info("x.exchangetype:" + x.exchangeType);
		x = setToUSD(x);
	}

	private static void gdaxFormat(JSONObject jsonObject, TickerStandardFormat x) {
		x.timestamp = System.currentTimeMillis();
		x.bid = jsonObject.getDouble("bid");
		x.ask = jsonObject.getDouble("ask");
		x.mid = (x.bid + x.ask) / 2;
		x.low = jsonObject.getDouble("low");
		x.high = jsonObject.getDouble("high");
		x.volume = jsonObject.getDouble("volume");
		x.lastPrice = jsonObject.getDouble("price");
		x.setExchangeType();
		x = setToUSD(x);
	}

	public static void hitbtcFormat(JSONObject jsonObject, TickerStandardFormat x) {
		x.timestamp = System.currentTimeMillis();
		x.bid = jsonObject.getDouble("bid");
		x.ask = jsonObject.getDouble("ask");
		x.mid = (x.bid + x.ask) / 2;
		x.low = jsonObject.getDouble("low");
		x.high = jsonObject.getDouble("high");
		x.volume = jsonObject.getDouble("volume");
		x.lastPrice = jsonObject.getDouble("last");
		x.setExchangeType();
		x = setToUSD(x);
	}

	private static void huobiFormat(JSONObject jsonObject, TickerStandardFormat x) {
		// {"status":"ok","ch":"market.ethusdt.detail.merged","ts":1514547024042,"tick":{"amount":45957.304744189411111707,"open":668.510000000000000000,"close":720.000000000000000000,"high":745.000000000000000000,"id":880684657,"count":36712,"low":658.000000000000000000,"version":880684657,"ask":[720.000000000000000000,44.548320432738661691],"vol":32702954.096220878944713067540000000000000000,"bid":[719.430000000000000000,0.580000000000000000]}}
		JSONObject tickerJsonObject = JSON.parseObject(jsonObject.getString("tick"));
		x.timestamp = System.currentTimeMillis();
		String t1 = tickerJsonObject.getString("bid");
		t1 = t1.substring(t1.indexOf("[") + 1, t1.indexOf(","));
		x.bid = Double.valueOf(t1);
		String t2 = tickerJsonObject.getString("ask");
		t2 = t2.substring(t2.indexOf("[") + 1, t2.indexOf(","));
		x.ask = Double.valueOf(t2);
		x.mid = (x.bid + x.ask) / 2;
		x.low = tickerJsonObject.getDouble("low");
		x.high = tickerJsonObject.getDouble("high");
		x.volume = tickerJsonObject.getDouble("vol");
		x.lastPrice = tickerJsonObject.getDouble("close");
		x.setExchangeType();
		x = setToUSD(x);
	}

	public static void korbitFormat(JSONObject jsonObject, TickerStandardFormat x) {
		x.timestamp = System.currentTimeMillis();
		x.bid = jsonObject.getDouble("bid");
		x.ask = jsonObject.getDouble("ask");
		x.mid = (x.bid + x.ask) / 2;
		x.low = jsonObject.getDouble("low");
		x.high = jsonObject.getDouble("high");
		x.volume = jsonObject.getDouble("volume");
		x.lastPrice = jsonObject.getDouble("last");
		x.setExchangeType();
		x = setToUSD(x);
	}
	
	public static void krakenFormat(JSONObject jsonObject, TickerStandardFormat x) {
		x.timestamp = System.currentTimeMillis();
		JSONObject resultJsonObject = JSON.parseObject(jsonObject.getString("result"));
		String tmp1 = resultJsonObject.toJSONString();
		String[] tmpArr1 = tmp1.split("\\{");
		String[] tmpArr2 = tmpArr1[tmpArr1.length - 1].split("\\}");
		String tmp2 = "{" + tmpArr2[0] + "}";
		JSONObject tickerJsonObject = JSON.parseObject(tmp2);
		String asks_00 = tickerJsonObject.get("a").toString();
		String[] askss = asks_00.split("\"");
		x.ask = Double.parseDouble(askss[1]);
		String bids_00 = tickerJsonObject.get("b").toString();
		String[] bidss = bids_00.split("\"");
		x.bid = Double.parseDouble(bidss[1]);
		x.mid = (x.bid + x.ask) / 2;
		String low_00 = tickerJsonObject.get("l").toString();
		String[] lows = low_00.split("\"");
		x.low = Double.parseDouble(lows[1]);
		String high_00 = tickerJsonObject.get("h").toString();
		String[] highs = high_00.split("\"");
		x.high = Double.parseDouble(highs[1]);
		String volume_00 = tickerJsonObject.get("v").toString();
		String[] volumes = volume_00.split("\"");
		x.volume = Double.parseDouble(volumes[1]);
		String last_00 = tickerJsonObject.get("l").toString();
		String[] lasts = last_00.split("\"");
		x.lastPrice = Double.parseDouble(lasts[1]);
		x.setExchangeType();
		x = setToUSD(x);
	}

	public static void livecoinFormat(JSONObject jsonObject, TickerStandardFormat x) {
		x.timestamp = System.currentTimeMillis();
		x.bid = jsonObject.getDouble("best_bid");
		x.ask = jsonObject.getDouble("best_ask");
		x.mid = (x.bid + x.ask) / 2;
		x.low = jsonObject.getDouble("low");
		x.high = jsonObject.getDouble("high");
		x.volume = jsonObject.getDouble("volume");
		x.lastPrice = jsonObject.getDouble("last");
		x.setExchangeType();
		x = setToUSD(x);
	}

	public static void okcoinFormat(JSONObject jsonObject, TickerStandardFormat x) {
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
		x = setToUSD(x);
	}

	public static void okexFormat(JSONObject jsonObject, TickerStandardFormat x) {
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
		x = setToUSD(x);
	}

	public static void quadrigacxFormat(JSONObject jsonObject, TickerStandardFormat x) {
		x.timestamp = jsonObject.getLong("timestamp");
		x.bid = jsonObject.getDouble("bid");
		x.ask = jsonObject.getDouble("ask");
		x.mid = (x.bid + x.ask) / 2;
		x.low = jsonObject.getDouble("low");
		x.high = jsonObject.getDouble("high");
		x.volume = jsonObject.getDouble("volume");
		x.lastPrice = jsonObject.getDouble("last");
		x.setExchangeType();
		x = setToUSD(x);
	}

	public static void therocktradingFormat(JSONObject jsonObject, TickerStandardFormat x) {
		x.timestamp = System.currentTimeMillis();
		x.bid = jsonObject.getDouble("bid");
		x.ask = jsonObject.getDouble("ask");
		x.mid = (x.bid + x.ask) / 2;
		x.low = jsonObject.getDouble("low");
		x.high = jsonObject.getDouble("high");
		x.volume = jsonObject.getDouble("volume");
		x.lastPrice = jsonObject.getDouble("last");
		x.setExchangeType();
		x = setToUSD(x);
	}

	public static void tidexFormat(JSONObject jsonObject, TickerStandardFormat x) {
		String[] str = jsonObject.toString().split("\\{");
		String jsonStr = ("{" + str[2]).substring(0, ("{" + str[2]).length() - 1);
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
		x = setToUSD(x);
	}

	public static void wexFormat(JSONObject jsonObject, TickerStandardFormat x) {
		// TODO
		/*
		 * wex.nz访问太慢
		 */
	}

	public static void zaifFormat(JSONObject jsonObject, TickerStandardFormat x) {
		x.timestamp = System.currentTimeMillis();
		x.bid = jsonObject.getDouble("bid");
		x.ask = jsonObject.getDouble("ask");
		x.mid = (x.bid + x.ask) / 2;
		x.low = jsonObject.getDouble("low");
		x.high = jsonObject.getDouble("high");
		x.volume = jsonObject.getDouble("volume");
		x.lastPrice = jsonObject.getDouble("last");
		x.setExchangeType();
		x = setToUSD(x);
	}

	public static TickerStandardFormat setToUSD(TickerStandardFormat x) {
		String b = x.coinB.toUpperCase();
		if (CoinCashCommon.getCashSet().contains(b)) {
			CashExchange ce = new CashExchange();
			// coin 2 cash
			x.midUSD = ce.toUSD(b, x.mid);
			x.bid = ce.toUSD(b, x.bid);
			x.ask = ce.toUSD(b, x.ask);
			x.mid = ce.toUSD(b, x.mid);
			x.midUSD = x.mid;
			x.low = ce.toUSD(b, x.low);
			x.high = ce.toUSD(b, x.high);
			x.lastPrice = ce.toUSD(b, x.lastPrice);
		} else {
			// coin 2 coin
			x.midUSD = x.mid;
		}
		return x;
	}

}
