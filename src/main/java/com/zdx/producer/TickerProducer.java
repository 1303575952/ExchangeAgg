package com.zdx.producer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;

import com.zdx.common.JsonFormatTool;

import io.parallec.core.ParallelClient;
import io.parallec.core.ParallelTaskBuilder;
import io.parallec.core.RequestProtocol;

public class TickerProducer {
	private static Logger logger = Logger.getLogger(TickerProducer.class);
	final static HashMap<String, String> FAILED_TICKER_MAP = new HashMap<String, String>();
	final static HashMap<String, Object> RESPONSE_CONTEXT = new HashMap<String, Object>();
	final static long START_TIME = System.currentTimeMillis();

	static ArrayList<String> tickerNamesLeft = new ArrayList<String>();

	public static TickerProducerConf tpConf = new TickerProducerConf();

	public static void main(String[] args) throws InterruptedException {
		if (args.length == 0) {
			System.err.println("Please input configuration file");
			System.exit(-1);
		}
		String confPath = args[0];
		TickerProducerConf.loadConfigFromFile(confPath);
		execute();
	}

	public static void execute() throws InterruptedException{
		DefaultMQProducer producer = new DefaultMQProducer();		
		producer.setNamesrvAddr(TickerProducerConf.serverUrl);	
		producer.setProducerGroup(TickerProducerConf.producerGroup);
		try {
			producer.start();
			//FileHandler.writeFile(destDir + File.separator + "failed.json", failedToString());
			//producer.shutdown();
		} catch (MQClientException e) {
			e.printStackTrace();
		}
		RESPONSE_CONTEXT.put("producer", producer);
		RESPONSE_CONTEXT.put("hostMap", TickerProducerConf.exchangeSymbolMap);
		RESPONSE_CONTEXT.put("pathMap", TickerProducerConf.pathSymbolMap);
		RESPONSE_CONTEXT.put("influxURL", TickerProducerConf.influxURL);
		RESPONSE_CONTEXT.put("influxDbName", TickerProducerConf.influxDbName);
		RESPONSE_CONTEXT.put("influxRpName", TickerProducerConf.influxRpName);
		RESPONSE_CONTEXT.put("validInterval", TickerProducerConf.validInterval);
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
		oneBatch(TickerProducerConf.targetHosts, TickerProducerConf.replaceLists);
	}

	public static void oneFullBathWithRetry(){
		int minError = Integer.MAX_VALUE;
		boolean done = false;
		while (!done){
			List<String> targetHostsLeft = new ArrayList<String>();
			List<List<String>> replaceListsLeft = new ArrayList<List<String>>();
			for (int i = 0; i < TickerProducerConf.targetHosts.size(); i ++){
				String host = TickerProducerConf.targetHosts.get(i);
				List<String> urls = TickerProducerConf.replaceLists.get(i);
				List<String> replaceListsLeft2 = new ArrayList<String>();
				for (String url : urls){
					TickerProducerConf.tickerNames.add(host + "/" + url);
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
