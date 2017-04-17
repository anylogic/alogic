package com.alogic.rpc;

import java.util.ArrayList;
import java.util.List;

import com.anysoft.util.KeyGen;

/**
 * 调用参数
 * 
 * @author duanyy
 * @since 1.6.7.15
 */
public interface Parameters {
	/**
	 * 获取参数列表
	 * 
	 * @return 参数列表
	 */
	public Object[] params();
	
	/**
	 * 重置参数列表
	 * @return 参数列表
	 */
	public Parameters reset();

	/**
	 * 增加调用参数
	 * @param params 参数对象列表
	 * @return 参数实例
	 */
	public Parameters params(Object... params);	
	
	/**
	 * 获取序列号
	 * @return 序列号
	 */
	public String sn();

	/**
	 * 设置序列号
	 * @param sn 序列号
	 * @return 参数实例
	 */
	public Parameters sn(String sn);
	
	/**
	 * 获取调用序号
	 * @return order
	 */
	public String order();
	
	/**
	 * 设置调用序号
	 * @param order
	 * @return 参数实例
	 */
	public Parameters order(String order);
	
	public Parameters context(InvokeContext ctx);
	
	public InvokeContext context();
	
	/**
	 * 缺省实现
	 * @author duanyy
	 *
	 */
	public static class Default implements Parameters{
		/**
		 * 参数列表
		 */
		protected List<Object> parameters = new ArrayList<Object>(3); // NOSONAR
		
		/**
		 * 序列号
		 */
		protected String sn = KeyGen.uuid(8, 0, 16);
		
		protected InvokeContext ctx = null;
		
		/**
		 * 调用序号
		 */
		protected String order = "1";
		
		public Default(){
		}
		
		@Override
		public Object[] params() {
			return parameters.toArray();
		}

		@Override
		public Parameters reset() {
			parameters.clear();
			return this;
		}

		@Override
		public Parameters params(Object... params) {
			for (Object o:params){
				parameters.add(o);
			}
			return this;
		}

		@Override
		public String sn() {
			return sn;
		}

		@Override
		public Parameters sn(String sno) {
			sn = sno;
			return this;
		}

		@Override
		public String order() {
			return order;
		}

		@Override
		public Parameters order(String o) {
			order = o;
			return this;
		}

		@Override
		public Parameters context(InvokeContext ctx) {
			this.ctx = ctx;
			return this;
		}

		@Override
		public InvokeContext context() {
			return this.ctx;
		}
		
	}
}
