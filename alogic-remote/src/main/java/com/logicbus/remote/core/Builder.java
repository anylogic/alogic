package com.logicbus.remote.core;

/**
 * 用于对指定的对象进行序列化和反序列化
 * 
 * @author duanyy
 *
 * @param <data>
 * 
 * @since 1.2.9
 */
public interface Builder<data> {

	/**
	 * 将指定的对象序列化为Json对象
	 * @param id
	 * @param o
	 * @return
	 */
	public Object serialize(String id,data o);

	/**
	 * 将指定的对象序列化为Json对象
	 * @param id
	 * @param json
	 * @return
	 */
	public data deserialize(String id,Object json);	
}
