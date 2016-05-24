package com.alogic.tracer.log;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import com.anysoft.stream.AbstractHandler;
import com.anysoft.stream.DispatchHandler;
import com.anysoft.stream.Handler;
import com.anysoft.stream.HubHandler;
import com.anysoft.stream.MatcherFilter;
import com.anysoft.stream.RateFilter;
import com.anysoft.util.Properties;

/**
 * 追踪日志接口
 * 
 * @author duanyy
 * @since 1.6.5.3
 * 
 * @version 1.6.5.6 [20160523 duanyy] <br>
 * - 增加过滤器插件 <br>
 */
public interface TraceLogger extends Handler<TraceLog>{
	public static class Dispatch extends DispatchHandler<TraceLog> implements TraceLogger{
		public String getHandlerType(){
			return "logger";
		}
	}
	
	public static class Hub extends HubHandler<TraceLog> implements TraceLogger{
		public String getHandlerType(){
			return "logger";
		}
	}	
	
	public static class Default extends AbstractHandler<TraceLog> implements TraceLogger{
		protected void onHandle(TraceLog _data,long t) {
			// nothing to do
		}
		
		protected void onFlush(long t) {
			// nothing to do
		}

		protected void onConfigure(Element e, Properties p) {
			// nothing to do
		}		
	}
	
	public static class Debug extends AbstractHandler<TraceLog> implements TraceLogger{
		/**
		 * a logger of log4j
		 */
		private static final Logger LOG = LogManager.getLogger(Debug.class);

		/**
		 * 输出间隔符
		 */
		protected String delimeter = "%%";	
		
		/**
		 * 单条记录的缓存
		 */
		protected StringBuffer buf = new StringBuffer();	
		
		protected void onHandle(TraceLog item,long t) {
			buf.setLength(0);
			buf.append(item.sn()).append(delimeter)
			.append(item.order()).append(delimeter)
			.append(item.method()).append(delimeter)
			.append(item.startDate()).append(delimeter)
			.append(item.duration()).append(delimeter)
			.append(item.contentLength()).append(delimeter)
			.append(item.code()).append(delimeter)
			.append(item.reason());
					
			LOG.info(buf.toString());
		}
		
		protected void onFlush(long t) {
			// nothing to do
		}

		protected void onConfigure(Element e, Properties p) {
			// nothing to do
		}		
	}
	
	public static class Matcher extends MatcherFilter<TraceLog> implements TraceLogger{
		public String getHandlerType(){
			return "logger";
		}		
	}
	
	public static class Rate extends RateFilter<TraceLog> implements TraceLogger{
		public String getHandlerType(){
			return "logger";
		}		
	}	
}
