package com.logicbus.together;

import com.anysoft.util.BaseException;
import com.anysoft.util.Factory;
import com.anysoft.util.Settings;

/**
 * Logiclet实例创建工厂
 * @author duanyy
 *
 */
public interface LogicletFactory {
	
	/**
	 * 根据module创建Logiclet实例
	 * 
	 * @param module
	 * @return
	 */
	public Logiclet newLogiclet(String module);
	
	/**
	 * 缺省实现
	 * @author duanyy
	 *
	 */
	public static class Default extends Factory<Logiclet> implements LogicletFactory {
		public Default(ClassLoader cl){
			super(cl);
		}
		
		
		public String getClassName(String _module) throws BaseException{
			if (_module.indexOf('.') >= 0){
				return _module;
			}
			return "com.logicbus.together.logiclet." + _module;
		}

		
		synchronized public Logiclet newLogiclet(String module) {
			if (classLoader == null){
				Settings settings = Settings.get();
				
				classLoader = (ClassLoader) settings.get("classLoader");
				if (classLoader == null){
					classLoader = Thread.currentThread().getContextClassLoader();
				}
			}
			return newInstance(module);
		}
	}
}
