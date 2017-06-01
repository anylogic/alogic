package com.logicbus.dbcp.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


/**
 * 对象映射适配器
 * 
 * @author yyduan
 *
 * @param <T>
 * 
 * @since 1.6.9.2
 */
public interface ObjectMappingAdapter<T> {
	
	/**
	 * fetch到数据行
	 * @param rs 数据集
	 * @param result 结果数据存储
	 */
	public void rowFetched(ResultSet rs,List<T> result) throws SQLException;
}
