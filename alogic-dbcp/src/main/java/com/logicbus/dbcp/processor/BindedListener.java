package com.logicbus.dbcp.processor;

/**
 * SQL绑定变量监听器
 * 
 * @author duanyy
 * @since 1.6.3.30
 */
public interface BindedListener {
	
	/**
	 * 绑定指定的值
	 * 
	 * @param value 绑定值
	 */
	public void bind(Object value);
}
