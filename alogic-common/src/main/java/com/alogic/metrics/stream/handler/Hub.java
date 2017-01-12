package com.alogic.metrics.stream.handler;

import com.alogic.metrics.Fragment;
import com.anysoft.stream.HubHandler;

/**
 * Hub处理器
 * 
 * @author yyduan
 *
 * @since 1.6.6.13
 *
 */
public class Hub extends HubHandler<Fragment>{
	public String getHandlerType(){
		return "handler";
	}
}
