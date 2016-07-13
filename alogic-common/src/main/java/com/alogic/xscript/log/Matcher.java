package com.alogic.xscript.log;

import com.anysoft.stream.MatcherFilter;

/**
 * ID匹配过滤器
 * 
 * @author duanyy
 *
 */
public class Matcher extends MatcherFilter<LogInfo> {
	public String getHandlerType(){
		return "logger";
	}
}
