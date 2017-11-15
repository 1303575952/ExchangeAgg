package com.zdx.producer;

import java.util.HashMap;
import java.util.Map;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import com.zdx.common.TickerFormat;
import com.zdx.common.TickerStandardFormat;

import io.parallec.core.ParallecResponseHandler;
import io.parallec.core.ResponseOnSingleTask;

public class TickerProducerHandler implements ParallecResponseHandler {
	@SuppressWarnings("unchecked")
	public void onCompleted(ResponseOnSingleTask res, Map<String, Object> responseContext) {

		Long startTime = (Long) responseContext.get("startTime");

		HashMap<String, String> failedTickerMap = (HashMap<String, String>) responseContext.get("failedTickerMap");
		Map<String, String> hostMap = (Map<String, String>) responseContext.get("hostMap");
		Map<String, String> pathMap = (Map<String, String>) responseContext.get("pathMap");


		String hostName = res.getRequest().getHostUniform();
		String pathName = res.getRequest().getResourcePath();
		String url = hostName + pathName;
		System.out.println("url == " + url);
		if (res.getError()){
			failedTickerMap.put(url,"ParallecError");
			return;
		} else if (res.getStatusCodeInt() != 200){
			failedTickerMap.put(url,"StatusNot200");
			return;
		} else if (res.getStatusCodeInt() == 200){			
			System.out.println("msgmsgmsg");
			TickerStandardFormat tsf = new TickerStandardFormat();
			String host = res.getRequest().getHostUniform();
			String exchangeName = hostMap.get(host);
			tsf.exchangeName = exchangeName;
			String path = res.getRequest().getResourcePath().substring(1, res.getRequest().getResourcePath().length());
			String[] coinAB = pathMap.get(path).split("_");
			tsf.coinA = coinAB[0];
			tsf.coinB = coinAB[1];
			TickerFormat.format(res.getResponseContent(), exchangeName, tsf);
			
			System.out.println("host:"+host);
			System.out.println("path:"+path);
			System.out.println("====="+pathMap.get(path));
			System.out.println("coinA:"+tsf.coinA+" coinB:"+tsf.coinB);
			
			
			int hashCode = tsf.hashCodeWithoutTimeStamp();
			System.out.println("hashCode:"+hashCode);
			System.out.println("res:"+res);			
			System.out.println("tsf:"+tsf.toJsonString());			
			String hashCodeOld = failedTickerMap.get(url);			
			if (!hashCodeOld.equals("code=" + hashCode)){			
				failedTickerMap.put(url,"code=" + hashCode);
				try {
					Message msg = new Message();
					msg.setTopic("tickerTest");
					msg.setTags("TagA");
					msg.setBody(tsf.toJsonString().getBytes());
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

		} else {
			failedTickerMap.put(url,"OtherError");
			return;
		}
	}
}

