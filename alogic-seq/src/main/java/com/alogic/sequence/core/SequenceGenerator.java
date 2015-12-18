package com.alogic.sequence.core;

import java.util.Map;
import java.util.Random;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
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
 * @version 1.6.4.19 [duanyy 20151218] <br>
 * - 按照SONAR建议修改代码 <br>
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
	 public abstract static class Abstract implements SequenceGenerator{
		protected static final Logger LOG = LogManager.getLogger(SequenceGenerator.class);
		/**
		 * 字符表
		 */
		private static final char[] CHARS = {
		      'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
		      'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
		      'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',
		      'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
		      'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
		      'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
		      '8', '9'
		 };
		
		private volatile long start = 0;
		private volatile long end = 0;
		private volatile long current = 0;
		private String id = "default";
			
		/**
		 * 当前序列号容量
		 */
		private long capacity = 1000;
		
		@Override
		public String id(){return id;}
		
		protected void setRange(long pStart,long pEnd){
			start = pStart;
			current = start;
			end = pEnd;
		}
		
		@Override
		public synchronized long nextLong() {
			if (current<end){
				return current ++;
			}else{
				onMore(current,capacity);
				current = start;
				return current ++;
			}
		}
			
		public abstract void onMore(long current, long capacity);
		
		/**
		 * 按照指定宽度生成随机字符串
		 * @param pWidth 字符串的宽度
		 * @return 随机字符串
		 */
		public static String randomString(int pWidth){
			int width = pWidth <= 0 ? 6 : pWidth;
			char [] ret = new char[width];
			Random ran = new Random();
			for (int i = 0 ; i < width ; i ++){
				int intValue = ran.nextInt(62) % 62;
				ret[i] = CHARS[intValue];
			}
			
			return new String(ret);
		}
		
		/**
		 * 生成20位的随机字符串
		 * @return 随机字符串
		 */
		public static String randomString(){
			return randomString(20);
		}

		@Override
		public void configure(Element element, Properties props){
			Properties p = new XmlElementProperties(element,props);
			
			capacity = PropertiesConstants.getLong(p,"capacity",capacity); // NOSONAR
			id = PropertiesConstants.getString(p,"id",id);
			onConfigure(element,p);			
			onMore(current,capacity);
		}
		
		/**
		 * 处理Configure事件
		 * @param e 配置的XML节点
		 * @param p 变量集
		 */
		public abstract void onConfigure(Element e,Properties p);

		@Override
		public void report(Element xml) {
			if (xml != null){
				xml.setAttribute("module", getClass().getName());
				xml.setAttribute("capacity",String.valueOf(capacity));
				xml.setAttribute("start", String.valueOf(start));
				xml.setAttribute("end", String.valueOf(end));
				xml.setAttribute("current", String.valueOf(current));
			}
		}

		@Override
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
		
		@Override
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
