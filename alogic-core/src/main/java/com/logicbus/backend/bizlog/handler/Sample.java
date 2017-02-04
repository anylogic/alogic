package com.logicbus.backend.bizlog.handler;

import com.anysoft.stream.RateFilter;
import com.logicbus.backend.bizlog.BizLogItem;
import com.logicbus.backend.bizlog.BizLogger;

/**
 * 按比例抽样
 * 
 * @author yyduan
 * @since 1.6.7.12
 */
public class Sample extends RateFilter<BizLogItem>  implements BizLogger {
	public String getHandlerType(){
		return "logger";
	}
}
