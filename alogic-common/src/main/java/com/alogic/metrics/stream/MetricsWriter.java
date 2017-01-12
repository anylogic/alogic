package com.alogic.metrics.stream;

import com.alogic.metrics.Fragment;
import com.anysoft.stream.AbstractHandler;

/**
 * 输出者
 * @author yyduan
 *
 * @since 1.6.6.13
 *
 */
public abstract class MetricsWriter extends AbstractHandler<Fragment>{

	@Override
	protected void onHandle(Fragment _data, long timestamp) {
		write(_data,timestamp);
	}

	@Override
	protected void onFlush(long timestamp) {

	}

	abstract protected void write(Fragment data,long t);
}
