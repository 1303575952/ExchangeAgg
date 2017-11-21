package com.zdx.pair;

import java.util.ArrayList;
import java.util.HashMap;

import com.zdx.common.TickerStandardFormat;

public class PairBolt {
	public double threshold = 0.05;
	public HashMap<String, LowestPrice> pairPriceMap = new HashMap<String, LowestPrice> ();
	PariConfig pc = new PariConfig();

	public void open(){
		String filePath1 = "C:\\Users\\zdx\\git\\ExchangeAgg\\conf\\topVol100M.json";
		String filePath2 = "C:\\Users\\zdx\\git\\ExchangeAgg\\conf\\ToyTopVol100MPair.json";
		pc.initPairConfig(filePath1, filePath2);
	}
	public void updatePairPrice(TickerStandardFormat tsf){
		String pair = tsf.coinA.toLowerCase() + "_" + tsf.coinB.toLowerCase();
		String exchangePairName = tsf.exchangeName.toLowerCase() + "_" + pair;
		if (pc.pairFourthMap.containsKey(exchangePairName)){
			ArrayList<String> tmp1 = pc.pairFourthMap.get(exchangePairName);
			for (String x : tmp1){
				if (pc.fourthPriceMap.containsKey(x)){
					EnterPrice ep = pc.fourthPriceMap.get(x);
					if (ep.exchangeName1.equals(tsf.exchangeName.toLowerCase())){
						ep.bid1 = tsf.bid;
						ep.ask1 = tsf.ask;
					} else if (ep.exchangeName2.equals(tsf.exchangeName.toLowerCase())){
						ep.bid2 = tsf.bid;
						ep.ask2 = tsf.ask;
					}
					if (ep.ask2 > 0.0){
						ep.profit = ep.bid1 / ep.ask2;
					}
					if (ep.profit > 1 + threshold ){
						System.out.println("---1-" + x);
						System.out.println("    ---2-" + ep.profit);
						String[] t1 = x.split("@@");						
						LowestPricePair lpp = getPairResetInfo(t1[0], t1[1]);
						System.out.println("    ---3-" + lpp.resetFee);
					}
					pc.fourthPriceMap.put(x, ep);
				}

			}
		}
	}
	public void updatePrice(TickerStandardFormat tsf){
		String pair = tsf.coinA.toLowerCase() + "_" + tsf.coinB.toLowerCase();
		//System.out.println("---1-" + pair);
		String exchangePairName = tsf.exchangeName.toLowerCase() + "_" + pair;
		//System.out.println("---2-" + exchangePairName);
		//用ticker直接值更新最低价，如BTC-USD
		updateLowestPrice(exchangePairName, pair, tsf.ask);
		if (pc.pairPathMap.containsKey(exchangePairName)){
			ArrayList<String> pathNameList = pc.pairPathMap.get(exchangePairName);
			//System.out.println("---9-" + pathNameList.toString());
			for (String pathName : pathNameList){
				//System.out.println("---10-" + pathName);
				PathPrice pp =  pc.pathPriceMap.get(pathName);
				if (pair.equals(pp.path1)){
					pp.bid1 = tsf.bid;
					pp.ask1 = tsf.ask;
				}
				if (pair.equals(pp.path2)){
					pp.bid2 = tsf.bid;
					pp.ask2 = tsf.ask;
				}
				//System.out.println("---11-" + pp.ask1);
				//System.out.println("---12-" + pp.ask2);
				pp.price = (1 + pp.fee1) * pp.ask1 * (1 + pp.fee2) * pp.ask2;				
				//更新etc-eth-usd间接价格
				pc.pathPriceMap.put(pathName, pp);
				//用间接价格更新最低价，如etc-usd
				String exchangePairName2 = tsf.exchangeName.toLowerCase() + "_" + pp.pair;
				updateLowestPrice(exchangePairName2, pp.pair, pp.price);
			}
		}

	}
	public void updateLowestPrice(String tickerName, String lowestPath, double lowestPrice){
		if (pairPriceMap.containsKey(tickerName)){
			LowestPrice lp = pairPriceMap.get(tickerName);
			if (lowestPrice < lp.lowestPrice){
				lp.lowestPath = lowestPath;
				lp.lowestPrice = lowestPrice;
				//System.out.println("    ---3-" + lp.lowestPath);
				//System.out.println("    ---4-" + lp.lowestPrice);
				//System.out.println("    ---5-" + tickerName);
			}
			pairPriceMap.put(tickerName, lp);
		} else {
			LowestPrice lp = new LowestPrice();
			lp.lowestPath = lowestPath;
			lp.lowestPrice = lowestPrice;
			//System.out.println("    ---6-" + lp.lowestPath);
			//System.out.println("    ---7-" + lp.lowestPrice);
			pairPriceMap.put(tickerName, lp);
			//System.out.println("    ---8-" + tickerName);			
		}
	}

	public LowestPricePair getPairResetInfo(String exchangeTicker1, String exchangeTicker2){
		LowestPricePair lpp = new LowestPricePair();		
		LowestPrice lp1 = new LowestPrice();
		LowestPrice lp2 = new LowestPrice();
		if (pairPriceMap.containsKey(exchangeTicker1) && pairPriceMap.containsKey(exchangeTicker2)){
			lp1 = pairPriceMap.get(exchangeTicker1);
			lp2 = pairPriceMap.get(exchangeTicker2);
		}
		lpp.lowestPath1 = lp1.lowestPath;
		lpp.lowestPrice1 = lp1.lowestPrice;
		lpp.lowestPath2 = lp2.lowestPath;
		lpp.lowestPrice2 = lp2.lowestPrice;
		if (lpp.lowestPrice2 < Integer.MAX_VALUE){
			lpp.resetFee = lpp.lowestPrice1 / lpp.lowestPrice2 - 1;
		}
		return lpp;
	}


}