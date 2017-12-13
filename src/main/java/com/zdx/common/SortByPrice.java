package com.zdx.common;

import java.util.Comparator;

import org.apache.log4j.Logger;

public class SortByPrice implements Comparator<Object> {
	private static Logger logger = Logger.getLogger(SortByPrice.class);
	@Override
	public int compare(Object o1, Object o2) {
		TickerStandardFormat s1 = (TickerStandardFormat) o1;
		TickerStandardFormat s2 = (TickerStandardFormat) o2;
		return s1.mid.compareTo(s2.mid);
	}
}
