package com.zdx.rocketmq;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zdx.common.TickerFormat;
import com.zdx.common.TickerFormatBitfinex;
import com.zdx.common.TickerFormatOkcoin;

import io.parallec.core.ParallecResponseHandler;
import io.parallec.core.ParallelClient;
import io.parallec.core.ParallelTaskBuilder;
import io.parallec.core.RequestProtocol;
import io.parallec.core.ResponseOnSingleTask;

public class TestRocketMQProducer {
	public static String serverUrl = "182.92.150.57:9876";

	public static void main(String[] args) throws MQClientException, InterruptedException{
		ParallelClient pc = new ParallelClient();

		final HashMap<String, Object> responseContext = new HashMap<String, Object>();

		DefaultMQProducer producer = new DefaultMQProducer("WufengTest1");
		producer.setNamesrvAddr(serverUrl);		
		producer.start();
		responseContext.put("producer", producer);

		List<String> targetHosts = new ArrayList<String>(Arrays.asList(
				//"www.okcoin.com",
				"api.bitfinex.com",
				//"api.quadrigacx.com",
				"www.bitstamp.net"));
		List<List<String>> replaceLists = new ArrayList<List<String>>();

		/*replaceLists.add(Arrays.asList("api/v1/ticker.do?symbol=btc_usd", 
				"api/v1/ticker.do?symbol=eth_usd", 
				"api/v1/ticker.do?symbol=ltc_usd"));*/
		replaceLists.add(Arrays.asList(
				"v1/pubticker/ethbtc"
				//"v1/pubticker/zecbtc"
				));
		/*replaceLists.add(Arrays.asList("v2/ticker?book=btc_usd",
				"v2/ticker?book=eth_btc"));*/
		replaceLists.add(Arrays.asList(
				//"api/v2/ticker/btcusd",
				"api/v2/ticker/ethbtc"));
		
		final Map<String, String> hostMap = new HashMap<String, String>();
		//hostMap.put("www.okcoin.com", "okcoin.com");
		hostMap.put("api.bitfinex.com", "bitfinex");
		//hostMap.put("api.quadrigacx.com", "quadrigacx");
		hostMap.put("www.bitstamp.net", "bitstamp");
		
		final Map<String, String> pathMap = new HashMap<String, String>();
		//pathMap.put("/api/v1/ticker.do?symbol=btc_usd", "btc_usd");
		//pathMap.put("/api/v1/ticker.do?symbol=eth_usd", "eth_usd");
		//pathMap.put("/api/v1/ticker.do?symbol=ltc_usd", "ltc_usd");
		pathMap.put("/v1/pubticker/ethbtc", "eth_btc");
		//pathMap.put("/v1/pubticker/zecbtc", "zec_btc");
		//pathMap.put("/v2/ticker?book=btc_usd", "btc_usd");
		//pathMap.put("/v2/ticker?book=eth_btc", "eth_btc");
		//pathMap.put("api/v2/ticker/btcusd", "btc_usd");
		pathMap.put("api/v2/ticker/ethbtc", "eth_btc");
		final List<String> pathList = new LinkedList<String>();
		//pathList.add("/api/v1/ticker.do?symbol=btc_usd");
		//pathList.add("/api/v1/ticker.do?symbol=eth_usd");
		//pathList.add("/api/v1/ticker.do?symbol=ltc_usd");
		pathList.add("/v1/pubticker/ethbtc");
		//pathList.add("/v1/pubticker/zecbtc");
		//pathList.add("/v2/ticker?book=btc_usd");
		//pathList.add("/v2/ticker?book=eth_btc");
		//pathList.add("api/v2/ticker/btcusd");
		pathList.add("api/v2/ticker/ethbtc");
		
		responseContext.put("pathList", pathList);
		System.out.println("responseContext"+responseContext);
		/*
		 .setReplaceVarMapToSingleTargetSingleVar("JOB_ID", Arrays.asList("api/v1/ticker.do?symbol=btc_usd", 
			"api/v1/ticker.do?symbol=eth_usd",
			"api/v1/ticker.do?symbol=ltc_usd"), "www.okcoin.com")
		 */
		ParallelTaskBuilder ptb = 
				pc.prepareHttpGet("/$JOB_ID")
				.setProtocol(RequestProtocol.HTTPS)
				.setHttpPort(443)
				.setReplaceVarMapToMultipleTarget("JOB_ID", replaceLists, targetHosts)
				.setResponseContext(responseContext);
		System.out.println("%%%%%%%%%%%%%%%%%%%"+ptb);
		boolean f1 = true;
		while (f1){
			ptb.execute(new ParallecResponseHandler(){
				public void onCompleted(ResponseOnSingleTask res, Map<String, Object> responseContext) {
					Message msg = new Message();
					msg.setTopic("ticker");
					msg.setTags("TagA");

					TickerFormat tickerData = new TickerFormat();
					
					String host = res.getRequest().getHostUniform(); //www.okcoin.com
					String path = res.getRequest().getResourcePath(); ///api/v1/ticker.do?symbol=btc_usd
					System.out.println("11111111111111111111111111111111111111111111");
					System.out.println(host);
					System.out.println(path);
					System.out.println("22222222222222222222222222222222222222222222");
					tickerData.exchangeName = hostMap.get(host);
					String[] coinAB = pathMap.get(path).split("_");
					tickerData.coinA = coinAB[0];
					tickerData.coinB = coinAB[1];				

					if (host.contains("okcoin")){
						TickerFormatOkcoin.format(res.getResponseContent(), tickerData);
						System.out.println("okcoin:::::::::::::"+tickerData.toJsonString());
					} else if (host.contains("bitfinex")){
						TickerFormatBitfinex.format(res.getResponseContent(), tickerData);
						System.out.println("bitfinex:::::::::::::"+tickerData.toJsonString());
					} else if (host.contains("quadrigacx")){
						TickerFormatBitfinex.format(res.getResponseContent(), tickerData);
						System.out.println("quadrigacx:::::::::::::"+tickerData.toJsonString());
					}else if (host.contains("bitstamp")) {
						TickerFormatBitfinex.format(res.getResponseContent(), tickerData);
						System.out.println("bitstamp:::::::::::::"+tickerData.toJsonString());
					}
					msg.setBody(tickerData.toJsonString().getBytes());
					try {
						DefaultMQProducer producer = (DefaultMQProducer)responseContext.get("producer");
						producer.sendOneway(msg);
					} catch (MQClientException e) {
						System.out.println("Exception1 ==================================================================");
						e.printStackTrace();
					} catch (RemotingException e) {
						System.out.println("Exception2 ==================================================================");
						e.printStackTrace();
					} catch (InterruptedException e) {
						System.out.println("Exception3 ==================================================================");
						e.printStackTrace();				
					} 
				}
			});
			System.out.println(" ===========================Done==============================" + pc.getRunningJobCount());
			f1 = (pc.getRunningJobCount() == 0);
		}
		producer.shutdown();
	}

}
