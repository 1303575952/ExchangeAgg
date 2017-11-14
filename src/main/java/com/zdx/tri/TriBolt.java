package com.zdx.tri;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zdx.common.TickerFormat;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;

public class TriBolt implements IRichBolt{

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
		System.out.println(tuple.getValue(0));
		System.out.println(tuple.getValue(1));
		String key = tuple.getValue(0).toString();
		JSONObject value = JSON.parseObject((String) tuple.getValue(1));
		System.out.println(value);
		if(coin2coinORcoin2cashDetail.containsKey(key)){
			//TODO 拿到该key对应value和新来的value对比更新
			JSONObject oldPair = (JSONObject) coin2coinORcoin2cashDetail.get(key);
			JSONArray oldPairArray = oldPair.getJSONArray("data");
			JSONArray newPairArray = value.getJSONArray("data");
			System.out.println("oldPairArray size:"+oldPairArray.size());
			System.out.println("newPairArray size:"+newPairArray.size());
			for(int i2 = 0;i2<newPairArray.size();i2++){
				int count = 0;//记录当前是和oldPairArray的第几个比较
				JSONObject tempNew = (JSONObject) newPairArray.get(i2);
				System.out.println("newPairArray"+"第"+i2+"个"+tempNew);
				String exchANew = tempNew.get("exchA").toString();
				String exchBNew = tempNew.get("exchB").toString();
				for(int i1=0;i1<oldPairArray.size();i1++){
					JSONObject tempOld = (JSONObject) oldPairArray.get(i1);
					System.out.println("oldPairArray"+"第"+i1+"个"+tempOld);
					String exchAOld = tempOld.get("exchA").toString();
					String exchBOld = tempOld.get("exchB").toString();
					if(exchAOld.equals(exchANew)&&exchBOld.equals(exchBNew)){//host相同，则替换
						System.out.println("替换exchAOld:"+exchAOld+"exchANew"+exchANew+"exchBOld:"+exchBOld+"exchBNew"+exchBNew);
						//替换
						oldPairArray.set(i1, tempNew);
						break;
					}else if((!exchAOld.equals(exchANew))&&(!exchBOld.equals(exchBNew))){//host不相同，则替换
						count++;
						if(count == oldPairArray.size()){
							System.out.println("添加exchAOld:"+exchAOld+"exchANew"+exchANew+"exchBOld:"+exchBOld+"exchBNew"+exchBNew);
							//tickerType不一样就加到data来
							System.out.println("添加前："+oldPairArray);
							oldPairArray.add(tempNew);
							System.out.println("添加后："+oldPairArray);
						}
						
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
