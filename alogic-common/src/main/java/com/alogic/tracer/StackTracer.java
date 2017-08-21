package com.alogic.tracer;

import java.util.concurrent.ConcurrentHashMap;

import com.alogic.tlog.TLog;
import com.anysoft.util.Properties;

/**
 * 调用栈追踪
 * 
 * @author duanyy
 * @since 1.6.5.3
 * 
 * @version 1.6.5.11 [20160603 duanyy] <br>
 * - tracelog增加type字段 <br>
 * 
 * @version 1.6.7.1 [20170117 duanyy] <br>
 * - trace日志调用链中的调用次序采用xx.xx.xx.xx字符串模式 <br>
 * 
 * @version 1.6.7.3 [20170118 duanyy] <br>
 * - trace日志的时长单位改为ns <br>
 * - 新增com.alogic.tlog，替代com.alogic.tracer.log包; <br>
 * 
 * @version 1.6.7.21 [20170303 duanyy] <br>
 * - TLog增加parameter字段，便于调用者记录个性化参数 <br>
 * 
 * @version 1.6.9.8 [20170821] <br>
 * - tlog增加keyword字段 <br>
 */
public class StackTracer extends Tracer.Abstract{
	/**
	 * 以线程为单位集合的上下文集合
	 */
	protected ConcurrentHashMap<Long,TraceContext> contexts = new ConcurrentHashMap<Long,TraceContext>();

	@Override
	public TraceContext startProcedure() {
		long thread = Thread.currentThread().getId();
		
		TraceContext current = contexts.get(thread);
		if (current == null){
			//创建一个新的
			current = new TraceContext.Default();
		}else{
			current = current.newChild();
		}
		
		contexts.put(thread, current);
		return current;
	}
	
	@Override
	public TraceContext startProcedure(String sn, String order) {
		long thread = Thread.currentThread().getId();
		
		TraceContext current = contexts.get(thread);
		if (current == null){
			//创建一个新的
			current = new TraceContext.Default(null,sn,order);
		}else{
			current = current.newChild();
		}
		
		contexts.put(thread, current);
		return current;
	}	

	@Override
	public void configure(Properties p) {
		super.configure(p);
	}

	@Override
	public void endProcedure(TraceContext ctx, String type, String name,
			String result, String note, String parameter, String keyword,
			long contentLength) {
		long thread = Thread.currentThread().getId();
		
		TraceContext current = contexts.get(thread);
		if (current != null){
			TraceContext parent = current.parent();
			if (parent == null){
				contexts.remove(thread);
			}else{
				contexts.put(thread, parent);		
			}
			
			TLog traceLog= new TLog();
			traceLog.sn(current.sn());
			traceLog.order(current.order());
			traceLog.method(name);
			traceLog.reason(note);
			traceLog.code(result);
			traceLog.type(type);
			traceLog.parameter(parameter);
			traceLog.keyword(keyword);
			traceLog.startDate(current.timestamp());
			traceLog.duration(System.nanoTime()-current.startTime());			
			traceLog.contentLength(contentLength);
			
			log(traceLog);			
		}else{
			//如果为空，恐怕出了问题			
			LOG.error("It is impossible,something is wrong.");
		}
	}


}
