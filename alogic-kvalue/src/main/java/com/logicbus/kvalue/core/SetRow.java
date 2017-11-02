package com.logicbus.kvalue.core;

import java.util.List;

/**
 * 基于Set的Row
 * 
 * @author duanyy
 *
 */
public interface SetRow extends KeyValueRow{
	
	/**
	 * 向Set中增加一个或多个元素
	 * 
	 * @param elements 新加入的元素列表
	 * @return 被添加到集合中的新元素的数量，不包括被忽略的元素。
	 */
	public long add(final String...elements);
	
	/**
	 * 获取所有元素
	 * @return 所有元素列表
	 */
	public List<String> getAll();
	
	/**
	 * 是否包含指定的元素
	 * @param element 指定的元素
	 * @return 是否包含
	 */
	public boolean contain(String element);
	
	/**
	 * 获取Set的大小
	 * @return Set的大小
	 */
	public long size();
	
	/**
	 * 获取一个随机的元素，并从Set中删除
	 * @return 被删除的元素
	 */
	public String pop();
	
	/**
	 * 从Set中删除一个或多个元素
	 * @param elements 待删除的元素列表
	 */
	public long remove(final String...elements);
	
	/**
	 * 获取一个随机的元素，并不从Set中删除
	 * @return 获取到的元素
	 */
	public String random();
	
	/**
	 * 计算给定Set(others等)差集，并返回为列表
	 * @param others
	 * @return 计算出的列表
	 */
	public List<String> diff(final String...others);
	
	/**
	 * 计算给定Set(others等)差集，并存储到目标Set(dstKey)中
	 * @param dstKey 目标Key
	 * @param others 其他的给定Set
	 * @return 结果集中元素个数
	 */
	public long diffStore(final String dstKey,final String...others);
	
	/**
	 * 计算给定Set(others等)交集，并返回为列表
	 * @param others 给定的Set集合
	 * @return 计算出的元素列表
	 */
	public List<String> inter(final String...others);
	
	/**
	 * 计算给定Set(others等)交集，并存储到目标Set(dstKey)中
	 * @param dstKey 目标集合的Key
	 * @param others 给定的Set集合
	 * @return 结果集的元素个数
	 */
	public long interStore(final String dstKey,final String...others);
	
	/**
	 * 计算给定Set(others等)并集，并返回为列表
	 * @param others 给定的Set集合
	 * @return 计算出的元素列表
	 */
	public List<String> union(final String...others);
	
	/**
	 * 计算给定Set(others等)并集，并存储到目标Set(dstKey)中
	 */
	public long unionStore(final String dstKey,final String subkey,final String...others);
}
