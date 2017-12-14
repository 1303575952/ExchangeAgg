package com.zdx.producer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;

import com.zdx.common.JsonFormatTool;
import com.zdx.common.LoadConfig;
import com.zdx.demo.ToyConsumer;
import com.zdx.rocketmq.TickerConfInfo;

import io.parallec.core.ParallelClient;
import io.parallec.core.ParallelTaskBuilder;
import io.parallec.core.RequestProtocol;

public class TickerProducer {
	private static Logger logger = Logger.getLogger(TickerProducer.class);
	final static HashMap<String, String> FAILED_TICKER_MAP = new HashMap<String, String>();
	final static HashMap<String, Object> RESPONSE_CONTEXT = new HashMap<String, Object>();
	final static long START_TIME = System.currentTimeMillis();	
	private static Map<Object, Object> tickerConf = new HashMap<Object, Object>();
	static List<String> targetHosts = new ArrayList<String>();
	static List<List<String>> replaceLists = new ArrayList<List<String>>();
	static Map<String, String> hostMap = new HashMap<String, String>();
	static Map<String, String> pathMap = new HashMap<String, String>();
	static ArrayList<String> tickerNames = new ArrayList<String>();
	static ArrayList<String> tickerNamesLeft = new ArrayList<String>();
	static String serverUrl = "";
	static String tickerInfoPath = "";
	static String producerGroup = "";

	public static void loadConf(String tickerConfPath){
		tickerConf = LoadConfig.loadConf(tickerConfPath);
		serverUrl = String.valueOf(tickerConf.get("GatewayURL"));
		tickerInfoPath = String.valueOf(tickerConf.get("TickerInfoPath"));
		producerGroup = String.valueOf(tickerConf.get("producerGroup"));
		
		TickerConfInfo tcConf = LoadConfig.loadTickerConf(tickerInfoPath );
		targetHosts = tcConf.targetHosts;
		replaceLists = tcConf.replaceLists;
		hostMap = tcConf.exchangeSymbolMap;
		pathMap = tcConf.pathSymbolMap;
		for (int i = 0; i < targetHosts.size(); i ++){
			String host = targetHosts.get(i);
			List<String> urls = replaceLists.get(i);
			for (String url : urls){
				tickerNames.add(host + "/" + url);
			}
		}
		logger.info("-------------1---------------");
		logger.info(tickerNames.toString());
		logger.info("-------------2---------------");
	}

	public static void execute(String tickerConfPath) throws InterruptedException{
		loadConf(tickerConfPath);

		DefaultMQProducer producer = new DefaultMQProducer(producerGroup);		
		producer.setNamesrvAddr(serverUrl);		
		try {
			producer.start();

			//FileHandler.writeFile(destDir + File.separator + "failed.json", failedToString());
			//producer.shutdown();
		} catch (MQClientException e) {
			e.printStackTrace();
		}
		RESPONSE_CONTEXT.put("producer", producer);
		RESPONSE_CONTEXT.put("hostMap", hostMap);
		RESPONSE_CONTEXT.put("pathMap", pathMap);
		while (true){
			oneFullBathWithoutRetry();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}



	}
	public static void oneFullBathWithoutRetry(){
		
		oneBatch(targetHosts, replaceLists);
		
	}
	
	public static void oneFullBathWithRetry(){
		int minError = Integer.MAX_VALUE;
		boolean done = false;
		while (!done){
			List<String> targetHostsLeft = new ArrayList<String>();
			List<List<String>> replaceListsLeft = new ArrayList<List<String>>();
			for (int i = 0; i < targetHosts.size(); i ++){
				String host = targetHosts.get(i);
				List<String> urls = replaceLists.get(i);
				List<String> replaceListsLeft2 = new ArrayList<String>();
				for (String url : urls){
					tickerNames.add(host + "/" + url);
					String key = host + "/" + url;
					String status = FAILED_TICKER_MAP.get(key);
					if (!FAILED_TICKER_MAP.containsKey(key) || !(status.contains("code") || "OtherError".equals(status)) ){
						replaceListsLeft2.add(url);
						tickerNamesLeft.add(host + "/" + url);
					}
				}
				targetHostsLeft.add(host);
				replaceListsLeft.add(replaceListsLeft2);
			}
			if (targetHostsLeft.isEmpty()){
				done = true;
			}
			if (tickerNamesLeft.size() < minError){
				minError = tickerNamesLeft.size();
			} else if (tickerNamesLeft.size() == minError){
				done = true;//两轮结果一样，
			}
			oneBatch(targetHostsLeft, replaceListsLeft);
			logger.info(tickerNamesLeft.toString());
			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void oneBatch(List<String> targetHostsLeft, List<List<String>> replaceListsLeft){
		ParallelClient pc = new ParallelClient();		
		ParallelTaskBuilder ptb = pc.prepareHttpGet("/$JOB_ID")
				.setProtocol(RequestProtocol.HTTPS)
				.setHttpPort(443)
				.setReplaceVarMapToMultipleTarget("JOB_ID", replaceListsLeft, targetHostsLeft)
				.setResponseContext(RESPONSE_CONTEXT);
		RESPONSE_CONTEXT.put("startTime", START_TIME);
		RESPONSE_CONTEXT.put("failedTickerMap", FAILED_TICKER_MAP);

		TickerProducerHandler tpHandler = new TickerProducerHandler(); 
		ptb.execute(tpHandler);
	}

	public static String failedToString(){
		StringBuffer sb = new StringBuffer();
		for (Entry<String, String> entry : FAILED_TICKER_MAP.entrySet()){
			String key = entry.getKey();
			String val = entry.getValue();
			if (!"True".equals(val)){
				sb.append("{");
				sb.append("\"currency\":\"" + key + "\",");
				sb.append("\"status\":\"" + val + "\"},");
			}
		}
		String sbTmp = sb.toString();
		if (!sbTmp.isEmpty()){
			sbTmp = sbTmp.substring(0, sbTmp.lastIndexOf(","));
		}
		sbTmp = "[" + sbTmp + "]";
		sbTmp = JsonFormatTool.formatJson(sbTmp);
		return sbTmp;
	}
}
