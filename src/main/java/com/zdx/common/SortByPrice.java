package com.zdx.common;

import java.util.Comparator;

public class SortByPrice implements Comparator<Object> {
	public int compare(Object o1, Object o2) {
		TickerStandardFormat s1 = (TickerStandardFormat) o1;
		TickerStandardFormat s2 = (TickerStandardFormat) o2;
		return s1.mid.compareTo(s2.mid);
	}
}
