package com.logicbus.backend.timer.util.parser;

public class HourOfDayItemParser extends DefaultItemParser{
	protected static int [] range = new int[]{24,0,23};
	public int [] getRange(){
		return range;
	}
}
