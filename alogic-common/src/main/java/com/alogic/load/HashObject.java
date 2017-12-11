package com.alogic.load;

import java.util.List;
import com.anysoft.util.Pair;

/**
 * HashObject
 * <p>
 *
 * HashObject用于将Hash对象封装成Loadable对象.
 * 
 * @author duanyy
 * 
 * @since 1.6.10.10
 *
 */
public interface HashObject extends Loadable{

	/**
	 * 设置指定Entry的值
	 * 
	 * <p>
	 * 用来设置Hash中指定key的Entry的值,如果该Entry不存在，则直接设置；如果该Entry已经存在，如果overwrite
	 * 为true，则覆盖；反之，则放弃。
	 * 
	 * @param group 分组
	 * @param key Entry key
	 * @param value 待设置的值
	 * @param overwrite 是否覆盖
	 */
	public void hSet(String group,String key,String value,boolean overwrite);
	
	/**
	 * 获取指定Entry的值
	 * <p>
	 * 用来获取指定Entry的值，如果该Entry存在，则返回Entry值，反之返回dftValue.
	 * 
	 * @param group 分组
	 * @param key
	 * @param dftValue
	 * @return 如果该Entry存在，则返回Entry值，反之返回dftValue.
	 */
	public String hGet(String group,String key,String dftValue);
	
	/**
	 * 是否存在指定key的Entry
	 * 
	 * @param group 分组
	 * @param key Entry key
	 * @return 当Entry存在时，返回为true,反之为false
	 */
	public boolean hExist(String group,String key);
	
	/**
	 * 获取符合指定查询条件的Entry集合
	 * 
	 * <p>
	 * 用于获取符合指定查询条件的Entry集合,参数condition是一个字符串匹配模板,支持简单的通配符*匹配.
	 * 当condition为空字符串时，返回所有的Entry。
	 * 
	 * @param group 分组
	 * @param condition 查询条件，字符串的模板
	 * 
	 * @return List形态的Entry集合
	 */
	public List<Pair<String,String>> hGetAll(String group,String condition);
	
	/**
	 * 获取Entry的个数
	 * @param group 分组
	 * @return Entry的个数
	 */
	public int hLen(String group);

	/**
	 * 获取符合指定查询条件的Key列表
	 * 
	 * <p>
	 * 用于获取符合指定查询条件的Key列表,参数condition是一个字符串匹配模板,支持简单的通配符*匹配.
	 * 当condition为空字符串时，返回所有的Key。
	 * @param group 分组
	 * @param condition 查询条件，字符串的模板	 
	 * @return 符合条件的Key列表
	 */
	public List<String> hKeys(String group,String condition);
	
	/**
	 * 删除指定的key
	 * @param group 分组
	 * @param key 待删除的key
	 */
	public void hDel(String group,String key);
	
	/**
	 * 删除整个Group
	 * @param group 分组
	 */
	public void hDel(String group);
}
