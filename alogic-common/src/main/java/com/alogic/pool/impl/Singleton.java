package com.alogic.pool.impl;

import java.util.Map;

import org.w3c.dom.Element;

import com.alogic.pool.CloseAware;
import com.alogic.pool.Pool;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.XmlElementProperties;

/**
 * 基于单例模式的实现
 * 
 * 保持一个共享对象，重复使用
 * 
 * @author duanyy
 * 
 * @version 1.6.9.9 [20170829 duanyy] <br>
 * - Pool的returnObject接口增加是否出错的参数 <br>
 */
public abstract class Singleton implements Pool,CloseAware{
	/**
	 * 唯一实例
	 */
	protected Object instance = null;

	/**
	 * 创建实例
	 * @param priority 优先级
	 * @param timeout 超时时间
	 * @return 对象实例
	 */
	protected abstract <pooled> pooled createObject(int priority, int timeout);
	
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

	@Override
	public void close(){
		if (instance != null && instance instanceof AutoCloseable){
			IOTools.close((AutoCloseable)instance);
		}
	}

	@Override
	public void closeObject(Object pooled) {
		// never close
	}

	@SuppressWarnings("unchecked")
	@Override
	public <pooled> pooled borrowObject(int priority, int timeout) {
		if (instance == null){
			synchronized(this){
				if (instance == null){
					instance = createObject(priority,timeout);
				}
			}
		}
		
		return (pooled)instance;
	}

	@Override
	public <pooled> void returnObject(pooled obj,boolean hasError) {
		// nothing to do
	}
}
