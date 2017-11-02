package com.alogic.remote.call;

/**
 * 用于对指定的对象进行序列化和反序列化
 * 
 * @author duanyy
 *
 * @param <data>
 * 
 * @since 1.2.9
 * 
 * @version 1.6.8.13 [duanyy 20170427] <br>
 * - 从alogic-remote中迁移过来 <br>
 */
public interface Builder<data> {

	/**
	 * 将指定的对象序列化为Json对象
	 * @param id
	 * @param o
	 * @return Json对象
	 */
	public Object serialize(String id,data o);

	/**
	 * 将Json对象反序列化为对象
	 * @param id
	 * @param json
	 * @return 对象
	 */
	public data deserialize(String id,Object json);	
}
