package com.anysoft.stream;

import com.anysoft.formula.DataProvider;

/**
 * 可流式处理
 * 
 * @author sony
 * @since 1.4.0
 * 
 * @version 1.6.5.6 [20160523 duanyy] <br>
 * - 增加id()接口 <br>
 * 
 * @version 1.6.11.3 [20171219 duanyy] <br>
 * - 增加isAsync方法，用来标记数据是否允许异步处理 <br>
 * 
 */
public interface Flowable extends DataProvider {
	/**
	 * 获取数据对象的统计维度
	 * @return dimensions
	 */	
	public String getStatsDimesion();
	
	/**
	 * 获取id
	 * @return id
	 * 
	 * @since 1.6.5.6
	 */
	public String id();
	
	/**
	 * 是否可以异步处理
	 * @return 是否可以异步处理
	 */
	public boolean isAsync();
}
