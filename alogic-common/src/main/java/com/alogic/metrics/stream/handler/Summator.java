package com.alogic.metrics.stream.handler;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;

import com.alogic.metrics.Fragment;
import com.anysoft.stream.Handler;
import com.anysoft.stream.SlideHandler;

/**
 * 累加器
 * 
 * @author yyduan
 *
 * @since 1.6.6.13
 *
 */
public class Summator extends SlideHandler<Fragment>{
	protected Hashtable<String,Fragment> metrics = new Hashtable<String,Fragment>();
	
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

	
	protected void onHandle(Fragment _data,long t) {
		incr(_data);
	}

	
	protected void onFlush(long t) {
		Handler<Fragment> handler = getSlidingHandler();
		Iterator<Entry<String,Fragment>> iterator = metrics.entrySet().iterator();
		
		while (iterator.hasNext()){
			Entry<String,Fragment> entry = iterator.next();
			if (handler != null){
				handler.handle(entry.getValue(),t);
			}
		}
		
		if (handler != null){
			handler.flush(t);
		}
		
		metrics.clear();
	}

}