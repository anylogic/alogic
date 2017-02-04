package com.logicbus.backend.bizlog.handler;

import com.anysoft.stream.MatcherFilter;
import com.logicbus.backend.bizlog.BizLogItem;
import com.logicbus.backend.bizlog.BizLogger;

/**
 * 匹配过滤
 * @author yyduan
 * @since 1.6.7.12
 */
public class Matcher extends MatcherFilter<BizLogItem>  implements BizLogger {
	public String getHandlerType(){
		return "logger";
	}
}
