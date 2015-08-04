package com.alogic.timer.matcher.util.parser;

import com.alogic.timer.matcher.util.DateItemParser.Default;

/**
 * Cron中的分
 * 
 * @author duanyy
 * @since 1.6.3.37
 */
public class Minute extends Default {
	protected static int [] range = new int[]{60,0,59};
	public int [] getRange(){
		return range;
	}
}
