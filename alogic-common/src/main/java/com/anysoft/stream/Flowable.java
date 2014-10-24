package com.anysoft.stream;

import com.anysoft.formula.DataProvider;

/**
 * 可流式处理
 * 
 * @author sony
 * @since 1.4.0
 */
public interface Flowable extends DataProvider {
	/**
	 * 获取数据对象的统计维度
	 * @param _data
	 * @return
	 */	
	public String getStatsDimesion();
}
