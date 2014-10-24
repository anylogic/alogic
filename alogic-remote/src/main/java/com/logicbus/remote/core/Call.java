package com.logicbus.remote.core;

import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;

/**
 * 远程服务调用
 * 
 * @author duanyy
 *
 * 
 * @since 1.2.9
 * 
 * @version 1.2.9.1 [20141017 duanyy]
 * - 实现Reportable接口
 * 
 */
public interface Call extends AutoCloseable,XMLConfigurable,Reportable{
	
	/**
	 * 创建参数实例
	 * @return
	 */
	public Parameters createParameter();
	
	/**
	 * 执行运程调用
	 * @param paras
	 * @return
	 * @throws CallException
	 */
	public Result execute(Parameters paras) throws CallException;
}
