package com.alogic.metrics.stream.handler;

import org.w3c.dom.Element;

import com.alogic.metrics.Fragment;
import com.anysoft.stream.AbstractHandler;
import com.anysoft.util.Properties;

/**
 * 缺省实现
 * @author yyduan
 *
 * @since 1.6.6.13
 *
 */
public class Default extends AbstractHandler<Fragment>{

	
	protected void onHandle(Fragment _data,long t) {
	}

	
	protected void onFlush(long t) {
	}

	
	protected void onConfigure(Element e, Properties p) {
	}

}