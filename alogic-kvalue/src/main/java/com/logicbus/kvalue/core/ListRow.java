package com.logicbus.kvalue.core;

import java.util.List;

/**
 * 基于List类型 的Row
 * @author duanyy
 *
 */
public interface ListRow extends KeyValueRow {
	
	/**
	 * 在指定元素前（或后）插入新的元素
	 * 
	 * @param pivot 用于定位的指定元素
	 * @param value 新元素
	 * @param insertAfter 如果为true，在后面插入，否则，在前面插入
	 * @return 如果执行成功，返回List长度，如果pivot没有找到，返回-1
	 */
	public long insert(final String pivot,final String value,boolean insertAfter);
	
	/**
	 * 获取List的长度
	 * @return List的长度
	 */
	public long length();
	
	/**
	 * 获取指定位置的元素
	 * @param index 位置
	 * @param dftValue 缺省值 
	 * @return
	 */
	public String get(final long index,final String dftValue);
	
	/**
	 * 在指定的位置设置元素
	 * @param index
	 * @param value
	 * @return
	 */
	public boolean set(final long index,final String value);
	
	/**
	 * 移除并返回头元素
	 * <br>
	 * 当List不为空的时候，返回相应的元素；当List为空时，如果block为false,则立刻返回空，如果
	 * block为true,线程阻塞直到其他线程向List增加元素。
	 * 
	 * @param block
	 * @return
	 */
	public String leftPop(boolean block);
	
	
	/**
	 * 移除并返回尾元素
	 * <br>
	 * 当List不为空的时候，返回相应的元素；当List为空时，如果block为false,则立刻返回空，如果
	 * block为true,线程阻塞直到其他线程向List增加元素。
	 * 
	 * @param block
	 * @return
	 */
	public String rightPop(boolean block);
	
	/**
	 * 依次向列表头插入一个或多个新元素
	 * @param values
	 * @return 插入后的列表长度
	 */
	public long leftPush(final String...values);
	
	/**
	 * 依次向列表尾插入一个或多个新元素
	 * @param values
	 * @return
	 */
	public long rightPush(final String...values);
	
	/**
	 * 获取List的一个区域（从start到stop）
	 * @param start
	 * @param stop
	 * @return
	 */
	public List<String> range(final long start,final long stop);
	
	/**
	 * 根据参数 count 的值，移除列表中与参数 value 相等的元素
	 * 
	 * <br>
	 * count 的值可以是以下几种：<br>
	 * - count > 0 : 从表头开始向表尾搜索，移除与 value 相等的元素，数量为 count <br>
	 * - count < 0 : 从表尾开始向表头搜索，移除与 value 相等的元素，数量为 count 的绝对值 <br>
	 * - count = 0 : 移除表中所有与 value 相等的值
	 * 
	 * @param value
	 * @param count
	 * @return
	 */
	public long remove(final String value,final long count);
	
	/**
	 * 裁剪不在指定区域的元素
	 * 
	 * @param start
	 * @param stop
	 * @return
	 */
	public long trim(final long start,final long stop);
}
