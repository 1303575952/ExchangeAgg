package com.zdx.exchange;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.zdx.tri.TriTopology;

public class CashExchange {
	private static Logger logger = Logger.getLogger(CashExchange.class);
	public Map<String, Double> priceToUSD = new HashMap<String, Double>();
	public CashExchange(){
		priceToUSD.put("CAD", 0.7813);
		priceToUSD.put("CNY", 0.1512);
		priceToUSD.put("EUR", 1.1941);
		priceToUSD.put("GBP", 1.3996);
		priceToUSD.put("JPY", 0.008892);
		priceToUSD.put("KRW", 0.000939);
		priceToUSD.put("MXN", 0.05259);
		priceToUSD.put("PLN", 0.2776);
		priceToUSD.put("USD", 1.0);
		
	}
	public Double toUSD(String cashType, Double amount){
		cashType = cashType.toUpperCase();
		//logger.info(cashType);
		if (priceToUSD.containsKey(cashType)){
			return priceToUSD.get(cashType) * amount;
		} else {
			return 0.0;
		}
	}

}
