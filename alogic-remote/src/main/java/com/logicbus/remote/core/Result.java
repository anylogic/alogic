package com.logicbus.remote.core;

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
	 * @return
	 */
	public String getCode();
	
	/**
	 * 获取错误原因
	 * @return
	 */
	public String getReason();
	
	/**
	 * 获取本次服务调用的全局序列号
	 * @return
	 */
	public String getGlobalSerial();
	
	/**
	 * 获取服务端的服务时长
	 * @return
	 */
	public long getDuration();
	
	/**
	 * 获取结果对象
	 * @param id
	 * @param clazz
	 * @return
	 */
	public <data extends JsonSerializer> data getData(String id,Class<data> clazz);
	
	/**
	 * 获取结果对象
	 * @param id
	 * @param instance
	 * @return
	 */
	public <data extends JsonSerializer> data getData(String id,data instance);
	
	/**
	 * 获取结果对象
	 * @param id
	 * @param builder
	 * @return
	 */
	public <data> data getData(String id,Builder<data> builder);	
}
