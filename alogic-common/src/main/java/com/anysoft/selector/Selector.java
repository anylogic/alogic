package com.anysoft.selector;

import java.util.HashMap;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.anysoft.formula.DataProvider;
import com.anysoft.util.BaseException;
import com.anysoft.util.Factory;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;

/**
 * 字段选择器
 * 
 * <br>
 * 字段选择器，负责从{@link com.anysoft.formula.DataProvider DataProvider}中获取数据值.
 * 
 * @author duanyy
 * @since 1.5.2
 * 
 */
abstract public class Selector implements XMLConfigurable {
	
	/**
	 * a logger of log4j
	 */
	protected Logger logger = LogManager.getLogger(FieldList.class);
	
	/**
	 * id
	 */
	protected String id;

	/**
	 * to get the id
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * default value
	 */
	private String defaultValue = "0";

	/**
	 * to get the default value
	 * @return 
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * 是否忽略异常
	 */
	protected boolean ignoreException = false;

	/**
	 * 是否OK
	 * @return
	 */
	public boolean isOk() {
		return id.length() > 0;
	}
	
	/**
	 * 构造函数
	 */
	protected Selector() {
	}
	
	
	public void configure(Element _e, Properties _properties)
			throws BaseException {
		XmlElementProperties p = new XmlElementProperties(_e, _properties);
		id = PropertiesConstants.getString(p, "selector-id", "",true);
		defaultValue =PropertiesConstants.getString(p, "selector-default", defaultValue,true);
		
		ignoreException = PropertiesConstants.getBoolean(p,
				"selector-ignoreException", ignoreException,true);
		onConfigure(_e, p);
	}
	
	/**
	 * Configure事件处理
	 * 
	 * @param _e 配置XML节点
	 * @param _p 变量集
	 * @throws BaseException
	 */
	public abstract void onConfigure(Element _e, Properties _p)
			throws BaseException;	
	
	/**
	 * 统计数据
	 */
	static public HashMap<String, Stat> stats = new HashMap<String, Stat>();

	/**
	 * 统计结构
	 * 
	 * @author duanyy
	 *
	 */
	public static class Stat {
		String _name;
		long _times;
		long _duration;
	}

	/**
	 * 从{@link com.anysoft.formula.DataProvider DataProvider}中获取字段值
	 * @param _dataProvider
	 * @return
	 */
	public String select(DataProvider _dataProvider) {
		long before = System.nanoTime();
		try {
			return onSelect(_dataProvider);
		} catch (RuntimeException ex) {
			if (!ignoreException) {
				throw ex;
			}
			logger.error("Error occurs when onSelect.Default value is returned.",ex);
			return defaultValue;
		} finally {
			String name = getClass().getName();
			Stat found = stats.get(getClass().getName());
			if (found == null) {
				found = new Stat();
				found._name = name;
				found._duration = 0;
				found._times = 0;
				stats.put(name, found);
			}
			found._times++;
			found._duration += System.nanoTime() - before;
		}
	}	
	
	/**
	 * Select事件处理
	 * @param _dataProvider
	 * @return
	 */
	public abstract String onSelect(DataProvider _dataProvider);
	
	/**
	 * 工厂类实例
	 */
	protected static TheFactory theFactory = new TheFactory();
	
	/**
	 * 创建实例
	 * @param e 配置XML节点
	 * @param p 环境变量
	 * @return
	 */
	public static Selector newInstance(Element e,Properties p){
		return theFactory.newInstance(e, p,"selector");
	}
	
	/**
	 * 创建实例
	 * @param e 
	 * @param p
	 * @param preferredClass 指定的类名
	 * @return
	 * 
	 * @since 1.2.8.1
	 */
	public static Selector newInstance(Element e,Properties p,String preferredClass){
		Selector instance = theFactory.newInstance(preferredClass);
		instance.configure(e, p);
		return instance;
	}
	
	/**
	 * 工厂类
	 * 
	 * @author duanyy
	 * @version 3.0.0
	 * 
	 */
	public static class TheFactory extends Factory<Selector>{
		public String getClassName(String _module) throws BaseException{
			String __module = _module;
			if (__module.indexOf(".") < 0) {
				__module = "com.anysoft.selector.impl." + __module;
			}
			return __module;
		}
	}
}
