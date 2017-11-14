package com.zdx.tri;
import java.util.ArrayList;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


public class TriCalcu {
	/*
	 * 每一组三元组调整为规范形式
	 */
	public static ArrayList<JSONObject> formatAL(ArrayList<JSONObject> al){
		ArrayList<JSONObject> fAL = new ArrayList<JSONObject>();
		fAL.add(al.get(0));
		if(al.get(0).getString("coinB").equals(al.get(1).getString("coinA"))){
			fAL.add(al.get(1));
			if(al.get(1).getString("coinB").equals(al.get(2).getString("coinA"))){
				fAL.add(al.get(2));
			}else if(al.get(1).getString("coinB").equals(al.get(2).getString("coinB"))){
				String str = "{\"coinA\":\""+al.get(2).getString("coinB")+"\",\"coinB\":\""+al.get(2).getString("coinA")
						+"\",\"bid\":"+(1.0/al.get(2).getDouble("ask")+",\"ask\":"+(1.0/al.get(2).getDouble("bid"))+"}");
				JSONObject jo = JSON.parseObject(str);
				fAL.add(jo);
			}
		}else if(al.get(0).getString("coinB").equals(al.get(1).getString("coinB"))){
			String str1 = "{\"coinA\":\""+al.get(1).getString("coinB")+"\",\"coinB\":\""+al.get(1).getString("coinA")
					+"\",\"bid\":"+(1.0/al.get(1).getDouble("ask")+",\"ask\":"+(1.0/al.get(1).getDouble("bid"))+"}");
			JSONObject jo1 = JSON.parseObject(str1);
			fAL.add(jo1);
			if(al.get(1).getString("coinA").equals(al.get(2).getString("coinA"))){
				fAL.add(al.get(2));
			}else if(al.get(1).getString("coinA").equals(al.get(2).getString("coinB"))){
				String str2 = "{\"coinA\":\""+al.get(2).getString("coinB")+"\",\"coinB\":\""+al.get(2).getString("coinA")
						+"\",\"bid\":"+(1.0/al.get(2).getDouble("ask")+",\"ask\":"+(1.0/al.get(2).getDouble("bid"))+"}");
				JSONObject jo2 = JSON.parseObject(str2);
				fAL.add(jo2);
			}
		}else if(al.get(0).getString("coinB").equals(al.get(2).getString("coinA"))){
			fAL.add(al.get(2));
			if(al.get(2).getString("coinB").equals(al.get(1).getString("coinA"))){
				fAL.add(al.get(1));
			}else if(al.get(2).getString("coinB").equals(al.get(1).getString("coinB"))){
				String str1 = "{\"coinA\":\""+al.get(1).getString("coinB")+"\",\"coinB\":\""+al.get(1).getString("coinA")
						+"\",\"bid\":"+(1.0/al.get(1).getDouble("ask")+",\"ask\":"+(1.0/al.get(1).getDouble("bid"))+"}");
				JSONObject jo1 = JSON.parseObject(str1);
				fAL.add(jo1);
			}
		}else if(al.get(0).getString("coinB").equals(al.get(2).getString("coinB"))){
			String str1 = "{\"coinA\":\""+al.get(2).getString("coinB")+"\",\"coinB\":\""+al.get(2).getString("coinA")
					+"\",\"bid\":"+(1.0/al.get(2).getDouble("ask")+",\"ask\":"+(1.0/al.get(2).getDouble("bid"))+"}");
			JSONObject jo1 = JSON.parseObject(str1);
			fAL.add(jo1);
			if(al.get(2).getString("coinA").equals(al.get(1).getString("coinA"))){
				fAL.add(al.get(1));
			}else if(al.get(2).getString("coinA").equals(al.get(1).getString("coinB"))){
				String str2 = "{\"coinA\":\""+al.get(1).getString("coinB")+"\",\"coinB\":\""+al.get(1).getString("coinA")
						+"\",\"bid\":"+(1.0/al.get(1).getDouble("ask")+",\"ask\":"+(1.0/al.get(1).getDouble("bid"))+"}");
				JSONObject jo2 = JSON.parseObject(str2);
				fAL.add(jo2);
			}
		}else {
			System.out.println("God , there must be something else I haven't thought about .");
		}
		for(int i=0;i<3;i++){
			System.out.println(fAL.get(i));
		}
		return fAL;
	}
	/*
	 * 三元组反转/
	 */
	public static ArrayList<JSONObject> reverseAL(ArrayList<JSONObject> al){
		ArrayList<JSONObject> ral = new ArrayList<JSONObject>();
		for(int i=0;i<3;i++){
			String str = "{\"coinA\":\""+al.get(2-i).getString("coinB")+"\",\"coinB\":\""+al.get(2-i).getString("coinA")
					+"\",\"bid\":"+(1.0/al.get(2-i).getDouble("ask")+",\"ask\":"+(1.0/al.get(2-i).getDouble("bid"))+"}");
			JSONObject jo = JSON.parseObject(str);
			ral.add(jo);
		}
		for(int j=0;j<3;j++){
			System.out.println(ral.get(j));
		}
		
		return ral;
	}
	/*
	 * 三元组路径和利润计算/
	 */
	public static JSONObject calPath(ArrayList<JSONObject> al){
		String[] path = new String[3];
		path[0] = al.get(0).getString("coinA")+"-"+al.get(0).getString("coinB")+"-"+al.get(1).getString("coinB");
		path[1] = al.get(1).getString("coinA")+"-"+al.get(1).getString("coinB")+"-"+al.get(2).getString("coinB");
		path[2] = al.get(2).getString("coinA")+"-"+al.get(2).getString("coinB")+"-"+al.get(0).getString("coinB");
		double profit = (al.get(0).getDouble("bid"))*(al.get(1).getDouble("bid"))*(al.get(2).getDouble("bid"))-1;
		String result = "{\"path\":[\""+path[0]+"\",\""+path[1]+"\",\""+path[2]+"\"],\"profit\":"+profit+"}";
		JSONObject js = JSON.parseObject(result);
		return js;
	}
	
	public static void main(String[] args) {
		//原始数据
		ArrayList<JSONObject> al = new ArrayList<JSONObject>();
		al.add(JSON.parseObject("{\"coinA\":\"eth\",\"coinB\":\"btc\",\"bid\":0.04039721,\"ask\":0.04045000}"));
		al.add(JSON.parseObject("{\"coinA\":\"etc\",\"coinB\":\"btc\",\"bid\":0.00189300,\"ask\":0.00189496}"));
		al.add(JSON.parseObject("{\"coinA\":\"etc\",\"coinB\":\"eth\",\"bid\":0.04450900,\"ask\":0.04784850}"));
		System.out.println("-----------------原始数据---------------------");
		for(int i=0;i<3;i++){
			System.out.println(al.get(i));
		}
		//规范化
		System.out.println("-----------------规范数据---------------------");
		ArrayList<JSONObject> fal = TriCalcu.formatAL(al);
		System.out.println(TriCalcu.calPath(fal));
		System.out.println("-----------------反向数据---------------------");
		ArrayList<JSONObject> ral = TriCalcu.reverseAL(fal);
		System.out.println(TriCalcu.calPath(ral));
	}
}
