package com.zdx.common;

import java.util.HashSet;

import org.apache.log4j.Logger;

public class CoinCashCommon {
	private static Logger logger = Logger.getLogger(CoinCashCommon.class);
	public static HashSet<String> coinSet = new HashSet<String>();
	public static HashSet<String> cashSet = new HashSet<String>();

	public static HashSet<String> getCoinSet(){
		coinSet.add("btc");
		coinSet.add("bch");
		coinSet.add("eth");
		coinSet.add("zec");
		coinSet.add("ltc");
		coinSet.add("etc");
		coinSet.add("zec");
		coinSet.add("qtum");
		coinSet.add("waves");
		coinSet.add("mona");
		coinSet.add("xem");
		coinSet.add("orme");
		return coinSet;
	}
	public static HashSet<String> getCashSet(){
		cashSet.add("usd");
		cashSet.add("cny");
		cashSet.add("jpy");
		cashSet.add("usdt");
		cashSet.add("mxn");
		cashSet.add("pln");
		cashSet.add("cad");
		
		return cashSet;
	}
}
