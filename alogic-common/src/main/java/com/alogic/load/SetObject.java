package com.alogic.load;

import java.util.List;

import com.anysoft.formula.DataProvider;

/**
 * SetObject
 * <p>
 *
 * SetObject用于将Set对象封装成Loadable对象.
 * 
 * @author duanyy
 *
 * @since 1.6.10.10
 */
public interface SetObject extends Loadable,DataProvider{

	/**
	 * 向Set中增加member
	 * @param group 分组
	 * @param members Set成员列表
	 */
	public void sAdd(String group,String...members);
	
	/**
	 * 从Set中删除member
	 * @param group 分组
	 * @param members 待删除的成员列表
	 */
	public void sDel(String group,String...members);
	
	/**
	 * 清除整个group
	 * @param group 分组
	 */
	public void sDel(String group);
	
	/**
	 * 当前Set大小
	 * @param group 分组
	 * @return 返回当前Set大小
	 */
	public int sSize(String group);
	
	/**
	 * 获取符合指定查询条件的member列表
	 * 
	 * <p>
	 * 用于获取符合指定查询条件的member列表,参数condition是一个字符串匹配模板,支持简单的通配符*匹配.
	 * 当condition为空字符串时，返回所有的member。
	 * 
	 * @param group 分组
	 * @param condition 查询条件，字符串的模板	 
	 * @return 符合条件的member列表
	 */
	public List<String> sMembers(String group,String condition);
	
	/**
	 * 当前Set是否存在指定的成员
	 * @param group 分组
	 * @param member 指定成员
	 * @return 如果该member存在，返回为true,反之为false
	 */
	public boolean sExist(String group,String member);
}
