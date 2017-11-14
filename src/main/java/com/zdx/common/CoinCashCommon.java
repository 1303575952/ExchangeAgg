package com.zdx.common;

import java.util.HashSet;

public class CoinCashCommon {
	public static HashSet<String> coinSet = new HashSet<String>();
	public static HashSet<String> cashSet = new HashSet<String>();

	public static HashSet<String> getCoinSet(){
		coinSet.add("btc");
		coinSet.add("eth");
		coinSet.add("zec");
		coinSet.add("ltc");
		coinSet.add("etc");
		coinSet.add("zec");
		return coinSet;
	}
	public static HashSet<String> getCashSet(){
		cashSet.add("usd");
		cashSet.add("cny");
		cashSet.add("usdt");
		return cashSet;
	}
}
