package com.zdx.tri;

import org.apache.log4j.Logger;

import com.zdx.demo.ToyConsumer;

public class TriArbitrageInfo {
	private static Logger logger = Logger.getLogger(TriArbitrageInfo.class);
	public String exchangeName = "";
	public double bid1 = 0.0;
	public double bid2 = 0.0;
	public double bid3 = 0.0;
	public double ask1 = 0.0;
	public double ask2 = 0.0;
	public double ask3 = 0.0;
	public String groupId = "";
	String path1 = "";
	String path2 = "";
	String path3 = "";
	public String fullPath = "";
	public double profitVal = 0.0;
	double lowerLimit = 0.00;


	public void updateProfitByGroupId(){
		if(bid1*bid2*bid3*ask1*ask2*ask3 > 0){
			if ("abbcca".equals(groupId)){
				if(((bid1 * bid2 * bid3) > (1.0 / (ask1 * ask2 * ask3)))&&((bid1 * bid2 * bid3)  > lowerLimit)){
					profitVal = bid1 * bid2 * bid3;
					path1 = "abc";
					path2 = "bca";
					path3 = "cab";
				}else if(((bid1 * bid2 * bid3) < (1.0 / (ask1 * ask2 * ask3)))&&((1.0 / (ask1 * ask2 * ask3)) > lowerLimit)){
					profitVal = 1.0 / (ask1 * ask2 * ask3);
					path1 = "acb";
					path2 = "cba";
					path3 = "bac";
				}

			} else if ("abcbca".equals(groupId)){
				if(((bid1 * bid3 / ask2) > (bid2 / (ask1 * ask3)))&&((bid1 * bid3 / ask2) > lowerLimit)){
					profitVal = bid1 * bid3 / ask2;
					path1 = "abc";
					path2 = "bca";
					path3 = "cab";
				}else if(((bid1 * bid3 / ask2) < (bid2 / (ask1 * ask3)))&&(bid2 / (ask1 * ask3) > lowerLimit)){
					profitVal = bid2 / (ask1 * ask3);
					path1 = "acb";
					path2 = "cba";
					path3 = "bac";
				}
			}else if ("abbcac".equals(groupId)) {
				if(((bid1 * bid2 / ask3) > (bid3 / (ask1 * ask2)))&&((bid1 * bid2 / ask3) > lowerLimit)){
					profitVal = bid1 * bid2 / ask3;
					path1 = "abc";
					path2 = "bca";
					path3 = "cab";
				}else if(((bid1 * bid2 / ask3) < (bid3 / (ask1 * ask2)))&&((bid3 / (ask1 * ask2)) > lowerLimit)){
					profitVal = bid3 / (ask1 * ask2);
					path1 = "acb";
					path2 = "cba";
					path3 = "bac";
				}
			}else if("abcbac".equals(groupId)){
				if(((bid1 / (ask2 * ask3)) > (bid2 * bid3 / ask1))&&(bid1 / (ask2 * ask3) > lowerLimit)){
					profitVal = bid1 / (ask2 * ask3);
					path1 = "abc";
					path2 = "bca";
					path3 = "cab";
				}else if(((bid1 / (ask2 * ask3)) < (bid2 * bid3 / ask1))&&((bid2 * bid3 / ask1) > lowerLimit)){
					profitVal = bid2 * bid3 / ask1;
					path1 = "acb";
					path2 = "cba";
					path3 = "bac";
				}
			}else if("abcabc".equals(groupId)){
				if(((bid1 * bid2 * bid3) > (1.0 / (ask1 * ask2 * ask3)))&&((bid1 * bid2 * bid3) > lowerLimit)){
					profitVal = bid1 * bid2 * bid3;
					path1 = "abc";
					path2 = "bca";
					path3 = "cab";
				}else if(((bid1 * bid2 * bid3) < (1.0 / (ask1 * ask2 * ask3)))&&((1.0 / (ask1 * ask2 * ask3)) >lowerLimit)){
					profitVal = 1.0 / (ask1 * ask2 * ask3);
					path1 = "acb";
					path2 = "cba";
					path3 = "bac";
				}
			}else if("abacbc".equals(groupId)){
				if(((bid1 * bid3 / ask2) > (bid2 / (ask1 * ask3)))&&((bid1 * bid3 / ask2) > lowerLimit)){
					profitVal = bid1 * bid3 / ask2;
					path1 = "abc";
					path2 = "bca";
					path3 = "cab";
				}else if(((bid1 * bid3 / ask2) < (bid2 / (ask1 * ask3)))&&(((bid2 / (ask1 * ask3)) > lowerLimit))){
					profitVal = bid2 / (ask1 * ask3);
					path1 = "acb";
					path2 = "cba";
					path3 = "bac";
				}
			}else{
				logger.error("ERROR ERROR ERROR");
			}
		}else {
			profitVal = 0;
		}

	}
	
	@Override
	public String toString(){
		return "{\"groupId\":\"" + groupId + 
				"\",\"exchangeName\":\"" + exchangeName +
				"\",\"fullPath\":\"" + fullPath +
				"\",\"bid1\":\"" + bid1 +
				"\",\"bid2\":\"" + bid2 +
				"\",\"bid3\":\"" + bid3 +
				"\",\"ask1\":\"" + ask1 +
				"\",\"ask2\":\"" + ask2 +
				"\",\"ask3\":\"" + ask3 +
				"\",\"path1\":\"" + path1 +
				"\",\"path2\":\"" + path2 +
				"\",\"path3\":\"" + path3 +
				"\",\"profitVal\":\"" + profitVal +
				"\"}";
	}
}
