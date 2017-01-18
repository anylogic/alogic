package com.alogic.tlog.handler;

import com.alogic.tlog.TLog;
import com.anysoft.stream.SwitchHandler;

/**
 * 条件
 * @author yyduan
 * @since 1.6.7.3
 */
public class Switch extends SwitchHandler<TLog> {
	public String getHandlerType(){
		return "logger";
	}
}
