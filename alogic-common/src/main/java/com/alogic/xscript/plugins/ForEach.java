package com.alogic.xscript.plugins;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.alogic.tracer.Tool;
import com.alogic.tracer.TraceContext;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 针对字符串数组进行循环
 * 
 * @author duanyy
 * @version 1.6.8.14 [20170509 duanyy] <br>
 * - 增加xscript的中间文档模型,以便支持多种报文协议 <br>
 * 
 * @version 1.6.10.2 [20170925 duanyy] <br>
 * - 增加异步执行功能 <br>
 */
public class ForEach extends Segment{
	protected String in;
	protected String id = "$value";
	protected String delimeter=";";
	protected String async = "false";
	protected long timeout = 1000L;	
	protected boolean shutdownGracefully = true;
	protected int threadPoolSize = 5;	
	
	public ForEach(String tag, Logiclet p) {
		super(tag, p);
	}
	
	public void configure(Properties p){
		super.configure(p);
		in = PropertiesConstants.getRaw(p,"in","");
		delimeter = PropertiesConstants.getString(p,"delimeter",delimeter,true);
		id = PropertiesConstants.getString(p,"id",id,true);
		async = PropertiesConstants.getRaw(p, "async", async);
		timeout = PropertiesConstants.getLong(p, "async.timeout", timeout);
		shutdownGracefully = PropertiesConstants.getBoolean(p, "async.shutdownGracefully", shutdownGracefully);
		threadPoolSize = PropertiesConstants.getInt(p, "async.threadPoolSize", threadPoolSize);		
	}

	@Override
	protected void onExecute(final XsObject root,final XsObject current, final LogicletContext ctx, final ExecuteWatcher watcher) {
		String[] values = ctx.transform(in).split(delimeter);
		
		if (values.length > 0){
			boolean asyncMode = PropertiesConstants.transform(ctx, async, false);
			if (asyncMode){
				final ScheduledThreadPoolExecutor exec = new  ScheduledThreadPoolExecutor(threadPoolSize);
				final CountDownLatch latch = new CountDownLatch(values.length);
	
				boolean error = false;
				String msg = "OK";
				final TraceContext tc = traceEnable()?Tool.start():null;				
				try {
					for (final String value:values){						
						exec.schedule(new Runnable(){
			
							@Override
							public void run() {
								TraceContext child = (traceEnable()&&tc != null)?tc.newChild():null;
								String msg  = "OK";
								boolean error = false;								
								try {
									LogicletContext childCtx = new LogicletContext(ctx);
									childCtx.SetValue(id, value);									
									superExecute(root,current,childCtx,watcher);
								}catch (Exception ex){
									msg = ExceptionUtils.getStackTrace(ex);
									log(msg,"error");
									error = true;
								}finally{
									if (latch != null){
										latch.countDown();
									}
									if (traceEnable()&&tc != null){
										Tool.end(child, "ASYNC-CHILD", getXmlTag(),error?"FAILED":"OK", msg);
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
			}else{
				for (final String value:values){
					ctx.SetValue(id, value);
					super.onExecute(root, current, ctx, watcher);
				}
			}
		}
	}

	protected void superExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher){
		List<Logiclet> list = children;
		for (int i = 0 ; i < list.size(); i ++){
			Logiclet logiclet = list.get(i);
			if (logiclet != null){
				logiclet.execute(root,current,ctx,watcher);
			}
		}		
	}
}
