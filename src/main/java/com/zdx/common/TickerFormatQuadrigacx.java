package com.zdx.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class TickerFormatQuadrigacx {

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
	public static void QuadrigacxTest(){
		TickerFormat tickerData = new TickerFormat();
		String tickerinfo = "{\"high\":\"0.04213000\",\"last\":\"0.04100001\",\"timestamp\":\"1509716505\",\"volume\":\"23397.97744340\",\"vwap\":\"0.04016977\",\"low\":\"0.03910000\",\"ask\":\"0.04188990\",\"bid\":\"0.04101325\"}";
		String path = "eth_btc";
		tickerData.exchangeName = "quadrigacx";
		String[] coinAB = path.split("_");
		tickerData.coinA = coinAB[0];
		tickerData.coinB = coinAB[1];
		format(tickerinfo, tickerData);
	}
	public static void main(String[] args){
		QuadrigacxTest();
	}
}
