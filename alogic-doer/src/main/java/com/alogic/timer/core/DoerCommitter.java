package com.alogic.timer.core;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.util.BaseException;
import com.anysoft.util.Configurable;
import com.anysoft.util.Counter;
import com.anysoft.util.Properties;
import com.anysoft.util.Reportable;
import com.anysoft.util.SimpleCounter;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;

/**
 * 任务提交者
 * 
 * @author duanyy
 * @since 1.6.3.37
 * 
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 */
public interface DoerCommitter extends Reportable,Configurable,XMLConfigurable{
	
	/**
	 * 提交任务
	 * 
	 * @param doer 任务执行者
	 * @param task 任务
	 */
	public void commit(Doer doer,Task task);
	
	/**
	 * Abstract
	 * @author duanyy
	 *
	 */
	abstract public static class Abstract implements DoerCommitter{
		/**
		 * a logger of log4j
		 */
		protected static final Logger logger = LoggerFactory.getLogger(DoerCommitter.class);
		
		/**
		 * 计数器
		 */
		protected Counter counter = null;
		
		public void report(Element xml) {
			if (xml != null){
				xml.setAttribute("module", getClass().getName());
				
				if (counter != null){
					Document doc = xml.getOwnerDocument();
					Element _counter = doc.createElement("counter");
					counter.report(_counter);
					xml.appendChild(_counter);
				}
			}
		}

		public void report(Map<String, Object> json) {
			if (json != null){
				json.put("module", getClass().getName());
				
				if (counter != null){
					Map<String,Object> _counter = new HashMap<String,Object>();
					counter.report(_counter);
					json.put("counter", _counter);
				}
			}
		}

		public void configure(Properties p) throws BaseException {
			counter = new SimpleCounter(p);
		}

		public void configure(Element _e, Properties _properties)
				throws BaseException {
			Properties p = new XmlElementProperties(_e,_properties);
			configure(p);	
		}

		public void commit(Doer doer,Task task) {
			long start = System.currentTimeMillis();
			boolean error = false;
			try {
				onCommit(doer,task);
			}catch (Exception ex){
				error = true;
				logger.error("Fail to commit task.",ex);
			}finally{
				if (counter != null){
					counter.count(System.currentTimeMillis() - start, error);
				}
			}
		}
		
		abstract protected void onCommit(Doer doer, Task task);
	}
}
