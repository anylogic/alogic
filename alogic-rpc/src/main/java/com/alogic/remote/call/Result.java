package com.alogic.remote.call;

import com.anysoft.util.JsonSerializer;

/**
 * 服务调用结果
 * 
 * @author duanyy
 *
 * @since 1.2.9
 * 
 * @version 1.2.9.3 [20141021 duanyy]
 * - 可通过JsonPath来获取对象
 * 
 * @version 1.6.8.13 [duanyy 20170427] <br>
 * - 从alogic-remote中迁移过来 <br>
 */
public interface Result {
	/**
	 * 获取服务的主机
	 * 
	 * @return
	 */
	public String getHost();
	
	/**
	 * 获取服务结果代码
	 * @return 结果代码
	 */
	public String getCode();
	
	/**
	 * 获取错误原因
	 * @return 错误原因
	 */
	public String getReason();
	
	/**
	 * 获取本次服务调用的全局序列号
	 * @return 全局序列号
	 */
	public String getGlobalSerial();
	
	/**
	 * 获取服务端的服务时长
	 * @return 服务时长
	 */
	public long getDuration();
	
	/**
	 * 获取结果对象
	 * @param id
	 * @param clazz
	 * @return 结果对象
	 */
	public <data extends JsonSerializer> data getData(String id,Class<data> clazz);
	
	/**
	 * 获取结果对象
	 * @param id
	 * @param instance
	 * @return 结果对象
	 */
	public <data extends JsonSerializer> data getData(String id,data instance);
	
	/**
	 * 获取结果对象
	 * @param id
	 * @param builder
	 * @return 结果对象
	 */
	public <data> data getData(String id,Builder<data> builder);	
	
	/**
	 * 获取指定的对象
	 * @param id 对象id
	 * @return
	 */
	public <data> data getData(String id);
}
