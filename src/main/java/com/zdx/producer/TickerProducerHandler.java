package com.zdx.producer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

import com.zdx.common.DataFormat;
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

			logger.debug("====================Begin Handle Message====================");
			String influxURL = (String)responseContext.get("influxURL");
			String influxDbName = (String)responseContext.get("influxDbName");
			String influxRpName = (String)responseContext.get("influxRpName");
			InfluxDB influxDB = InfluxDBFactory.connect(influxURL);
			if (!influxDB.databaseExists(influxDbName)){
				logger.debug("==================================================================Database" + influxDbName + " not Exist");
				influxDB.createDatabase(influxDbName);
			}
			influxDB.setDatabase(influxDbName);
			influxDB.createRetentionPolicy(influxRpName, influxDbName, "30d", "30m", 2, true);



			TickerStandardFormat tsf = new TickerStandardFormat();
			String host = res.getRequest().getHostUniform();
			String exchangeName = hostMap.get(host);
			tsf.exchangeName = exchangeName;
			String path = res.getRequest().getResourcePath().substring(1, res.getRequest().getResourcePath().length());
			String[] coinAB = pathMap.get(path).split("_");
			tsf.coinA = coinAB[0];
			tsf.coinB = coinAB[1];
			TickerFormat.format(res.getResponseContent(), exchangeName, tsf);

			logger.debug("======API host="+host);
			logger.debug("======API path="+path);

			int hashCode = tsf.hashCodeWithoutTimeStamp();

			logger.debug("======API Response="+res);			
			logger.debug("======TickerInfo="+tsf.toJsonString());
			String hashCodeOld = "";
			if (failedTickerMap.containsKey(url)){
				hashCodeOld = failedTickerMap.get(url);
			}
			logger.debug("======hashCodeOld=:"+hashCodeOld);
			logger.debug("======hashCode= " + hashCode);
			logger.debug((hashCodeOld == null)||(!hashCodeOld.equals("code="+hashCode)));
			if ((hashCodeOld == null)||(!hashCodeOld.equals("code="+hashCode))){
				failedTickerMap.put(url, "code=" + hashCode);

				String tableName = DataFormat.removeShortTerm(tsf.exchangeName);
				Point point1 = Point.measurement(tableName)
						.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
						.addField("exchangeName", tsf.exchangeName)	
						.tag("coinA", tsf.coinA)
						.tag("coinB", tsf.coinB)
						.addField("bid", tsf.bid)
						.addField("ask", tsf.ask)
						.addField("low", tsf.low)
						.addField("high", tsf.high)
						.addField("midUSD", tsf.midUSD)
						.build();
				influxDB.write(influxDbName, influxRpName, point1);
				Query query = new Query("SELECT * FROM " + tableName + " GROUP BY *", influxDbName);
				QueryResult result = influxDB.query(query);
				if (result.getResults().get(0).getSeries().get(0).getTags().isEmpty() == true){
					logger.debug("===========================InfluxDB Insert Failed=======================================");
					influxDB.close();
					influxDB = InfluxDBFactory.connect(influxURL);
				} else {
					logger.debug("===========================InfluxDB Insert Sucess=======================================");
				}

				try {
					Message msg = new Message();
					msg.setTopic("ticker_" + tsf.exchangeName.toLowerCase());
					msg.setTags("TagA");
					msg.setBody(tsf.toJsonString().getBytes());
					logger.debug("======producer send message : " + tsf.toJsonString());
					DefaultMQProducer producer = (DefaultMQProducer)responseContext.get("producer");
					String tmp = tsf.exchangeName + "-" + tsf.coinA + "-" + tsf.coinB;
					int id = Math.abs(tmp.hashCode()%10);
					logger.debug("======Message ID=" + id);
					producer.sendOneway(msg,new MessageQueueSelector() {
						@Override
						public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
							int index = (Integer) arg % mqs.size();
							logger.debug("======Message quene info = " + mqs.get(index));
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
			logger.debug("====================End Handle Message====================");
		} else {
			failedTickerMap.put(url,"OtherError");
			return;
		}
	}
}

