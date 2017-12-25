package com.zdx.common;

public class DataFormat {
	public static String removeShortTerm(String x){
		//remove -
		if (!x.isEmpty() && x.contains("-")){			
			x = x.replaceAll("-", "");
		}
		return x;
	}
}
