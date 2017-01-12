package com.alogic.metrics.stream;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alogic.metrics.Fragment;
import com.anysoft.stream.AbstractHandler;

/**
 * 累加之后写出
 * @author yyduan
 *
 * @since 1.6.6.13
 *
 */
public abstract class MetricsSummaryWriter extends AbstractHandler<Fragment>{
	protected Map<String,Fragment> metrics = new ConcurrentHashMap<String,Fragment>();
	
	protected void incr(Fragment f){
		String id = f.getStatsDimesion();
		Fragment found = metrics.get(id);
		
		if (found == null){
			synchronized(metrics){
				found = metrics.get(id);
				if (found == null){
					metrics.put(id, f);
				}else{
					found.incr(f);
				}
			}
		}else{
			found.incr(f);
		}
	}
	
	@Override
	protected void onHandle(Fragment _data,long t) {
		incr(_data);
	}
	
	@Override
	protected void onFlush(long t) {
		write(metrics,t);
		metrics.clear();
	}			
	
	protected abstract void write(Map<String,Fragment> data,long t);
}