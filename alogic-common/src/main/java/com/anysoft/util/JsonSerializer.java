package com.anysoft.util;

import java.util.Map;

/**
 * JSON序列化接口
 * 
 * @author duanyy
 * 
 * @since 1.0.6
 * 
 * @version [20140912 duanyy]<br>
 * - 将Map参数进行参数化
 * 
 */
public interface JsonSerializer {
	/**
	 * 输出到JSON对象
	 * @param json
	 */
	public void toJson(Map<String,Object> json);
	
	/**
	 * 从JSON对象读入
	 * @param json
	 */
	public void fromJson(Map<String,Object> json);
}
