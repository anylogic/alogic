package com.alogic.rpc;

/**
 * 调用结果
 * 
 * @author duanyy
 * @since 1.6.7.15
 */
public interface Result {
	/**
	 * 获取返回对象
	 * @return 返回的对象
	 */
	public Object ret();	
	
	/**
	 * 设置返回对象
	 * @param returnObject 返回对象
	 * @return 结果实例
	 */
	public Result ret(Object returnObject);
	
	/**
	 * 获取结果代码
	 * @return 结果代码
	 */
	public String code();
	
	/**
	 * 获取错误原因
	 * @return 错误原因
	 */
	public String reason();
	
	/**
	 * 获取远端服务器
	 * @return 服务器(IP:PORT)
	 */
	public String host();
	
	/**
	 * 设置服务器
	 * @param host 服务器(IP:PORT)
	 * @return 结果实例
	 */
	public Result host(String host);
	
	/**
	 * 获取服务端的服务时长
	 * @return 服务时长
	 */
	public long duration();	

	/**
	 * 设置本地调用结果
	 * @param code 代码
	 * @param reason 原因
	 * @param duration 时长
	 * @return 结果实例
	 */
	public Result result(String code,String reason,long duration);
	
	/**
	 * 设置调用序号
	 * @param sn 全局序列号
	 * @param order 调用次序
	 * @return 结果实例
	 */
	public Result sn(String sn,String order);
	
	public String sn();
	
	public String order();
	
	/** 返回服务调用异常
	 * @return 异常
	 */
	public Throwable getThrowable();
	
	/**设置服务调用异常
	 * @param t 异常
	 */
	public void setThrowable(Throwable t);
		
	/**
	 * 缺省实现
	 * 
	 * @author duanyy
	 *
	 */
	public static class Default implements Result{
		protected String code = "core.ok";
		protected String reason = "It is ok.";
		protected long duration = 0;
		protected String host;
		protected Object returnObject;
		protected Throwable t;
		protected String sn;
		protected String order;
		
		public Default(){
		}
		
		@Override
		public Object ret() {
			return returnObject;
		}

		@Override
		public Result ret(Object ret) {
			returnObject = ret;
			return this;
		}

		@Override
		public String code() {
			return code;
		}

		@Override
		public String reason() {
			return reason;
		}

		@Override
		public long duration() {
			return duration;
		}

		@Override
		public String host(){
			return host;
		}
		
		@Override
		public Result host(String h){
			host = h;
			return this;
		}
		
		@Override
		public Result result(String c, String msg, long dur) {
			code = c;
			reason = msg;
			duration = dur;
			return this;
		}

		@Override
		public Throwable getThrowable() {
			return this.t;
		}

		@Override
		public void setThrowable(Throwable t) {
			this.t = t;
		}

		@Override
		public Result sn(String sn, String order) {
			this.sn = sn;
			this.order = order;
			return this;
		}

		@Override
		public String sn() {
			return this.sn;
		}

		@Override
		public String order() {
			return this.order;
		}

	}
}
