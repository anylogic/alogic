package com.alogic.uid;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.anysoft.util.Configurable;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

/**
 * ID生成器
 * 
 * @author yyduan
 *
 * @since 1.6.11.5
 */
public interface IdGenerator extends XMLConfigurable,Configurable,Reportable{
	
	/**
	 * 生成下一个ID
	 * @return id
	 */
	public String nextId();
	
	/**
	 * 生成下一个id
	 * @return id
	 */
	public long nextLong();
	
	/**
	 * 虚基类
	 * @author yyduan
	 *
	 */
	public abstract static class Abstract implements IdGenerator{
		
		/**
		 * a logger of slf4j
		 */
		protected static final Logger LOG = LoggerFactory.getLogger(IdGenerator.class.getName());
		
		@Override
		public String nextId() {
			return String.format("%d", nextLong());
		}
		
		@Override
		public void configure(Element e, Properties p) {
			Properties props = new XmlElementProperties(e,p);
			configure(props);
		}

		@Override
		public void report(Element xml) {
			if (xml != null){
				XmlTools.setString(xml, "module", getClass().getName());
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
	 * 基于预分配模型的IDGen
	 * @author yyduan
	 *
	 */
	public abstract static class Prepare extends Abstract{
		/**
		 * 当前预分配id段的起始值
		 */
		private volatile long start = 0;
		
		/**
		 * 当前预分配id端的结束值
		 */
		private volatile long end = 0;
		
		/**
		 * id的当前值
		 */
		private volatile long current = 0;
		
		/**
		 * 当前id段容量
		 */
		private long capacity = 10;
		
		@Override
		public void configure(Properties p){	
			capacity = PropertiesConstants.getLong(p,"capacity",capacity);		
			onPrepare(current,capacity);
		}
		
		@Override
		public synchronized long nextLong() {
			if (current<end){
				return current ++;
			}else{
				onPrepare(current,capacity);
				current = start;
				return current ++;
			}
		}
		
		/**
		 * 触发预分配
		 * @param current 当前id值
		 * @param capacity 预申请容量
		 */
		public abstract void onPrepare(long current, long capacity);
		
		/**
		 * 进行预分配
		 * @param start 预分配id段的起始值
		 * @param end 预分配id端的结束值
		 */
		protected void doPrepare(long start,long end){
			this.start = start;
			this.current = start;
			this.end = end;
		}
	}
}
