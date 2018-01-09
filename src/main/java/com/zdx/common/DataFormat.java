package com.zdx.common;

public class DataFormat {
	public static String removeShortTerm(String x){
		//remove -
		if (!x.isEmpty() && x.contains(CommonConst.SHORT_TERM)){			
			x = x.replaceAll(CommonConst.SHORT_TERM, "");
		}
		return x;
	}
}
