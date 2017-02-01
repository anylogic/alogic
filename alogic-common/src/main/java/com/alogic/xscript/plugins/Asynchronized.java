package com.alogic.xscript.plugins;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.alogic.tracer.Tool;
import com.alogic.tracer.TraceContext;
import com.alogic.xscript.Block;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 异步执行块
 * 
 * @author duanyy
 *
 */
public class Asynchronized extends Block {
	protected long timeout = 1000L;	
	protected static ScheduledThreadPoolExecutor exec = new  ScheduledThreadPoolExecutor(5);
	
	public Asynchronized(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p) {
		super.configure(p);
		timeout = PropertiesConstants.getLong(p, "timeout", timeout);
	}

	@Override
	protected void onExecute(final Map<String, Object> root,
			final Map<String, Object> current, final LogicletContext ctx, final ExecuteWatcher watcher) {
		List<Logiclet> list = children;
		
		final CountDownLatch latch = new CountDownLatch(list.size());
		
		boolean error = false;
		String msg = "OK";
		
		final TraceContext tc = traceEnable()?Tool.start():null;
		
		try {
			for (int i = 0 ; i < list.size(); i ++){
				final Logiclet logiclet = list.get(i);
				exec.schedule(new Runnable(){
	
					@Override
					public void run() {
						TraceContext child = (traceEnable()&&tc != null)?tc.newChild():null;
						try {
							if (logiclet != null){
								logiclet.execute(root,current,ctx,watcher);
							}
						}finally{
							if (latch != null){
								latch.countDown();
							}
							if (traceEnable()&&tc != null){
								Tool.end(child, "ASYNC-CHILD", getXmlTag(),"OK", "OK");
							}
						}
					}
					
				}, 0, TimeUnit.MICROSECONDS);
			}
			
			if (!latch.await(timeout, TimeUnit.MILLISECONDS)){
				logger.warn("The async executing is timtout.");
			}
		}catch (Exception ex){
			logger.error(ex.getMessage());
			error = true;
			msg = ex.getMessage();
		}finally{
			if (traceEnable()){
				Tool.end(tc, "ASYNC", getXmlTag(), error?"FAILED":"OK", msg);
			}				
		}
	}

}
