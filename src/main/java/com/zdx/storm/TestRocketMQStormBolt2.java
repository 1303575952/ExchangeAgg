package com.zdx.storm;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zdx.common.TickerFormat;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;

public class TestRocketMQStormBolt2 implements IRichBolt{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected OutputCollector collector;
	Map<String, Object> coin2coinORcoin2cashDetail = new HashMap<String,Object>();
	
	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector  = collector;
		
	}

	@Override
	public void execute(Tuple tuple) {
		System.out.println("Exception22 111111111111111111111111111111111111111111111111111111111111111111");
		System.out.println("Exception22 111111111111111111111111111111111111111111111111111111111111111111");
		System.out.println("Exception22 111111111111111111111111111111111111111111111111111111111111111111");
		System.out.println("***********************************************");
		System.out.println("bolt2 key:"+tuple.getValue(0));
		System.out.println("bolt2 value:"+tuple.getValue(1));
		System.out.println("***********************************************");
		String key = tuple.getValue(0).toString();
		JSONObject value = (JSONObject) tuple.getValue(0);
		
		if(coin2coinORcoin2cashDetail.containsKey(key)){
			//TODO 拿到该key对应value和新来的value对比更新
			JSONObject oldPair = (JSONObject) coin2coinORcoin2cashDetail.get(key);
			JSONArray oldPairArray = oldPair.getJSONArray("data");
			JSONArray newPairArray = value.getJSONArray("data");
			for(int i1 = 0;i1<oldPairArray.size();i1++){
				JsonObject tempOld = (JsonObject) oldPairArray.get(i1);
				String coinAOld = tempOld.get("coinA").toString();
				String coinBOld = tempOld.get("coinB").toString();
				for(int i2=0;i2<newPairArray.size();i2++){
					JsonObject tempNew = (JsonObject) newPairArray.get(i2);
					String coinANew = tempNew.get("coinA").toString();
					String coinBNew = tempNew.get("coinB").toString();
					if(coinAOld.equals(coinANew)&&coinBOld.equals(coinBNew)){
						//替换
						oldPairArray.set(i1, tempNew);
					}else if((!coinAOld.equals(coinANew))&&(!coinBOld.equals(coinBNew))){
						//加到data来
						oldPairArray.add(tempNew);
					}else{
						System.out.println("GOD , SOMETHING MUST BE WRONG !");
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
		System.out.println("result:"+result);
		System.out.println("Exception22 222222222222222222222222222222222222222222222222222222222222222222");
		System.out.println("Exception22 222222222222222222222222222222222222222222222222222222222222222222");
		System.out.println("Exception22 222222222222222222222222222222222222222222222222222222222222222222");
		
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
