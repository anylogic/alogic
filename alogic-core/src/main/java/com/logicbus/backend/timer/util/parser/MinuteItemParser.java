package com.logicbus.backend.timer.util.parser;


public class MinuteItemParser extends DefaultItemParser{
	protected static int [] range = new int[]{60,0,59};
	public int [] getRange(){
		return range;
	}
}
