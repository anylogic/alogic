package com.alogic.xscript.log;

import com.anysoft.stream.HubHandler;

/**
 * 集线器
 * 
 * @author duanyy
 *
 */
public class Hub extends HubHandler<LogInfo>{
	public String getHandlerType(){
		return "logger";
	}
}
