package com.zdx.rocketmq;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import com.zdx.common.LoadConfig;
import com.zdx.common.TickerFormat;
import com.zdx.common.TickerStandardFormat;
import com.zdx.demo.ToyConsumer;

import io.parallec.core.ParallecResponseHandler;
import io.parallec.core.ParallelClient;
import io.parallec.core.ParallelTaskBuilder;
import io.parallec.core.RequestProtocol;
import io.parallec.core.ResponseOnSingleTask;

public class TestRocketMQProducer {
	private static Logger logger = Logger.getLogger(TestRocketMQProducer.class);
	public static String serverUrl = "182.92.150.57:9876";
	public final static HashMap<String, Object> RESPONSE_CONTEXT = new HashMap<String, Object>();
	
	public static void main(String[] args) throws MQClientException, InterruptedException{
		String path = "C:\\Users\\zdx\\git\\ExchangeAgg\\conf\\ticker.json";
		TickerConfInfo tcConf = LoadConfig.loadTickerConf(path);
		List<String> targetHosts = tcConf.targetHosts;
		List<List<String>> replaceLists = tcConf.replaceLists;
		final Map<String, String> hostMap = tcConf.exchangeSymbolMap;
		final Map<String, String> pathMap = tcConf.pathSymbolMap;
		logger.info(tcConf.toString());
		ParallelClient pc = new ParallelClient();

		DefaultMQProducer producer = new DefaultMQProducer("WufengTest1");
		producer.setNamesrvAddr(serverUrl);		
		producer.start();
		RESPONSE_CONTEXT.put("producer", producer);

		logger.info("responseContext"+RESPONSE_CONTEXT);
		/*
		 .setReplaceVarMapToSingleTargetSingleVar("JOB_ID", Arrays.asList("api/v1/ticker.do?symbol=btc_usd", 
			"api/v1/ticker.do?symbol=eth_usd",
			"api/v1/ticker.do?symbol=ltc_usd"), "www.okcoin.com")
		 */
		ParallelTaskBuilder ptb = 
				pc.prepareHttpGet("/$JOB_ID")
				.setProtocol(RequestProtocol.HTTPS)
				.setHttpPort(443)
				.setPingTimeoutMillis(10000)
				.setSshConnectionTimeoutMillis(10000)
				.setTcpConnectTimeoutMillis(10000)
				.setTcpIdleTimeoutSec(10000)
				.setUdpIdleTimeoutSec(10000)
				.setReplaceVarMapToMultipleTarget("JOB_ID", replaceLists, targetHosts)
				.setResponseContext(RESPONSE_CONTEXT);
		logger.info("%%%%%%%%%%%%%%%%%%%"+ptb);
		boolean f1 = true;
		while (f1){
			ptb.execute(new ParallecResponseHandler(){
				@Override
				public void onCompleted(ResponseOnSingleTask res, Map<String, Object> responseContext) {
					logger.info("whilewhilewhilewhilewhile");
					Message msg = new Message();
					msg.setTopic("tickerTest");
					msg.setTags("TagA");
					logger.info("msgmsgmsg");
					TickerStandardFormat tsf = new TickerStandardFormat();
					String host = res.getRequest().getHostUniform();
					logger.info("host:"+host);
					String exchangeName = hostMap.get(host);
					tsf.exchangeName = exchangeName;
					String path = res.getRequest().getResourcePath().substring(1, res.getRequest().getResourcePath().length());
					
					logger.info("path:"+path);
					logger.info("====="+pathMap.get(path));
					String[] coinAB = pathMap.get(path).split("_");
					
					tsf.coinA = coinAB[0];
					tsf.coinB = coinAB[1];
					logger.info("coinA:"+tsf.coinA+" coinB:"+tsf.coinB);
					TickerFormat.format(res.getResponseContent(), exchangeName, tsf);
					logger.info("res:"+res);
					msg.setBody(tsf.toJsonString().getBytes());
					logger.info("tsf:"+tsf.toJsonString());
					try {
						logger.info("-");
						DefaultMQProducer producer = (DefaultMQProducer)responseContext.get("producer");
						producer.sendOneway(msg);
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
			});
			logger.info(" ===========================Done==============================" + pc.getRunningJobCount());
			f1 = (pc.getRunningJobCount() == 0);
		}
		producer.shutdown();
	}

}
