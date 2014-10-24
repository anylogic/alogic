package com.logicbus.backend.timer.util.parser;

import java.util.Hashtable;

public class DayOfMonthItemParser extends DefaultItemParser {
	protected static int [] range = new int[]{31,1,31};
	protected static Hashtable<String,Integer> mappings;
	public int [] getRange(){
		return range;
	}
	public int parseSingleItem(String _item) {
		Integer found = mappings.get(_item.toUpperCase());
		if (found != null){
			return found.intValue();
		}
		return super.parseSingleItem(_item);
	}
	static{
		mappings = new Hashtable<String,Integer>();
		mappings.put("JAN", 1);
		mappings.put("FEB", 2);
		mappings.put("MAR", 3);
		mappings.put("APR", 4);
		mappings.put("MAY", 5);
		mappings.put("JUNE", 6);
		mappings.put("JULY", 7);
		mappings.put("AUG", 8);
		mappings.put("SEPT", 9);
		mappings.put("OCT", 10);
		mappings.put("NOV", 11);
		mappings.put("DEC", 12);
	}
}
