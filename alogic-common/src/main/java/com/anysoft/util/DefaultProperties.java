package com.anysoft.util;

import java.io.PrintStream;
import java.util.*;

/**
 * 缺省的变量集实现
 * <p>本类基于{@link java.util.Hashtable}实现.</p>
 * @author hmyyduan
 *
 */
public class DefaultProperties extends Properties {
	/**
	 * 变量集内容
	 */
	protected Hashtable<String, String> content = new Hashtable<String, String>();
	
	/**
	 * 构造函数
	 * @param _domain 变量域
	 * @param _parent 父节点
	 * @see Properties#Properties(String, Properties)
	 */
	public DefaultProperties(String _domain,Properties _parent){
		super(_domain,_parent);
	}
	
	/**
	 * 构造函数
	 * @param _domain 变量域
	 * @see Properties#Properties(String)
	 */
	public DefaultProperties(String _domain){
		super(_domain);
	}
	
	/**
	 * 构造函数
	 * @see Properties#Properties()
	 */
	public DefaultProperties(){
	}
	
	/**
	 * 获取变量集的内容
	 * @return 内容
	 */
	public Hashtable<String, String> getContent(){
		return content;
	}
	
	/**
	 * 向变量集中写入变量
	 * @param _name 变量名
	 * @param _value 变量值
	 */
	protected void _SetValue(String _name, String _value) {
		if (_value == null || _value.length() <= 0){
			content.remove(_name);
		}
		content.put(_name,_value);
	}

	/**
	 * 从变量集中提取出变量值
	 * @param _name 变量名
	 */
	protected String _GetValue(String _name) {
		String __value = (String)content.get(_name);
		if (__value == null){
			__value = "";
		}
		return __value;
	}
	
	/**
	 * 获取变量集中所有的变量名列表
	 * @return 变量名列表
	 */
	public Enumeration<String> keys(){return content.keys();} 
	
	/**
	 * 打印出变量集中的内容
	 * @param out 输出打印流
	 */
	public void list(PrintStream out){
		Enumeration<?> __keys = keys();
		while (__keys.hasMoreElements()){
			String __name = (String)__keys.nextElement();
			String __value = _GetValue(__name);
			out.print(__name);
			out.print("=");
			out.println(__value);
		}
	}
	
	/**
	 * 清除变量集内容
	 */
	public void Clear() {
		content.clear();		
	}
	
	/**
	 * 从另一实例中复制内容
	 * @param other 另一实例
	 */
	public void copyFrom(DefaultProperties other){
		Enumeration<?> __keys = other.keys();
		Clear();
		while (__keys.hasMoreElements()){
			String __name = (String)__keys.nextElement();
			String __value = other._GetValue(__name);
			SetValue(__name, __value);
		}
	}
}
