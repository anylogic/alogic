package com.logicbus.backend.bizlog.handler;

import com.anysoft.stream.HubHandler;
import com.logicbus.backend.bizlog.BizLogItem;
import com.logicbus.backend.bizlog.BizLogger;

/**
 * Hub分发
 * @author yyduan
 * @since 1.6.7.12
 */
public class Hub extends HubHandler<BizLogItem> implements BizLogger{
	public String getHandlerType(){
		return "logger";
	}
}
