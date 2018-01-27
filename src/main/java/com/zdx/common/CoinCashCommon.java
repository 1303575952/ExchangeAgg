package com.zdx.common;

import java.util.HashSet;

import org.apache.log4j.Logger;

public class CoinCashCommon {
	private static Logger logger = Logger.getLogger(CoinCashCommon.class);
	public static HashSet<String> coinSet = new HashSet<String>();
	public static HashSet<String> cashSet = new HashSet<String>();


	public static HashSet<String> getCoinSet(){
		coinSet.add("IOTA");
		coinSet.add("AHT");
		coinSet.add("ARN");
		coinSet.add("BCC");
		coinSet.add("BCH");
		coinSet.add("BCY");
		coinSet.add("BOT");
		coinSet.add("BTC");
		coinSet.add("BTG");
		coinSet.add("BMC");
		coinSet.add("BNB");
		coinSet.add("BTS");
		coinSet.add("B@");
		coinSet.add("CHAT");
		coinSet.add("DASH");
		coinSet.add("DOGE");
		coinSet.add("DRCT");
		coinSet.add("EDG");
		coinSet.add("ENG");
		coinSet.add("ENJ");
		coinSet.add("EOS");
		coinSet.add("EOT");
		coinSet.add("ETC");
		coinSet.add("ETH");
		coinSet.add("ETN");
		coinSet.add("EVX");
		coinSet.add("GNT");
		coinSet.add("HPY");
		coinSet.add("HSR");
		coinSet.add("INCENT");
		coinSet.add("INK");
		coinSet.add("INPAY");
		coinSet.add("KBR");
		coinSet.add("KNC");
		coinSet.add("KOLION");
		coinSet.add("LTC");
		coinSet.add("LUX");
		coinSet.add("MER");
		coinSet.add("MGO");
		coinSet.add("MONA");
		coinSet.add("MRT");
		coinSet.add("MSD");
		coinSet.add("MTH");
		coinSet.add("MTL");
		coinSet.add("NXT");
		coinSet.add("OCL");
		coinSet.add("ORME");
		coinSet.add("PBT");
		coinSet.add("PEPECASH");
		coinSet.add("PING");
		coinSet.add("PPC");
		coinSet.add("PRG");
		coinSet.add("PROC");
		coinSet.add("QRL");
		coinSet.add("QTUM");
		coinSet.add("RBX");
		coinSet.add("SJCX");
		coinSet.add("SNM");
		coinSet.add("SNT");
		coinSet.add("SRN");
		coinSet.add("STA");
		coinSet.add("STR");
		coinSet.add("STEEM");
		coinSet.add("STORJ");
		coinSet.add("SUB");
		coinSet.add("SUR");
		coinSet.add("TIME");
		coinSet.add("TKS");
		coinSet.add("TRCT");
		coinSet.add("TRX");
		coinSet.add("USDT");
		coinSet.add("WAVES");
		coinSet.add("WCT");
		coinSet.add("WGE");
		coinSet.add("WGO");
		coinSet.add("WGR");
		coinSet.add("XCP");
		coinSet.add("XEM");
		coinSet.add("XLM");
		coinSet.add("XMR");
		coinSet.add("XRP");
		coinSet.add("ZCL");
		coinSet.add("ZEC");
		coinSet.add("ZRC");
		
		
		return coinSet;
	}
	
	public static HashSet<String> getCashSet(){
		
		cashSet.add("AUD");
		cashSet.add("BRL");
		cashSet.add("CAD");
		cashSet.add("CNY");
		cashSet.add("EUR");
		cashSet.add("GBP");
		cashSet.add("IDR");
		cashSet.add("JPY");
		cashSet.add("KRW");
		cashSet.add("MXN");
		cashSet.add("PLN");
		cashSet.add("RUB");
		cashSet.add("THB");
		cashSet.add("TRY");
		cashSet.add("USD");
		cashSet.add("ZAR");
		
		return cashSet;
	}
}

