package com.anysoft.util;


/**
 * 计数接口
 * 
 * @author duanyy
 * 
 * @since 1.5.2
 *
 */
public interface Counter extends Reportable{
	
	/**
	 * 计数
	 * @param duration 耗时
	 * @param error 是否错误
	 */
	public void count(long duration,boolean error);
	
	/**
	 * 工厂类
	 * @author duanyy
	 *
	 */
	public static class TheFactory extends Factory<Counter>{
		protected static TheFactory instance;
		public static Counter getCounter(String module,Properties p)throws BaseException{
			return instance.newInstance(module, p);
		}
		
		static {
			instance = new TheFactory();
		}
	}
}
