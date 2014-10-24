package com.anysoft.util;

/**
 * 数据变动监控器
 * 
 * @author duanyy
 *
 * @param <data>
 */
public interface Watcher<data> {
	
	/**
	 * 增加了对象
	 * @param id 对象ID
	 * @param _data 对象实例
	 */
	public void added(String id,data _data);
	
	/**
	 * 删除了对象
	 * @param id
	 */
	public void removed(String id,data _data);
	
	/**
	 * 变更了对象
	 * @param id
	 * @param _data
	 */
	public void changed(String id,data _data);
}
