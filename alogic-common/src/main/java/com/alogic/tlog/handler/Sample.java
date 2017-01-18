package com.alogic.tlog.handler;

import com.alogic.tlog.TLog;
import com.anysoft.stream.RateFilter;

/**
 * 按比例抽样
 * 
 * @author yyduan
 * @since 1.6.7.3
 */
public class Sample extends RateFilter<TLog> {
	public String getHandlerType(){
		return "logger";
	}
}
