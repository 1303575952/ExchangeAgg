package com.zdx.tri;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import com.alibaba.fastjson.JSONObject;
import com.zdx.common.TickerStandardFormat;

import io.parallec.core.ParallecResponseHandler;
import io.parallec.core.ParallelClient;
import io.parallec.core.ParallelTaskBuilder;
import io.parallec.core.RequestProtocol;
import io.parallec.core.ResponseOnSingleTask;

public class APIDataGet {
	public static HashMap<String,ArrayList<ArrayList<JSONObject>>> dataGet(HashMap<String,ArrayList<ArrayList<String>>> allExchAllTriGetString){
		HashMap<String,ArrayList<ArrayList<JSONObject>>> allExchAllTriDataGet = new HashMap<String,ArrayList<ArrayList<JSONObject>>>();
		ParallelClient pc = new ParallelClient();
		HashMap<String, Object> responseContext = new HashMap<String, Object>();
		List<String> targetHosts = new ArrayList<String>();
		List<List<String>> replaceLists = new ArrayList<List<String>>();
		for(String key:allExchAllTriGetString.keySet()){
			ArrayList<ArrayList<String>> value = allExchAllTriGetString.get(key);
			List<String> oneExchGetString = new ArrayList<String>();
			for(int i=0;i<value.size();i++){
				oneExchGetString.add(value.get(i).get(0));
				oneExchGetString.add(value.get(i).get(1));
				oneExchGetString.add(value.get(i).get(2));
			}
			replaceLists.add(oneExchGetString);
		}
		
		ParallelTaskBuilder ptb = 
				pc.prepareHttpGet("/$JOB_ID")
				.setProtocol(RequestProtocol.HTTPS)
				.setHttpPort(443)
				.setReplaceVarMapToMultipleTarget("JOB_ID", replaceLists, targetHosts)
				//.setResponseContext(responseContext)
				;
		System.out.println("%%%%%%%%%%%%%%%%%%%"+ptb);
		boolean f1 = true;
		while (f1){
			ptb.execute(new ParallecResponseHandler(){
				public void onCompleted(ResponseOnSingleTask res, Map<String, Object> responseContext) {
					Message msg = new Message();
					msg.setTopic("ticker");
					msg.setTags("TagA");

					TickerStandardFormat tickerData = new TickerStandardFormat();

					String host = res.getRequest().getHostUniform(); //www.okcoin.com
					String path = res.getRequest().getResourcePath(); ///api/v1/ticker.do?symbol=btc_usd
					System.out.println("11111111111111111111111111111111111111111111");
					System.out.println(host);
					System.out.println(path);
					System.out.println("22222222222222222222222222222222222222222222");
					//tickerData.exchangeName = hostMap.get(host);
					//String[] coinAB = pathMap.get(path).split("_");
					//tickerData.coinA = coinAB[0];
					//tickerData.coinB = coinAB[1];				
					System.out.println(tickerData.coinA);
					
					System.out.println(tickerData.coinB);
					if (host.contains("okcoin")){
						System.out.println("***");
						System.out.println(res.getResponseContent());
						System.out.println("***");
						//TickerFormat.format(res.getResponseContent(), tickerData, hostMap.get(host));
						System.out.println("okcoin:::::::::::::"+tickerData.toJsonString());
					} else if (host.contains("bitfinex")){
						//TickerFormatBitfinex.format(res.getResponseContent(), tickerData);
						System.out.println("bitfinex:::::::::::::"+tickerData.toJsonString());
					} else if (host.contains("quadrigacx")){
						//TickerFormatQuadrigacx.format(res.getResponseContent(), tickerData);
						System.out.println("quadrigacx:::::::::::::"+tickerData.toJsonString());
					} 		else if (host.contains("bitstamp")) {
						System.out.println("************************");
						System.out.println(res.getResponseContent());
						System.out.println("*************");
						//TickerFormatBitstamp.format(res.getResponseContent(), tickerData);
						System.out.println("bitstamp:::::::::::::"+tickerData.toJsonString());
					}
					msg.setBody(tickerData.toJsonString().getBytes());
					try {
						System.out.println("-");
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
		
		
		return allExchAllTriDataGet;
	}
	public static void main(String[] args) {
		
	}
}
