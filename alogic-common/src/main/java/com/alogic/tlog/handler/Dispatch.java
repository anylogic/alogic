package com.alogic.tlog.handler;

import com.alogic.tlog.TLog;
import com.anysoft.stream.DispatchHandler;

/**
 * 分发处理
 * @author yyduan
 * @since 1.6.7.3
 */
public class Dispatch extends DispatchHandler<TLog> {
	public String getHandlerType(){
		return "logger";
	}
}
