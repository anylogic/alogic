package com.alogic.timer;

import java.util.Map;

import org.w3c.dom.Element;

import com.anysoft.util.BaseException;
import com.anysoft.util.Configurable;
import com.anysoft.util.Properties;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;

/**
 * 上下文持有者
 * 
 * @author duanyy
 * @since 1.6.3.37
 */
public interface ContextHolder extends Reportable,Configurable,XMLConfigurable{
	/**
	 * 获取当前的Context
	 * @return Context
	 */
	public TaskContext getContext();
	
	/**
	 * 保存Context
	 * @param ctx Context
	 * @param task Task
	 */
	public void saveContext(TaskContext ctx,Task task);
	
	/**
	 * 缺省实现
	 * @author duanyy
	 *
	 */
	public static class Default implements ContextHolder{
		protected TaskContext ctx = new TaskContext();
		
		public void report(Element xml) {
			if (xml != null){
				xml.setAttribute("module", getClass().getName());
			}
		}

		public void report(Map<String, Object> json) {
			if (json != null){
				json.put("module", getClass().getName());
			}
		}

		public TaskContext getContext() {
			return ctx;
		}

		@Override
		public void saveContext(TaskContext _ctx, Task task) {
			ctx = _ctx;
		}

		@Override
		public void configure(Properties p) throws BaseException {
			// nothing to do
		}

		@Override
		public void configure(Element _e, Properties _properties)
				throws BaseException {
			Properties p = new XmlElementProperties(_e,_properties);
			configure(p);				
		}
		
	}
}
