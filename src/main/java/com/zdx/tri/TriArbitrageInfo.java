package com.zdx.tri;



public class TriArbitrageInfo {
	double bid1 = 0.0;
	double bid2 = 0.0;
	double bid3 = 0.0;
	double ask1 = 0.0;
	double ask2 = 0.0;
	double ask3 = 0.0;
	String groupId = "";
	String path1 = "";
	String path2 = "";
	String path3 = "";
	double profitVal = 0.0;


	public void updateProfitByGroupId(){
		if (groupId.equals("abbcca")){
			if(((bid1 * bid2 * bid3) > (1.0 / (ask1 * ask2 * ask3)))&&((bid1 * bid2 * bid3)  > 1.00)){
				profitVal = bid1 * bid2 * bid3;
				path1 = "abc";
				path2 = "bca";
				path3 = "cab";
			}else if(((bid1 * bid2 * bid3) < (1.0 / (ask1 * ask2 * ask3)))&&((1.0 / (ask1 * ask2 * ask3)) > 1.00)){
				profitVal = 1.0 / (ask1 * ask2 * ask3);
				path1 = "acb";
				path2 = "cba";
				path3 = "bac";
			}

		} else if (groupId.equals("abcbca")){
			if(((bid1 * bid3 / ask2) > (1.0 / (bid2 / (ask1 * ask3))))&&(bid1 * bid3 / ask2 > 1.00)){
				profitVal = bid1 * bid3 / ask2;
				path1 = "abc";
				path2 = "bca";
				path3 = "cab";
			}else if((bid1 * bid3 / ask2 < (bid2 / (ask1 * ask3)))&&(bid2 / (ask1 * ask3) > 1.00)){
				profitVal = bid2 / (ask1 * ask3);
				path1 = "acb";
				path2 = "cba";
				path3 = "bac";
			}
		}else if (groupId.equals("abbcac")) {
			if(((bid1 * bid2 / ask3) > (bid3 / (ask1 * ask2)))&&((bid1 * bid2 / ask3) > 1.00)){
				profitVal = bid1 * bid2 / ask3;
				path1 = "abc";
				path2 = "bca";
				path3 = "cab";
			}else if(((bid1 * bid2 / ask3) < (bid3 / (ask1 * ask2)))&&((bid3 / (ask1 * ask2)) > 1.00)){
				profitVal = bid3 / (ask1 * ask2);
				path1 = "acb";
				path2 = "cba";
				path3 = "bac";
			}
		}else if(groupId.equals("abcbac")){
			if(((bid1 / (ask2 * ask3)) > (bid2 * bid3 / ask1))&&(bid1 / (ask2 * ask3) > 1.00)){
				profitVal = bid1 / (ask2 * ask3);
				path1 = "abc";
				path2 = "bca";
				path3 = "cab";
			}else if(((bid1 / (ask2 * ask3)) < (bid2 * bid3 / ask1))&&((bid2 * bid3 / ask1) > 1.00)){
				profitVal = bid2 * bid3 / ask1;
				path1 = "acb";
				path2 = "cba";
				path3 = "bac";
			}
		}else if(groupId.equals("abcabc")){
			if(((bid1 * bid2 * bid3) > (1.0 / (ask1 * ask2 * ask3)))&&((bid1 * bid2 * bid3) > 1.00)){
				profitVal = bid1 * bid2 * bid3;
				path1 = "abc";
				path2 = "bca";
				path3 = "cab";
			}else if(((bid1 * bid2 * bid3) < (1.0 / (ask1 * ask2 * ask3)))&&((1.0 / (ask1 * ask2 * ask3)) >1.00)){
				profitVal = 1.0 / (ask1 * ask2 * ask3);
				path1 = "acb";
				path2 = "cba";
				path3 = "bac";
			}
		}else if(groupId.equals("abacbc")){
			if(((bid1 * bid3 / ask2) > (bid2 / (ask1 * ask3)))&&((bid1 * bid3 / ask2) > 1.00)){
				profitVal = bid1 * bid3 / ask2;
				path1 = "abc";
				path2 = "bca";
				path3 = "cab";
			}else if(((bid1 * bid3 / ask2) < (bid2 / (ask1 * ask3)))&&(((bid2 / (ask1 * ask3)) > 1.00))){
				profitVal = bid2 / (ask1 * ask3);
				path1 = "acb";
				path2 = "cba";
				path3 = "bac";
			}
		}else{
			System.out.println("ERROR ERROR ERROR");
		}
	}

	public String toString(){
		return "{\"groupId\":\"" + groupId + 
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
