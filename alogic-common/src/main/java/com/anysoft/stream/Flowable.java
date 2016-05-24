package com.anysoft.stream;

import com.anysoft.formula.DataProvider;

/**
 * 可流式处理
 * 
 * @author sony
 * @since 1.4.0
 * 
 * @version 1.6.5.6 [20160523 duanyy] <br>
 * - 增加id()接口
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
}
