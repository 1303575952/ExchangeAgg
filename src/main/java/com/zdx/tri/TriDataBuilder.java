package com.zdx.tri;

import java.io.File;

import org.apache.log4j.Logger;

public class TriDataBuilder {
	private static Logger logger = Logger.getLogger(TriDataBuilder.class);
	
	public static void main(String[] args){
		if (args.length == 0) {
			logger.error("Please input Pair file");
			System.exit(-1);
		}
		//t1.json
		String pairPath = args[0];
		if (!pairPath.contains(File.separator)){
			logger.error("Please input Pair file");
			System.exit(-1);
		}
		String pathName = pairPath.substring(0, pairPath.lastIndexOf(File.separator));
		String fileName = pairPath.substring(pairPath.lastIndexOf(File.separator) + 1, pairPath.lastIndexOf("."));

		//t2.json
		String triPath = pathName + File.separator + fileName + "_02.json";
		//t3.json
		String tickerIndexPath = pathName + File.separator + fileName + "_03.json";
		TriListBuilder.buildTriListFromPairFile(pairPath, triPath);
		TickerIndexBuilder tib = new TickerIndexBuilder();
		tib.buildIndexFromFile(triPath);
		tib.saveToFile(tickerIndexPath);
	}
}
