package com.logicbus.dbcp.sql.oma;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.anysoft.util.Pair;
import com.logicbus.dbcp.sql.ObjectMappingAdapter;

/**
 * KeyValue模式的ORM工具
 * 
 * @author yyduan
 * 
 * @since 1.6.9.2
 */
public class KeyValue implements ObjectMappingAdapter<Pair<String,String>>{

	@Override
	public void rowFetched(ResultSet rs, List<Pair<String,String>> result) throws SQLException{
		Pair<String,String> pair = new Pair.Default<String, String>(rs.getString(1), rs.getString(2));
		result.add(pair);
	}

}
