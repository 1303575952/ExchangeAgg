package com.zdx.producer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;

import com.zdx.common.TickerFormat;
import com.zdx.common.TickerStandardFormat;
import com.zdx.demo.ToyConsumer;

import io.parallec.core.ParallecResponseHandler;
import io.parallec.core.ResponseOnSingleTask;

public class TickerProducerHandler implements ParallecResponseHandler {
	private static Logger logger = Logger.getLogger(TickerProducerHandler.class);
	@SuppressWarnings("unchecked")
	@Override
	public void onCompleted(ResponseOnSingleTask res, Map<String, Object> responseContext) {

		HashMap<String, String> failedTickerMap = (HashMap<String, String>) responseContext.get("failedTickerMap");
		Map<String, String> hostMap = (Map<String, String>) responseContext.get("hostMap");
		Map<String, String> pathMap = (Map<String, String>) responseContext.get("pathMap");


		String hostName = res.getRequest().getHostUniform();
		String pathName = res.getRequest().getResourcePath();
		String url = hostName + pathName;
		logger.info("url == " + url);
		if (res.getError()){
			failedTickerMap.put(url,"ParallecError");
			return;
		} else if (res.getStatusCodeInt() != 200){
			failedTickerMap.put(url,"StatusNot200");
			return;
		} else if (res.getStatusCodeInt() == 200){			
			logger.info("msgmsgmsg");
			TickerStandardFormat tsf = new TickerStandardFormat();
			String host = res.getRequest().getHostUniform();
			String exchangeName = hostMap.get(host);
			tsf.exchangeName = exchangeName;
			String path = res.getRequest().getResourcePath().substring(1, res.getRequest().getResourcePath().length());
			String[] coinAB = pathMap.get(path).split("_");
			tsf.coinA = coinAB[0];
			tsf.coinB = coinAB[1];
			TickerFormat.format(res.getResponseContent(), exchangeName, tsf);
			
			logger.info("host:"+host);
			logger.info("path:"+path);
			logger.info("====="+pathMap.get(path));
			logger.info("coinA:"+tsf.coinA+" coinB:"+tsf.coinB);
			
			
			int hashCode = tsf.hashCodeWithoutTimeStamp();
			logger.info("hashCode:"+hashCode);
			logger.info("res:"+res);			
			logger.info("tsf:"+tsf.toJsonString());			
			String hashCodeOld = failedTickerMap.get(url);
			logger.info("hashCodeOld:"+hashCodeOld);
			logger.info("111 " + hashCode);
			logger.info((hashCodeOld == null)||(!hashCodeOld.equals("code="+hashCode)));
			if ((hashCodeOld == null)||(!hashCodeOld.equals("code="+hashCode))){
				logger.info("222 " + hashCode);
				failedTickerMap.put(url,"code=" + hashCode);
				try {
					Message msg = new Message();
					msg.setTopic("tickerInfo");
					msg.setTags("TagA");
					msg.setBody(tsf.toJsonString().getBytes());
					DefaultMQProducer producer = (DefaultMQProducer)responseContext.get("producer");
					String tmp = tsf.coinA + "-" + tsf.coinB;
					int id = Math.abs(tmp.hashCode()%10);
					producer.sendOneway(msg,new MessageQueueSelector() {
						
						@Override
						public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
							Integer id = (Integer) arg;
							logger.info("mqs.size" + mqs.size());
							int index = id % mqs.size();
							logger.info("index" + index);
							logger.info("mqs.get(index)" + mqs.get(index));
							return mqs.get(index);
						}
					}, id);
				} catch (MQClientException e) {
					logger.info("Exception1 ==================================================================");
					e.printStackTrace();
				} catch (RemotingException e) {
					logger.info("Exception2 ==================================================================");
					e.printStackTrace();
				} catch (InterruptedException e) {
					logger.info("Exception3 ==================================================================");
					e.printStackTrace();				
				}
			}

		} else {
			failedTickerMap.put(url,"OtherError");
			return;
		}
	}
}

