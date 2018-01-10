package com.zdx.test;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.java_websocket.drafts.Draft_6455;

import com.zdx.pair.BuildTopPairs;
import com.zdx.pair.PairStormConf;
import com.zdx.producer.TickerProducer;
import com.zdx.producer.TickerProducerConf;
//import com.zdx.tri.TickerIndexBuilder;
import com.zdx.ticker.TickerStandardFormat;

public class Test {
	private static Logger logger = Logger.getLogger(Test.class);
	public static void main(String[] args) throws InterruptedException {
		long t1 = 1515508088875L;
		long t2 = 1515508089875L;
		
		System.out.println(t1 - t2);
	}
	


}

