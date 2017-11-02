package com.logicbus.kvalue.core;

import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;

/**
 * KVDB数据表
 * 
 * @author duanyy
 *
 */
public interface Table extends XMLConfigurable,Reportable{
	
	public static enum DataType {
		String,
		Bit,
		Integer,
		Float,
		ByteArray,
		Hash,
		List,
		Set,
		SortedSet;
		
		public static DataType from(final String value){
			try {
				return valueOf(value);
			}catch (Exception ex){
				//缺省为String
				return String;
			}
		}
	};
	
	/**
	 * 获取表名
	 * @return 表名
	 */
	public String getName();
	
	/**
	 * 选择指定的行
	 * @param key 行的key
	 * @param enableRWSplit 是否允许读写分离
	 * @return 行的实例
	 */
	public KeyValueRow select(String key,boolean enableRWSplit);
	
}
