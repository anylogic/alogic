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
	 * @param elements
	 * @return
	 */
	public long add(final String...elements);
	
	/**
	 * 获取所有元素
	 * @return
	 */
	public List<String> getAll();
	
	/**
	 * 是否包含指定的元素
	 * @param element
	 * @return
	 */
	public boolean contain(String element);
	
	/**
	 * 获取Set的大小
	 * @return
	 */
	public long size();
	
	/**
	 * 获取一个随机的元素，并从Set中删除
	 * @return
	 */
	public String pop();
	
	/**
	 * 从Set中删除一个或多个元素
	 * @param elements
	 */
	public long remove(final String...elements);
	
	/**
	 * 获取一个随机的元素，并不从Set中删除
	 * @return
	 */
	public String random();
	
	/**
	 * 计算给定Set(others等)差集，并返回为列表
	 * @param others
	 * @return
	 */
	public List<String> diff(final String...others);
	
	/**
	 * 计算给定Set(others等)差集，并存储到目标Set(dstKey)中
	 * @param subkey
	 * @param others
	 * @return
	 */
	public long diffStore(final String dstKey,final String...others);
	
	/**
	 * 计算给定Set(others等)交集，并返回为列表
	 * @param subkey
	 * @param others
	 * @return
	 */
	public List<String> inter(final String...others);
	
	/**
	 * 计算给定Set(others等)交集，并存储到目标Set(dstKey)中
	 * @param subkey
	 * @param others
	 * @return
	 */
	public long interStore(final String dstKey,final String...others);
	
	/**
	 * 计算给定Set(others等)并集，并返回为列表
	 * @param subkey
	 * @param others
	 * @return
	 */
	public List<String> union(final String...others);
	
	/**
	 * 计算给定Set(others等)并集，并存储到目标Set(dstKey)中
	 * @param subkey
	 * @param others
	 * @return
	 */
	public long unionStore(final String dstKey,final String subkey,final String...others);
}
