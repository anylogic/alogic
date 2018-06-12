package com.alogic.cube.mdr;

import org.w3c.dom.Element;

import com.anysoft.util.Configurable;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;

/**
 * 量度
 * @author duanyy
 * @since 1.6.11.35
 */
public interface Measure {
	
	/**
	 * 计算方法
	 * @author duanyy
	 *
	 */
	public static enum Method{
		max,min,sum
	}
	
	/**
	 * 获得计算方法
	 * @return method
	 */
	public Method getMethod();
	
	/**
	 * 从数据中提取量度的片段值
	 * 
	 * @param data 原始行数据
	 * @return 量度值
	 */
	public long getValue(final Properties provider);
	
	/**
	 * 获取量度id
	 * @return id
	 */
	public String getId();
	
	/**
	 * 获取样式
	 * @return 样式
	 */
	public String getStyle();	
	
	/**
	 * 缺省实现
	 * @author duanyy
	 *
	 */
	public static class Default implements Measure,XMLConfigurable,Configurable{
		/**
		 * id
		 */
		protected String id;
		
		/**
		 * 取值模板
		 */
		protected String $value;
		
		/**
		 * 计算方法
		 */
		protected Method method;
		
		/**
		 * 样式
		 */
		protected String style;	
		
		@Override
		public void configure(Properties p) {
			id = PropertiesConstants.getString(p,"id","",true);
			$value = PropertiesConstants.getRaw(p,"value","");
			method = Method.valueOf(PropertiesConstants.getString(p, "method","sum"));
			style = PropertiesConstants.getString(p,"style","");
		}

		@Override
		public void configure(Element e, Properties p) {
			Properties props = new XmlElementProperties(e,p);
			configure(props);
		}

		@Override
		public Method getMethod() {
			return method;
		}

		@Override
		public long getValue(final Properties provider) {
			return PropertiesConstants.transform(provider, $value, 0);
		}

		@Override
		public String getId() {
			return id;
		}

		@Override
		public String getStyle() {
			return style;
		}		
		
	}
}
