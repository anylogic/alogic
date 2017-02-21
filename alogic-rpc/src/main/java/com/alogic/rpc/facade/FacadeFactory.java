package com.alogic.rpc.facade;

import java.util.Map;

import org.w3c.dom.Element;

import com.anysoft.util.Configurable;
import com.anysoft.util.Properties;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;

/**
 * 桩代码工厂
 * 
 * @author duanyy
 * @since 1.6.7.15
 */
public interface FacadeFactory extends Reportable,Configurable,XMLConfigurable{
	FacadeFactory DEFAULT = new FacadeFactoryImpl();
	
	/**
	 * 通过接口类名获取桩实例
	 * 
	 * @param callId call
	 * @param clazz 接口类名
	 * @return 桩实例
	 */
	public <I> I getInterface(String callId,Class<I> clazz);
	
	/**
	 * 通过接口类名获取桩实例
	 * 
	 * @param callId call
	 * @param service 服务id
	 * @param clazz 接口类名
	 * @return 桩实例
	 */
	public <I> I getInterface(String callId,String service,Class<I> clazz);	
	
	/**
	 * 虚基类
	 * 
	 * @author duanyy
	 *
	 */
	public abstract static class Abstract implements FacadeFactory{

		@Override
		public void report(Element xml) {
			if (xml != null){
				xml.setAttribute("module", getClass().getName());
			}
		}

		@Override
		public void report(Map<String, Object> json) {
			if (json != null){
				json.put("module", getClass().getName());
			}
		}

		@Override
		public void configure(Element e, Properties p) {
			XmlElementProperties props = new XmlElementProperties(e,p);
			configure(props);			
		}

	}
}
