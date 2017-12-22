package com.alogic.ha;

import java.util.Map;

import org.w3c.dom.Element;

import com.anysoft.util.Configurable;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;

/**
 * 主备控制器
 * 
 * @author yyduan
 *
 * @since 1.6.8.3
 * 
 * @version 1.6.11.4 [20171222 duanyy] <br>
 * - 增加Null实现 <br>
 */
public interface FailoverController extends Configurable,XMLConfigurable,Reportable{
	
	/**
	 * 当前控制器是否活跃
	 * @return true|false
	 */
	public boolean isActive();
	
	/**
	 * 启动控制器
	 * @param listener
	 */
	public void start(FailoverListener listener);
	
	/**
	 * 停止控制器
	 */
	public void stop();
	
	/**
	 * 虚基类
	 * @author yyduan
	 *
	 */
	public static abstract class Abstract implements FailoverController{
		@Override
		public void configure(Element e, Properties p) {
			Properties props = new XmlElementProperties(e,p);
			configure(props);
		}

		@Override
		public void report(Element xml) {
			if (xml != null){
				xml.setAttribute("module",getClass().getName());
			}
		}

		@Override
		public void report(Map<String, Object> json) {
			if (json != null){
				JsonTools.setString(json,"module",getClass().getName());
			}
		}
	}
	
	/**
	 * Null实现
	 * @author yyduan
	 *
	 */
	public static class Null extends Abstract{

		@Override
		public boolean isActive() {
			return true;
		}

		@Override
		public void start(FailoverListener listener) {
		}

		@Override
		public void stop() {
		}

		@Override
		public void configure(Properties p) {
		}
		
	}
}
