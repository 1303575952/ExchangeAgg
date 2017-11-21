package com.zdx.common;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.zdx.tri.TriTopology;

public class CashExchange {
	private static Logger logger = Logger.getLogger(CashExchange.class);
	public Map<String, Double> priceToUSD = new HashMap<String, Double>();
	public CashExchange(){
		priceToUSD.put("cny", 0.1512);
		priceToUSD.put("usd", 1.0);
		priceToUSD.put("jpy", 0.008892);
		priceToUSD.put("usdt", 1.0);
		priceToUSD.put("mxn", 0.05259);
		priceToUSD.put("pln", 0.2776);
		priceToUSD.put("cad", 0.7813);
	}
	public Double toUSD(String cashType, Double amount){
		logger.info(cashType);
		logger.info(priceToUSD.toString());
		if (priceToUSD.containsKey(cashType)){
			return priceToUSD.get(cashType) * amount;
		} else {
			return 0.0;
		}
	}

}
