package com.zdx.pair;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zdx.common.TickerFormat;
import com.zdx.demo.ToyConsumer;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;

public class PairBolt2 implements IRichBolt{

	/**
	 * 
	 */
	private static Logger logger = Logger.getLogger(PairBolt2.class);
	private static final long serialVersionUID = 1L;
	protected OutputCollector collector;
	Map<String, Object> coin2coinORcoin2cashDetail = new HashMap<String,Object>();
	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector  = collector;
		
	}

	@Override
	public void execute(Tuple tuple) {
		logger.info("Exception22 111111111111111111111111111111111111111111111111111111111111111111");
		logger.info("Exception22 111111111111111111111111111111111111111111111111111111111111111111");
		logger.info("Exception22 111111111111111111111111111111111111111111111111111111111111111111");
		logger.info(tuple.getValue(0));
		logger.info(tuple.getValue(1));
		String key = tuple.getValue(0).toString();
		JSONObject value = JSON.parseObject((String) tuple.getValue(1));
		logger.info(value);
		if(coin2coinORcoin2cashDetail.containsKey(key)){
			//TODO 拿到该key对应value和新来的value对比更新
			JSONObject oldPair = (JSONObject) coin2coinORcoin2cashDetail.get(key);
			JSONArray oldPairArray = oldPair.getJSONArray("data");
			JSONArray newPairArray = value.getJSONArray("data");
			logger.info("oldPairArray size:"+oldPairArray.size());
			logger.info("newPairArray size:"+newPairArray.size());
			for(int i2 = 0;i2<newPairArray.size();i2++){
				int count = 0;//记录当前是和oldPairArray的第几个比较
				JSONObject tempNew = (JSONObject) newPairArray.get(i2);
				logger.info("newPairArray"+"第"+i2+"个"+tempNew);
				String exchANew = tempNew.get("exchA").toString();
				String exchBNew = tempNew.get("exchB").toString();
				for(int i1=0;i1<oldPairArray.size();i1++){
					JSONObject tempOld = (JSONObject) oldPairArray.get(i1);
					logger.info("oldPairArray"+"第"+i1+"个"+tempOld);
					String exchAOld = tempOld.get("exchA").toString();
					String exchBOld = tempOld.get("exchB").toString();
					if(exchAOld.equals(exchANew)&&exchBOld.equals(exchBNew)){//host相同，则替换
						logger.info("替换exchAOld:"+exchAOld+"exchANew"+exchANew+"exchBOld:"+exchBOld+"exchBNew"+exchBNew);
						//替换
						oldPairArray.set(i1, tempNew);
						break;
					}else if((!exchAOld.equals(exchANew))&&(!exchBOld.equals(exchBNew))){//host不相同，则替换
						count++;
						if(count == oldPairArray.size()){
							logger.info("添加exchAOld:"+exchAOld+"exchANew"+exchANew+"exchBOld:"+exchBOld+"exchBNew"+exchBNew);
							//tickerType不一样就加到data来
							logger.info("添加前："+oldPairArray);
							oldPairArray.add(tempNew);
							logger.info("添加后："+oldPairArray);
						}
						
					}else{
						logger.info("GOD , SOMETHING MUST BE WRONG !");
					}
					
				}
			}
			//现在是新的oldPairArray
			oldPair.put("data", oldPairArray);
		}else {
			//没有该key就加进来
			coin2coinORcoin2cashDetail.put(key, value);
		}
		JSONObject result = new JSONObject(coin2coinORcoin2cashDetail);
		logger.info("result:"+result);
		logger.info("Exception22 222222222222222222222222222222222222222222222222222222222222222222");
		logger.info("Exception22 222222222222222222222222222222222222222222222222222222222222222222");
		logger.info("Exception22 222222222222222222222222222222222222222222222222222222222222222222");
		
		collector.ack(tuple);
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

}
