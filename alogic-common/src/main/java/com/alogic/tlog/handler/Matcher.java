package com.alogic.tlog.handler;

import com.alogic.tlog.TLog;
import com.anysoft.stream.MatcherFilter;

/**
 * 匹配过滤
 * @author yyduan
 * @since 1.6.7.3
 */
public class Matcher extends MatcherFilter<TLog> {
	public String getHandlerType(){
		return "logger";
	}
}
