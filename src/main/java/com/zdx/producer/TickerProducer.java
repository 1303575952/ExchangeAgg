package com.zdx.producer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;

import com.zdx.common.JsonFormatTool;
import com.zdx.common.LoadConfig;
import com.zdx.rocketmq.TickerConfInfo;

import io.parallec.core.ParallelClient;
import io.parallec.core.ParallelTaskBuilder;
import io.parallec.core.RequestProtocol;

public class TickerProducer {
	final static HashMap<String, String> failedTickerMap = new HashMap<String, String>();
	final static HashMap<String, Object> responseContext = new HashMap<String, Object>();
	final static long startTime = System.currentTimeMillis();
	final static String destDir = System.getProperty("user.dir") + File.separator + "Currency" + File.separator + startTime;
	private static Map<Object, Object> tickerConf = new HashMap<Object, Object>();
	static List<String> targetHosts = new ArrayList<String>();
	static List<List<String>> replaceLists = new ArrayList<List<String>>();
	static Map<String, String> hostMap = new HashMap<String, String>();
	static Map<String, String> pathMap = new HashMap<String, String>();
	static ArrayList<String> tickerNames = new ArrayList<String>();
	static ArrayList<String> tickerNamesLeft = new ArrayList<String>();
	static String serverUrl = "";
	static String tickerInfoPath = "";

	public static void loadConf(String tickerConfPath){
		tickerConf = LoadConfig.LoadConf(tickerConfPath);
		serverUrl = String.valueOf(tickerConf.get("ServerUrl"));
		tickerInfoPath = String.valueOf(tickerConf.get("TickerInfoPath"));

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
		System.out.println("-------------1---------------");
		System.out.println(tickerNames.toString());
		System.out.println("-------------2---------------");
	}

	public static void execute(String tickerConfPath) throws InterruptedException{
		loadConf(tickerConfPath);

		DefaultMQProducer producer = new DefaultMQProducer("WufengTest1");		
		producer.setNamesrvAddr(serverUrl);		
		try {
			producer.start();

			//FileHandler.writeFile(destDir + File.separator + "failed.json", failedToString());
			//producer.shutdown();
		} catch (MQClientException e) {
			e.printStackTrace();
		}
		responseContext.put("producer", producer);
		responseContext.put("hostMap", hostMap);
		responseContext.put("pathMap", pathMap);
		while (true){
			oneFullBathWithoutRetry();
			try {
				Thread.sleep(20000);
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
					String status = failedTickerMap.get(key);
					if (!failedTickerMap.containsKey(key) || !(status.contains("code") || "OtherError".equals(status)) ){
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
			System.out.println(tickerNamesLeft.toString());
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
				.setResponseContext(responseContext);
		responseContext.put("startTime", startTime);
		responseContext.put("failedTickerMap", failedTickerMap);

		TickerProducerHandler tpHandler = new TickerProducerHandler(); 
		ptb.execute(tpHandler);
	}

	public static String failedToString(){
		StringBuffer sb = new StringBuffer();
		for (Entry<String, String> entry : failedTickerMap.entrySet()){
			String key = entry.getKey();
			String val = entry.getValue();
			if (!"True".equals(val)){
				sb.append("{");
				sb.append("\"currency\":\"" + key + "\",");
				sb.append("\"status\":\"" + val + "\"},");
			}
		}
		String sb_tmp = sb.toString();
		if (!sb_tmp.isEmpty()){
			sb_tmp = sb_tmp.substring(0, sb_tmp.lastIndexOf(","));
		}
		sb_tmp = "[" + sb_tmp + "]";
		sb_tmp = JsonFormatTool.formatJson(sb_tmp);
		return sb_tmp;
	}
}
