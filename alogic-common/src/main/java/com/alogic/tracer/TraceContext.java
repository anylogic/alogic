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
 * 
 * @version 1.6.7.1 [20170117 duanyy] <br>
 * - trace日志调用链中的调用次序采用xx.xx.xx.xx字符串模式 <br>
 * 
 * @version 1.6.7.3 [20170118 duanyy] <br>
 * - trace日志的时长单位改为ns <br>
 * - 新增com.alogic.tlog，替代com.alogic.tracer.log包;
 * 
 * @version 1.6.8.6 [20170410 duanyy] <br>
 * - 服务调用全局序列号采用随机64位数字(16进制) <br>
 * 
 * @version 1.6.9.3 [20170615 duanyy] <br>
 * - 修正tlog的全局序列号不规范问题 <br>
 * 
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
	public String order();
		
	/**
	 * 时间戳
	 * @return 时间戳
	 */
	public long timestamp();
	
	/**
	 * 开始时间，单位:ns
	 * @return 开始时间
	 */
	public long startTime();
	
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
		protected volatile String order = "1";
		
		/**
		 * 子节点次序
		 */
		protected volatile int childOrder = 1;
		
		/**
		 * 时间戳
		 */
		protected long t = System.currentTimeMillis();
		
		/**
		 * 开始时间：ns
		 */
		protected long start = System.nanoTime();
		
		/**
		 * 父节点
		 */
		protected transient TraceContext parent = null;
		
		public Default(){
			sn = KeyGen.uuid(8,0,15);
			order = "1";
		}
		
		public Default(TraceContext p,String n,String o){
			parent = p;
			sn = StringUtils.isEmpty(n)?KeyGen.uuid(8,0,15):n;
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
		public String order() {
			return order;
		}
		
		public Default order(String o){
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
				XmlTools.setString(xml, "order", order);
				XmlTools.setLong(xml,"t",t);
			}
		}

		@Override
		public void report(Map<String, Object> json) {
			if (json != null){
				JsonTools.setString(json,"sn",sn);
				JsonTools.setString(json,"order",order);
				JsonTools.setLong(json, "t", t);
			}
		}

		@Override
		public TraceContext parent() {
			return parent;
		}

		@Override
		public synchronized TraceContext newChild() {
			return new TraceContext.Default(this,sn(),order + "." + (childOrder ++));
		}

		@Override
		public long startTime() {
			return start;
		}
		
	}
}
