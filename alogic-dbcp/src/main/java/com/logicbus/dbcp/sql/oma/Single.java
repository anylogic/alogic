package com.logicbus.dbcp.sql.oma;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.logicbus.dbcp.sql.ObjectMappingAdapter;

/**
 * 单字符串模式的ORM工具
 * 
 * @author yyduan
 * 
 * @since 1.6.9.2
 */
public class Single implements ObjectMappingAdapter<String>{

	@Override
	public void rowFetched(ResultSet rs, List<String> result) throws SQLException{
		String value = rs.getString(1);
		result.add(value);
	}

}
