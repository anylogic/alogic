package com.anysoft.util;


/**
 * 确认者
 * 
 * <br>
 * 用于在使用配置信息之前的确认。
 * 
 * @author duanyy
 *
 * @since 1.0.10
 */
public interface Confirmer {
	
	/**
	 * 通知Confirmer准备数据
	 * @param id 数据ID
	 */
	public void prepare(String id);
	
	/**
	 * 进行数据确认
	 * @param field 数据域ID
	 * @param value 数据值
	 * @return 确认之后的数据
	 */
	public String confirm(String field,String value);
}
