package com.alogic.timer.matcher.util.parser;

import com.alogic.timer.matcher.util.DateItemParser.Default;

/**
 * Cron中的时
 * 
 * @author duanyy
 * @since 1.6.3.37
 */
public class HourOfDay extends Default {
	protected static int [] range = new int[]{24,0,23};
	public int [] getRange(){
		return range;
	}
}
