package com.alogic.tlog.handler;

import com.alogic.tlog.TLog;
import com.anysoft.stream.HubHandler;

/**
 * Hub分发
 * @author yyduan
 * @since 1.6.7.3
 */
public class Hub extends HubHandler<TLog> {
	public String getHandlerType(){
		return "logger";
	}
}
