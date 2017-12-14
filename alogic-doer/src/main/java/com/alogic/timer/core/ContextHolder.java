package com.alogic.timer.core;

import java.util.Map;

import org.w3c.dom.Element;

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
	public DoerContext getContext();
	
	/**
	 * 保存Context
	 * @param ctx Context
	 * @param task Task
	 */
	public void saveContext(DoerContext ctx,Doer task);
	
	/**
	 * 缺省实现
	 * @author duanyy
	 *
	 */
	public static class Default implements ContextHolder{
		protected DoerContext ctx = new DoerContext();
		
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

		public DoerContext getContext() {
			return ctx;
		}

		public void saveContext(DoerContext _ctx, Doer task) {
			ctx = _ctx;
		}

		public void configure(Properties p) {
			// nothing to do
		}

		public void configure(Element _e, Properties _properties){
			Properties p = new XmlElementProperties(_e,_properties);
			configure(p);				
		}
		
	}
}
