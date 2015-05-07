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
 * @version 1.2.9.1 [20141017 duanyy] <br>
 * - 实现Reportable接口 <br>
 * 
 * @version 1.6.3.21 [20150507 duanyy] <br>
 * - 增加全局序列号的支持 <br>
 */
public interface Call extends AutoCloseable,XMLConfigurable,Reportable{
	
	/**
	 * 创建参数实例
	 * @return 参数实例
	 */
	public Parameters createParameter();
	
	/**
	 * 执行运程调用
	 * @param paras 调用参数
	 * @return 调用结果
	 * @throws CallException
	 */
	public Result execute(Parameters paras) throws CallException;
	
	/**
	 * 执行运程调用
	 * @param paras 调用参数
	 * @param globalSerial 全局序列号
	 * @return 调用结果
	 * @throws CallException
	 */
	public Result execute(Parameters paras,String globalSerial) throws CallException;	
}
