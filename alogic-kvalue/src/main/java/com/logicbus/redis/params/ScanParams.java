package com.logicbus.redis.params;

import com.logicbus.redis.client.Params;
import com.logicbus.redis.util.SafeEncoder;

/**
 * Key扫描时用的参数
 * 
 * @author duanyy
 *
 */
public class ScanParams extends Params {
	protected final byte [] KW_MATCH = SafeEncoder.encode("MATCH");
	protected final byte [] KW_COUNT = SafeEncoder.encode("COUNT");
	
	public ScanParams match(final byte[] pattern) {
		add(KW_MATCH);
		add(pattern);
		return this;
	}

	public ScanParams match(final String pattern) {
		add(KW_MATCH);
		add(SafeEncoder.encode(pattern));
		return this;
	}

	public ScanParams count(final int count) {
		add(KW_COUNT);
		add(SafeEncoder.encode(count));
		return this;
	}
}
