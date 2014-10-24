package com.logicbus.together;

import java.util.concurrent.TimeUnit;

import com.anysoft.util.Factory;
import com.logicbus.backend.Context;


/**
 * 执行过程监视器
 * 
 * @author duanyy
 * @version 1.2.1 [20140613 duanyy]
 * - executed方法增加Context参数
 * - 增加Factory类
 */
public interface ExecuteWatcher {
	
	/**
	 * Logiclet执行完成
	 * @param logiclet logiclet
	 * @param duration 耗时
	 */
	public void executed(Logiclet logiclet,Context ctx,long duration,TimeUnit timeUnit);
	
	/**
	 * 工厂类 
	 * @author duanyy
	 * @since 1.2.1
	 */
	public static class TheFactory extends Factory<ExecuteWatcher>{
		public TheFactory(ClassLoader cl){
			super(cl);
		}
	}
}
