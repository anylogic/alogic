package com.alogic.xscript.plugins;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.alogic.tracer.Tool;
import com.alogic.tracer.TraceContext;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.doc.json.JsonObject;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.jayway.jsonpath.JsonPath;

/**
 * 循环
 * 
 * @author duanyy
 * @version 1.6.7.22 [20170306 duanyy] <br>
 * - 当jsonPath语法错误或者节点不存在时，不再抛出异常 <br>
 * 
 * @version 1.6.8.4 [20170329 duanyy] <br>
 * - 对象的属性可以循环处理 <br>
 * 
 * @version 1.6.9.1 [20170516 duanyy] <br>
 * - 修复部分插件由于使用新的文档模型产生的兼容性问题 <br>
 */
public class Repeat extends Segment{
	protected String jsonPath;
	protected String value = "$value";
	protected String key = "$key";
	protected String async = "false";
	protected long timeout = 1000L;	
	protected boolean shutdownGracefully = true;
	protected int threadPoolSize = 5;	
	
	public Repeat(String tag, Logiclet p) {
		super(tag, p);
	}
	
	public void configure(Properties p){
		super.configure(p);
		jsonPath = PropertiesConstants.getString(p, "path", jsonPath);
		value = PropertiesConstants.getString(p,"value",value,true);
		key = PropertiesConstants.getString(p,"key",key,true);
		async = PropertiesConstants.getRaw(p, "async", async);
		timeout = PropertiesConstants.getLong(p, "async.timeout", timeout);
		shutdownGracefully = PropertiesConstants.getBoolean(p, "async.shutdownGracefully", shutdownGracefully);
		threadPoolSize = PropertiesConstants.getInt(p, "async.threadPoolSize", threadPoolSize);				
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		boolean isAsyncMode = PropertiesConstants.transform(ctx, async, false);
		if (current instanceof JsonObject){
			if (StringUtils.isNotEmpty(jsonPath)){
				Object result = null;
				try {
					result = JsonPath.read(current.getContent(), jsonPath);
				}catch (Exception ex){
					
				}
				if (result != null){
					if (result instanceof List<?>){
						repeat(isAsyncMode,(List<Object>)result,root,current,ctx,watcher);
					}else{
						if (result instanceof Map<?,?>){
							repeat(isAsyncMode,(Map<String,Object>)result,root,current,ctx,watcher);
						}else{
							logger.error("Can not locate the path:" + jsonPath);
						}
					}
				}
			}
		}else{
			throw new BaseException("core.not_supported",
					String.format("Tag %s does not support protocol %s",this.getXmlTag(),root.getClass().getName()));	
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void repeat(boolean async,final List<Object> list,final XsObject root,final XsObject current, 
			final LogicletContext ctx, final ExecuteWatcher watcher){
		if (async){
			final ScheduledThreadPoolExecutor exec = new  ScheduledThreadPoolExecutor(threadPoolSize);
			final CountDownLatch latch = new CountDownLatch(list.size());

			boolean error = false;
			String msg = "OK";
			final TraceContext tc = traceEnable()?Tool.start():null;				
			try {
				for (final Object o:list){				
					exec.schedule(new Runnable(){
		
						@Override
						public void run() {
							TraceContext child = (traceEnable()&&tc != null)?tc.newChild():null;
							String msg  = "OK";
							boolean error = false;	
							try {
								if (o instanceof Map<?,?>){
									superExecute(root, new JsonObject("current",(Map<String,Object>)o), ctx, watcher);
								}else{
									LogicletContext childCtx = new LogicletContext(ctx);
									childCtx.SetValue(value, o.toString());
									superExecute(root, current, childCtx, watcher);
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
			for (Object o:list){
				if (o instanceof Map<?,?>){
					super.onExecute(root, new JsonObject("current",(Map<String,Object>)o), ctx, watcher);
				}else{
					ctx.SetValue(value, o.toString());
					super.onExecute(root, current, ctx, watcher);
				}
			}			
		}
	}
	
	protected void repeat(boolean async,final Map<String,Object> map,final XsObject root,final XsObject current, 
			final LogicletContext ctx, final ExecuteWatcher watcher){
		if (async){
			final ScheduledThreadPoolExecutor exec = new  ScheduledThreadPoolExecutor(threadPoolSize);
			final CountDownLatch latch = new CountDownLatch(map.size());

			boolean error = false;
			String msg = "OK";
			final TraceContext tc = traceEnable()?Tool.start():null;				
			try {
				final Iterator<Entry<String,Object>> iter = map.entrySet().iterator();
				while (iter.hasNext()){
					final Entry<String,Object> entry = iter.next();
					exec.schedule(new Runnable(){
		
						@Override
						public void run() {
							TraceContext child = (traceEnable()&&tc != null)?tc.newChild():null;
							String msg  = "OK";
							boolean error = false;	
							try {
								final Object val = entry.getValue();	
								if (val instanceof String || val instanceof Number){
									LogicletContext childCtx = new LogicletContext(ctx);
									childCtx.SetValue(key, entry.getKey());
									childCtx.SetValue(value, val.toString());
									superExecute(root, current, childCtx, watcher);
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
			Iterator<Entry<String,Object>> iter = map.entrySet().iterator();
			while (iter.hasNext()){
				Entry<String,Object> entry = iter.next();
				Object val = entry.getValue();
				if (val instanceof String || val instanceof Number){
					ctx.SetValue(key, entry.getKey());
					ctx.SetValue(value, val.toString());
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
