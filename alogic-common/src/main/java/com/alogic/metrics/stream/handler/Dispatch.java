package com.alogic.metrics.stream.handler;

import com.alogic.metrics.Fragment;
import com.anysoft.stream.DispatchHandler;


/**
 * 分发处理器
 * @author yyduan
 *
 * @since 1.6.6.13
 *
 */
public class Dispatch extends DispatchHandler<Fragment>{
	public String getHandlerType(){
		return "handler";
	}
}
