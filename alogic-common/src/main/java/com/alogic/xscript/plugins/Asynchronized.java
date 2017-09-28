package com.alogic.xscript.plugins;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.alogic.tracer.Tool;
import com.alogic.tracer.TraceContext;
import com.alogic.xscript.Block;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 异步执行块
 * 
 * @author duanyy
 * @version 1.6.8.14 [20170509 duanyy] <br>
 * - 增加xscript的中间文档模型,以便支持多种报文协议 <br>
 * 
 * @version 1.6.10.2 [20170925 duanyy] <br>
 * - 采用私有线程池，可指定线程池大小，并且可关闭线程池 <br>
 * 
 */
public class Asynchronized extends Block {
	protected long timeout = 1000L;	
	protected int threadPoolSize = 5;
	protected boolean shutdownGracefully = true;
	
	public Asynchronized(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p) {
		super.configure(p);
		timeout = PropertiesConstants.getLong(p, "async.timeout", timeout);
		threadPoolSize = PropertiesConstants.getInt(p, "async.threadPoolSize", threadPoolSize);
		shutdownGracefully = PropertiesConstants.getBoolean(p, "async.shutdownGracefully", shutdownGracefully);
	}

	@Override
	protected void onExecute(final XsObject root,final XsObject current, final LogicletContext ctx, final ExecuteWatcher watcher) {
		final List<Logiclet> list = children;
		final ScheduledThreadPoolExecutor exec = new  ScheduledThreadPoolExecutor(threadPoolSize);
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
						String msg  = "OK";
						boolean error = false;
						try {
							if (logiclet != null){
								logiclet.execute(root,current,ctx,watcher);
							}
						}catch (Exception ex){
							msg = ExceptionUtils.getStackTrace(ex);
							log(msg,"error");
							error = true;
						}finally{
							if (latch != null){
								latch.countDown();
							}
							if (traceEnable()&&tc != null){
								Tool.end(child, "ASYNC-CHILD",getXmlTag(), error?"FAILED":"OK", msg);
							}
						}
					}
					
				}, 0, TimeUnit.MICROSECONDS);
			}
			
			if (!latch.await(timeout, TimeUnit.MILLISECONDS)){
				logger.warn("The async executing is timtout.");
			}
			
		}catch (Exception ex){
			msg = ExceptionUtils.getStackTrace(ex);
			log(msg,"error");
			error = true;
		}finally{
			if (shutdownGracefully){
				exec.shutdown();
			}else{
				exec.shutdownNow();
			}
			if (traceEnable()){
				Tool.end(tc, "ASYNC", getXmlTag(), error?"FAILED":"OK", msg);
			}				
		}
	}

}
