package com.zdx.common;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class TickerStandardFormat {
	private static Logger logger = Logger.getLogger(TickerStandardFormat.class);
	public String exchangeName = "";
	public String exchangeType = "";
	public String coinA = "";
	public String coinB = "";		
	public Double mid = 0.0;
	public Double bid = 0.0;
	public Double ask = 0.0;
	public Double lastPrice = 0.0;
	public Double low = 0.0;
	public Double high = 0.0;
	public Double volume = 0.0;
	public Long timestamp;	
	public Double midUSD = 0.0;

	public TickerStandardFormat(){
	}

	public String toJsonString(){
		return "{\"exchangeName\":\"" + exchangeName + 
				"\",\"exchangeType\":\"" + exchangeType +
				"\",\"coinA\":\"" + coinA + 
				"\",\"coinB\":\"" + coinB +
				"\",\"midUSD\":\"" + midUSD +
				"\",\"mid\":\"" + mid +
				"\",\"bid\":\"" + bid +
				"\",\"ask\":\"" + ask +
				"\",\"last_price\":\"" + lastPrice +
				"\",\"low\":\"" + low +
				"\",\"high\":\"" + high +
				"\",\"volume\":\"" + volume +
				"\",\"timestamp\":\"" + timestamp +
				"\"}";

	}

	public JSONObject toJson(){
		return JSON.parseObject(this.toJsonString());
	}

	public TickerStandardFormat setExchangeType(){
		if (this.coinB.isEmpty()){
			logger.debug("coinB is empty");
			return this;
		} else {
			if (CoinCashCommon.getCashSet().contains(this.coinB)){
				this.exchangeType = "coin2cash";
			} else {
				this.exchangeType = "coin2coin";
			}
			return this;
		}
	}

	public TickerStandardFormat formatJsonString(String jsonString){
		JSONObject jsonObject = JSON.parseObject(jsonString);
		if (jsonObject.containsKey(CommonConst.EXCHANGE_NAME)){
			this.exchangeName = jsonObject.getString(CommonConst.EXCHANGE_NAME);
		}
		if (jsonObject.containsKey(CommonConst.EXCHANGE_TYPE)){
			this.exchangeType = jsonObject.getString(CommonConst.EXCHANGE_TYPE);
		}
		if (jsonObject.containsKey(CommonConst.COIN_A)){
			this.coinA = jsonObject.getString(CommonConst.COIN_A);
		}
		if (jsonObject.containsKey(CommonConst.COIN_B)){
			this.coinB = jsonObject.getString(CommonConst.COIN_B);
		}
		if (jsonObject.containsKey("midUSD")){
			this.midUSD = jsonObject.getDouble("midUSD");
		}
		if (jsonObject.containsKey("mid")){
			this.mid = jsonObject.getDouble("mid");
		}
		if (jsonObject.containsKey("bid")){
			this.bid = jsonObject.getDouble("bid");
		}
		if (jsonObject.containsKey("ask")){
			this.ask = jsonObject.getDouble("ask");
		}
		if (jsonObject.containsKey("last_price")){
			this.lastPrice = jsonObject.getDouble("last_price");
		}
		if (jsonObject.containsKey("low")){
			this.low = jsonObject.getDouble("low");
		}
		if (jsonObject.containsKey("high")){
			this.high = jsonObject.getDouble("high");
		}
		if (jsonObject.containsKey("volume")){
			this.volume = jsonObject.getDouble("volume");
		}
		if (jsonObject.containsKey("timestamp")){
			this.timestamp = jsonObject.getLong("timestamp");
		}
		return this;
	}

	public TickerStandardFormat setMidUSD(){
		logger.info(this.toJsonString());
		if (CoinCashCommon.getCashSet().contains(this.coinB)){
			CashExchange ce = new CashExchange();
			//coin 2 cash
			this.midUSD = ce.toUSD(this.coinB, this.mid);
		} else {
			//coin 2 coin
			this.midUSD = this.mid;
		}
		return this;
	}
	


	public int hashCodeWithoutTimeStamp(){
		String s1 = "{\"exchangeName\":\"" + exchangeName + 
				"\",\"exchangeType\":\"" + exchangeType +
				"\",\"coinA\":\"" + coinA + 
				"\",\"coinB\":\"" + coinB +
				"\",\"midUSD\":\"" + midUSD +
				"\",\"mid\":\"" + mid +
				"\",\"bid\":\"" + bid +
				"\",\"ask\":\"" + ask +
				"\",\"last_price\":\"" + lastPrice +			
				"\"}";
		return s1.hashCode();
	}

}
