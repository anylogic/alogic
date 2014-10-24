package com.anysoft.stream;

import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;

/**
 * 数据处理器
 * 
 * @author duanyy
 *
 * @param <data>
 * 
 * @since 1.4.0
 * 
 * @version 1.4.3 [20140903 duanyy]
 * - 增加pause,resume接口
 * 
 * @version 1.4.4 [20140917 duanyy]
 * - handle和flush方法增加timestamp参数，以便进行时间同步
 * 
 */
public interface Handler<data extends Flowable> extends XMLConfigurable,AutoCloseable,Reportable{
	
	/**
	 * 处理数据
	 * @param _data
	 * @param timestamp
	 */
	public void handle(data _data,long timestamp);
	
	/**
	 * 清理缓存
	 */
	public void flush(long timestamp);

	
	/**
	 * 获取Handler的类型
	 * @return
	 */
	public String getHandlerType();
	
	/**
	 * 暂停
	 * 
	 * @since 1.4.3
	 */
	public void pause();
	
	/**
	 * 恢复
	 * 
	 * @since 1.4.3
	 */
	public void resume();
	
	/**
	 * 获取ID
	 * @return
	 */
	public String getId();
}
