package com.alogic.tracer;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import com.anysoft.util.JsonTools;
import com.anysoft.util.KeyGen;
import com.anysoft.util.Reportable;
import com.anysoft.util.XmlTools;

/**
 * 追踪上下文
 * 
 * @author duanyy
 * @since 1.6.5.3
 */
public interface TraceContext extends Reportable{
	/**
	 * 序列号
	 * @return 序列号
	 */
	public String sn();
	
	/**
	 * 次序
	 * @return 次序
	 */
	public long order();
		
	/**
	 * 时间戳
	 * @return 时间戳
	 */
	public long timestamp();
	
	/**
	 * 父节点
	 * @return 父节点
	 */
	TraceContext parent();
	
	/**
	 * 新创建子节点
	 * @return 子节点
	 */
	TraceContext newChild();
	
	/**
	 * 缺省实现
	 * 
	 * @author duanyy
	 *
	 */
	public static class Default implements TraceContext {
		/**
		 * 序列号
		 */
		protected String sn;
		
		/**
		 * 次序
		 */
		protected volatile long order = 1;
		
		/**
		 * 子节点次序
		 */
		protected volatile int childOrder = 1;
		
		/**
		 * 时间戳
		 */
		protected long t = System.currentTimeMillis();
		
		/**
		 * 父节点
		 */
		protected transient TraceContext parent = null;
		
		public Default(){
			sn = KeyGen.uuid(10, 64);
			order = 1;
		}
		
		public Default(TraceContext p,String n,long o){
			parent = p;
			sn = StringUtils.isEmpty(n)?KeyGen.uuid(10, 64):n;
			order = o;
		}		
		
		@Override
		public String sn() {
			return sn;
		}

		public Default sn(String n){
			sn = n;
			return this;
		}
		
		@Override
		public long order() {
			return order;
		}
		
		public Default order(long o){
			order = o;
			return this;
		}

		@Override
		public long timestamp() {
			return t;
		}
		
		public Default timestamp(long now){
			t = now;
			return this;
		}

		@Override
		public void report(Element xml) {
			if (xml != null){
				XmlTools.setString(xml,"sn",sn);
				XmlTools.setLong(xml, "order", order);
				XmlTools.setLong(xml,"t",t);
			}
		}

		@Override
		public void report(Map<String, Object> json) {
			if (json != null){
				JsonTools.setString(json,"sn",sn);
				JsonTools.setLong(json,"order",order);
				JsonTools.setLong(json, "t", t);
			}
		}

		@Override
		public TraceContext parent() {
			return parent;
		}

		@Override
		public synchronized TraceContext newChild() {
			return new TraceContext.Default(this,sn(),order * 100 + (childOrder ++));
		}
		
	}
}
