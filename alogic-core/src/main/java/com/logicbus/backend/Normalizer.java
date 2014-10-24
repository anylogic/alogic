package com.logicbus.backend;

import javax.servlet.http.HttpServletRequest;

import com.anysoft.util.BaseException;
import com.anysoft.util.Factory;
import com.logicbus.models.catalog.Path;

/**
 * 对输入参数进行规范化
 * 
 * <br>
 * 对输入参数进行规范化
 * 
 * @author duanyy
 *
 */
public interface Normalizer {
	
	/**
	 * 规范化
	 * @param context 上下文，用于输出
	 * @param request Http请求
	 * @return 服务路径
	 */
	public Path normalize(Context context,HttpServletRequest request);
	
	/**
	 * Normalizer工厂类
	 * @author duanyy
	 *
	 */
	public static class TheFactory extends Factory<Normalizer>{
		public TheFactory(ClassLoader cl){
			super(cl);
		}
		/**
		 * 根据module映射类名
		 */
		public String getClassName(String _module) throws BaseException{
			if (_module.indexOf(".") < 0){
				return "com.logicbus.backend." + _module;
			}
			return _module;
		}			
	}
}
