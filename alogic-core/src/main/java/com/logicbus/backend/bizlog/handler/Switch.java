package com.logicbus.backend.bizlog.handler;

import com.anysoft.stream.SwitchHandler;
import com.logicbus.backend.bizlog.BizLogItem;
import com.logicbus.backend.bizlog.BizLogger;

/**
 * 条件
 * @author yyduan
 * @since 1.6.7.12
 */
public class Switch extends SwitchHandler<BizLogItem> implements BizLogger {
	public String getHandlerType(){
		return "logger";
	}
}
