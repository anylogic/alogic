package com.alogic.pool.impl;

import com.anysoft.util.Properties;

/**
 * 缺省的实现
 * 
 * @author duanyy
 *
 */
public class Default extends Singleton {

	@Override
	public void configure(Properties p) {
		// nothing to do
	}

	@Override
	protected <pooled> pooled createObject(int priority, int timeout) {
		return null;
	}

}
