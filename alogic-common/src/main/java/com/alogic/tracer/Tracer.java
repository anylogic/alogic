package com.alogic.tracer;

import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.alogic.tracer.log.TraceLog;
import com.alogic.tracer.log.TraceLogger;
import com.anysoft.util.Configurable;
import com.anysoft.util.Factory;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

/**
 * 追踪器
 * 
 * @author duanyy
 * @since 1.6.5.3
 * 
 * @version 1.6.5.7 [20160525 duanyy] <br>
 * - 增加enable方法，以便可以选择关闭tracer <br>
 * 
 * @version 1.6.7.1 [20170117 duanyy] <br>
 * - trace日志调用链中的调用次序采用xx.xx.xx.xx字符串模式 <br>
 * 
 */
public interface Tracer extends Reportable,Configurable,XMLConfigurable{
	
	/**
	 * 是否启用Tracer
	 * @return 是否
	 */
	public boolean enable();
	
	/**
	 * 开始过程
	 * 
	 * @return 上下文实例
	 */
	public TraceContext startProcedure();
	
	/**
	 * 以指定的序列号和顺序开始过程
	 * @param sn 序列号
	 * @param order 顺序
	 * 
	 * @return 上下文实例
	 */
	public TraceContext startProcedure(String sn,String order);
	
	/**
	 * 结束过程
	 * @param ctx 上下文实例
	 * @param type 过程类型
	 * @param name 过程名称
	 * @param result 结果
	 * @param note 说明
	 * @param contentLength 内容长度
	 */
	public void endProcedure(TraceContext ctx,String type,String name,String result,String note,long contentLength);
	
	/**
	 * 虚基类
	 * @author duanyy
	 *
	 */
	public abstract static class Abstract implements Tracer{
		/**
		 * a trace logger
		 */
		protected TraceLogger logger=null;
		
		/**
		 * 是否开放
		 */
		protected boolean enable = false;
		
		/**
		 * a logger of log4j
		 */
		protected static final Logger LOG = LogManager.getLogger(Tracer.class);
		
		/**
		 * 是否开放
		 */
		public boolean enable(){
			return enable;
		}
		
		@Override
		public void report(Element xml) {
			if (xml != null){
				XmlTools.setString(xml,"module",getClass().getName());
			}
		}

		@Override
		public void report(Map<String, Object> json) {
			if (json != null){
				JsonTools.setString(json,"module",getClass().getName());
			}
		}
		
		@Override
		public void configure(Element e, Properties p) {
			Properties props = new XmlElementProperties(e,p);
			configure(props);
			
			enable = PropertiesConstants.getBoolean(props, "enable", enable);
			
			Element elem = XmlTools.getFirstElementByPath(e, "logger");
			
			if (elem != null){
				Factory<TraceLogger> factory = new Factory<TraceLogger>();				
				try {
					logger = factory.newInstance(elem, p, "module", TraceLogger.Default.class.getName());
				}catch (Exception ex){
					logger = new TraceLogger.Default();
					logger.configure(elem, p);
					LOG.error("Can not create a trace logger,using default:" + logger.getClass().getName(),ex);
				}
			}
		}	
		
		public void log(TraceLog log){
			if (logger != null){
				logger.handle(log, System.currentTimeMillis());
			}
		}
	}
}
