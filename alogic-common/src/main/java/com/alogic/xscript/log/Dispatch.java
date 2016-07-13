package com.alogic.xscript.log;

import com.anysoft.stream.DispatchHandler;

/**
 * 分发器
 * 
 * @author duanyy
 *
 */
public class Dispatch extends DispatchHandler<LogInfo> {
	public String getHandlerType(){
		return "logger";
	}
}
