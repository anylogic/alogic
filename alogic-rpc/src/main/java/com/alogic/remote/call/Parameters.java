package com.alogic.remote.call;

import com.anysoft.formula.DataProvider;
import com.anysoft.util.JsonSerializer;


/**
 * 服务调用参数
 * 
 * @author duanyy
 *
 * @since 1.2.9
 * 
 * @version 1.6.8.13 [duanyy 20170427] <br>
 * - 从alogic-remote中迁移过来 <br>
 */
public interface Parameters extends DataProvider{
	
	/**
	 * 设置单个服务参数
	 * 
	 * @param id 参数Id
	 * @param value 参数值
	 * @return 自身实例
	 */
	public Parameters param(String id,String value);
	
	/**
	 * 批量设置服务参数
	 * 
	 * @param idvalues 参数名值列表
	 * @return 自身实例
	 */
	public Parameters params(String...idvalues);
	
	/**
	 * 设置单个服务参数对象
	 * @param id
	 * @param value
	 * @return 自身实例
	 */
	public <data extends JsonSerializer> Parameters param(String id,data value);
	
	/**
	 * 设置单个服务参数对象
	 * @param id
	 * @param builder
	 * @return 自身实例
	 */
	public <data> Parameters param(String id,data value,Builder<data> builder);
	
	/**
	 * 清空当前所有的参数和对象
	 * @return 自身实例
	 */
	public Parameters clean();
	
	/**
	 * 获取指定的参数对象
	 * @param id 对象id
	 * @return 对象数据
	 */
	public Object getData(String id);
}
