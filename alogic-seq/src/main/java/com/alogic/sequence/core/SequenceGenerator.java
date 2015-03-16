package com.alogic.sequence.core;

import java.util.Map;
import java.util.Random;

import org.w3c.dom.Element;

import com.anysoft.util.BaseException;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;

/**
 * 序列号生成器
 * 
 * @author duanyy
 * @since 1.6.3.5
 * 
 */
public interface SequenceGenerator extends XMLConfigurable,Reportable{
	/**
	 * 获取生成器的ID
	 * 
	 * @return id
	 */
	public String id();
	/**
	 * 提取long型的全局序列号
	 * 
	 * @return 序列号
	 */
	public long nextLong();
	
	/**
	 * 提取String型的全局序列号
	 * 
	 * @return 序列号
	 */
	public String nextString();
	
	/**
	 * 虚基类
	 * 
	 * @author duanyy
	 * @since 1.6.3.5
	 */
	abstract public static class Abstract implements SequenceGenerator{
		private volatile long start = 0;
		private volatile long end = 0;
		private volatile long current = 0;
		private String id = "default";
		/**
		 * 当前序列号容量
		 */
		private long capacity = 1000;
		
		protected void setRange(long _start,long _end){
			start = _start;
			current = start;
			end = _end;
		}
		
		synchronized public long nextLong() {
			if (current<end){
				return current ++;
			}else{
				onMore(current,capacity);
				current = start;
				return current ++;
			}
		}
		
		public String id(){return id;}
		
		abstract public void onMore(long current, long capacity);
		
		/**
		 * 字符表
		 */
		protected static final char[] Chars = {
		      'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
		      'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
		      'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',
		      'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
		      'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
		      'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
		      '8', '9'
		 };
		
		/**
		 * 按照指定宽度生成随机字符串
		 * @param _width 字符串的宽度
		 * @return 随机字符串
		 */
		static public String randomString(int _width){
			int width = _width <= 0 ? 6 : _width;
			char [] ret = new char[width];
			Random ran = new Random();
			for (int i = 0 ; i < width ; i ++){
				int intValue = ran.nextInt(62) % 62;
				ret[i] = Chars[intValue];
			}
			
			return new String(ret);
		}
		
		/**
		 * 生成20位的随机字符串
		 * @return 随机字符串
		 */
		static public String randomString(){
			return randomString(20);
		}

		public void configure(Element _e, Properties _properties)
				throws BaseException {
			Properties p = new XmlElementProperties(_e,_properties);
			
			capacity = PropertiesConstants.getLong(p,"capacity",capacity);
			id = PropertiesConstants.getString(p,"id",id);
			onConfigure(_e,p);
			
			onMore(current,capacity);
		}
		
		/**
		 * 处理Configure事件
		 * @param e 配置的XML节点
		 * @param p 变量集
		 */
		abstract public void onConfigure(Element e,Properties p);

		public void report(Element xml) {
			if (xml != null){
				xml.setAttribute("module", getClass().getName());
				xml.setAttribute("capacity",String.valueOf(capacity));
				xml.setAttribute("start", String.valueOf(start));
				xml.setAttribute("end", String.valueOf(end));
				xml.setAttribute("current", String.valueOf(current));
			}
		}

		public void report(Map<String, Object> json) {
			if (json != null){
				JsonTools.setString(json, "module", getClass().getName());
				JsonTools.setLong(json, "capacity", capacity);
				JsonTools.setLong(json, "start", start);
				JsonTools.setLong(json, "end", end);
				JsonTools.setLong(json, "current", current);
			}
		}
	}
	
	/**
	 * 简单实现
	 * 
	 * @author duanyy
	 * @since 1.6.3.5
	 */
	public static class Simple extends Abstract {
		protected int stringWidth = 20;
		
		public String nextString() {
			return randomString(stringWidth);
		}

		@Override
		public void onMore(long current, long capacity) {
			setRange(current,current + capacity);
		}

		@Override
		public void onConfigure(Element e, Properties p) {
			stringWidth = PropertiesConstants.getInt(p,"stringWidth", stringWidth);
		}
		
	}
}
