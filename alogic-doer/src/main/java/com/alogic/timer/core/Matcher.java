package com.alogic.timer.core;

import java.util.Date;
import java.util.Map;

import org.w3c.dom.Element;

import com.anysoft.util.BaseException;
import com.anysoft.util.Configurable;
import com.anysoft.util.Properties;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;

/**
 * 调度匹配器
 * 
 * @author duanyy
 * @since 1.6.3.37
 */
public interface Matcher extends Configurable,XMLConfigurable,Reportable {
	
	/**
	 * 是否匹配
	 * @param _last 上次调度时间
	 * @param _now 当前时间
	 * @param _ctxHolder 上下文持有人
	 * @return true|false
	 */
	public boolean match(Date _last,Date _now,ContextHolder _ctxHolder);
	
	/**
	 * 是否可以清除
	 * 
	 * <br>
	 * 如果返回为true，框架将从列表中清除该Timer
	 * @return true/false
	 */
	public boolean isTimeToClear();	
	
	/**
	 * Abstract实现
	 * 
	 * @author duanyy
	 * @since 1.6.3.37
	 */
	abstract public static class Abstract implements Matcher{

		public void configure(Element _e, Properties _properties)
				throws BaseException {
			Properties p = new XmlElementProperties(_e,_properties);
			configure(p);
		}

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
	}
}
