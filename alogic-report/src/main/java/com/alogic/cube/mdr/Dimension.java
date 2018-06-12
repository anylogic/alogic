package com.alogic.cube.mdr;

import org.w3c.dom.Element;

import com.anysoft.util.Configurable;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;

/**
 * 维度
 * @author duanyy
 * @since 1.6.11.35
 */
public interface Dimension {
	/**
	 * 获取维度id
	 * @return
	 */
	public String getId();
	
	/**
	 * 从数据中提取维度值
	 * 
	 * @param data 原始行数据
	 * @return 维度值
	 */
	public String getValue(final Properties provider);
	
	/**
	 * 获取样式
	 * @return 样式
	 */
	public String getStyle();
	
	/**
	 * 获取子维度
	 * @return 子维度
	 */
	public Dimension next();
	
	/**
	 * 添加子维度
	 * @param dim 子维度
	 */
	public void append(Dimension dim);
	
	/**
	 * 缺省实现
	 * @author yyduan
	 *
	 */
	public static class Default implements Dimension,XMLConfigurable,Configurable{
		/**
		 * id
		 */
		protected String id;
		
		/**
		 * 取值模板
		 */
		protected String $value;
		
		/**
		 * 样式
		 */
		protected String style;
		
		/**
		 * 子维度
		 */
		protected Dimension next = null;
		
		@Override
		public void configure(Properties p) {
			id = PropertiesConstants.getString(p,"id","",true);
			$value = PropertiesConstants.getRaw(p,"value","");
			style = PropertiesConstants.getString(p,"style","");
		}

		@Override
		public void configure(Element e, Properties p) {
			Properties props = new XmlElementProperties(e,p);
			configure(props);
		}

		@Override
		public String getId() {
			return id;
		}

		@Override
		public String getValue(Properties provider) {
			return PropertiesConstants.transform(provider, $value, id);
		}

		@Override
		public String getStyle() {
			return style;
		}

		@Override
		public Dimension next() {
			return null;
		}

		@Override
		public void append(Dimension dim) {
			if (next != null){
				next.append(dim);
			}else{
				next = dim;
			}
		}
		
	}
}
