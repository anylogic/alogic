package com.logicbus.backend.timer.util.parser;

public class MonthOfYearItemParser extends DefaultItemParser {
	protected static int [] range = new int[]{12,1,12};
	public int [] getRange(){
		return range;
	}
}
