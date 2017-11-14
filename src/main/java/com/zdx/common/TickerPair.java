package com.zdx.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class TickerPair {
	public String exchA = "";
	public String exchB = "";
	public String coinA = "";
	public String coinB = "";
	public Double midAUSD = 0.0;
	public Double midBUSD = 0.0;
	public Double volA = 0.0;
	public Double volB = 0.0;

	public String exchangeType = "";

	public TickerPair(){
	}

	public String toJsonString(){
		return "{\"exchA\":\"" + exchA + 
				"\",\"exchB\":\"" + exchB +
				"\",\"coinA\":\"" + coinA + 
				"\",\"coinB\":\"" + coinB +
				"\",\"midAUSD\":\"" + midAUSD +
				"\",\"midBUSD\":\"" + midBUSD +
				"\",\"volA\":\"" + volA +
				"\",\"volB\":\"" + volB +
				"\"}";
	}

	public JSONObject toJson(){
		return JSON.parseObject(this.toJsonString());
	}

	public TickerPair formatTickerPair(TickerStandardFormat a, TickerStandardFormat b){
		this.exchA = a.exchangeName;
		this.exchB = b.exchangeName;
		this.coinA = a.coinA + "_" + a.coinB;
		this.coinB = b.coinA + "_" + b.coinB;
		this.midAUSD = a.midUSD;
		this.midBUSD = b.midUSD;
		this.volA = a.volume;
		this.volB = b.volume;
		return this;
	}
}
