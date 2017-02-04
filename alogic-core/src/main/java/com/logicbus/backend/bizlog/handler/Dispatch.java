package com.logicbus.backend.bizlog.handler;

import com.anysoft.stream.DispatchHandler;
import com.logicbus.backend.bizlog.BizLogItem;
import com.logicbus.backend.bizlog.BizLogger;

/**
 * 分发处理
 * @author yyduan
 * @since 1.6.7.12
 */
public class Dispatch extends DispatchHandler<BizLogItem> implements BizLogger{
	public String getHandlerType(){
		return "logger";
	}
}
