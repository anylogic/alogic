package com.anysoft.loadbalance;

import com.anysoft.util.Reportable;

/**
 * 负载接口
 * 
 * @author duanyy
 * 
 * @since 1.2.0
 * @version 1.5.3 [20141020 duanyy]
 * - 改造loadbalance模型
 */
public interface Load extends Reportable{
	/**
	 * 获取负载的标示ID
	 * @return
	 */
	public String getId();
	
	/**
	 * 获取本负载的权重
	 * @return
	 */
	public int getWeight();
	
	/**
	 * 获取本负载的优先级
	 * @return
	 */
	public int getPriority();
	
	/**
	 * 获取Counter
	 * 
	 * <p>create为true时，如果不存在，则创建一个；为false时，如果不存在则返回为空
	 * 
	 * @param create 是否创建
	 * @return
	 */
	public LoadCounter getCounter(boolean create);
	
	/**
	 * 进行使用计数
	 * 
	 * @param _duration 本次使用的时长
	 * @param error 是否有错误发生
	 */
	public void count(long _duration,boolean error);
	
	/**
	 * 是否有效
	 * @return
	 */
	public boolean isValid();
}
