package com.alogic.timer.matcher.util.parser;

import java.util.Hashtable;

import com.alogic.timer.matcher.util.DateItemParser.Default;

/**
 * Cron中的DayOfMonth
 * 
 * @author duanyy
 * @since 1.6.3.37
 */
public class DayOfMonth extends Default{
	protected static int [] range = new int[]{31,1,31};
	protected static Hashtable<String,Integer> mappings;
	public int [] getRange(){
		return range;
	}
}
