package com.alogic.uid.impl;

import com.alogic.uid.IdGenerator;
import com.anysoft.util.KeyGen;
import com.anysoft.util.Properties;

/**
 * 简单的id生成算法
 * @author yyduan
 * @since 1.6.11.5
 */
public class Simple extends IdGenerator.Abstract {

	@Override
	public String nextId() {
		return String.format("%d%s",System.currentTimeMillis(),KeyGen.uuid(5, 0, 9));
	}

	@Override
	public long nextLong() {
		try {
			return System.currentTimeMillis() * 10000L + Long.parseLong(KeyGen.uuid(5, 0, 9));
		}catch (NumberFormatException ex){
			return 0;
		}
	}

	@Override
	public void configure(Properties p) {
		
	}
}
