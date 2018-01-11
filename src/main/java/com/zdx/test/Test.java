package com.zdx.test;
import org.apache.log4j.Logger;

public class Test {
	private static Logger logger = Logger.getLogger(Test.class);
	public static void main(String[] args) throws InterruptedException {
		long t1 = 1515508088875L;
		long t2 = 1515508089875L;
		
		System.out.println(t1 - t2);
		String tmp = "1515570892.7317615";		
		tmp = tmp.substring(0, tmp.indexOf(".")) + tmp.substring(tmp.indexOf(".")+1, tmp.indexOf(".")+4);
		System.out.println(tmp);
		long t3 = new Long(tmp);		
		System.out.println(t3);
	}
	


}

