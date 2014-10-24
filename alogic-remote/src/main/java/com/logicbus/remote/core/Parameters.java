package com.logicbus.remote.core;

import com.anysoft.formula.DataProvider;
import com.anysoft.util.JsonSerializer;


/**
 * 服务调用参数
 * 
 * @author duanyy
 *
 * @since 1.2.9
 */
public interface Parameters extends DataProvider{
	
	/**
	 * 设置单个服务参数
	 * 
	 * @param id 参数Id
	 * @param value 参数值
	 * @return
	 */
	public Parameters param(String id,String value);
	
	/**
	 * 批量设置服务参数
	 * 
	 * @param idvalues 参数名值列表
	 * @return
	 */
	public Parameters params(String...idvalues);
	
	/**
	 * 设置单个服务参数对象
	 * @param id
	 * @param value
	 * @return
	 */
	public <data extends JsonSerializer> Parameters param(String id,data value);
	
	/**
	 * 设置单个服务参数对象
	 * @param id
	 * @param builder
	 * @return
	 */
	public <data> Parameters param(String id,data value,Builder<data> builder);
	
	/**
	 * 清空当前所有的参数和对象
	 * @return
	 */
	public Parameters clean();
	
	/**
	 * 获取指定的参数对象
	 * @param id 对象id
	 * @return
	 */
	public Object getData(String id);
}
