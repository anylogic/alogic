package com.alogic.timer.matcher.util.parser;

import java.util.Hashtable;

import com.alogic.timer.matcher.util.DateItemParser.Default;

/**
 * Cron中的MonthOfYear
 * 
 * @author duanyy
 * @since 1.6.3.37
 */
public class MonthOfYear extends Default{
	protected static int [] range = new int[]{12,1,12};
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
