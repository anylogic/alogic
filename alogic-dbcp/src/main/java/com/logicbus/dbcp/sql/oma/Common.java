package com.logicbus.dbcp.sql.oma;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

import com.logicbus.dbcp.sql.ObjectMappingAdapter;

/**
 * 通用模式的ORM工具
 * 
 * @author yyduan
 * 
 * @since 1.6.9.2
 */
public abstract class Common<T> implements ObjectMappingAdapter<T>{

	/**
	 * 创建新的对象
	 * @return 新的对象实例
	 */
	public abstract T newObject();
	
	@Override
	public void rowFetched(ResultSet rs, List<T> result) throws SQLException {
		T t = newObject();
		
		ResultSetMetaData metadata = rs.getMetaData();
		for (int i = 0 ;i < metadata.getColumnCount() ; i ++){
			Object value = rs.getObject(i+1);
			if (value == null){
				continue;
			}
			
			String columnId = getColumnId(metadata,i + 1);
			if (columnId == null){
				continue;
			}
			try {
				Field field = t.getClass().getField(columnId);
				if (field != null){
					field.set(t, value);
				}
			} catch (Exception ex) {

			}
		}
		
		result.add(t);
	}
	
	public String getColumnId(ResultSetMetaData metadata, int index) {
		try {
			String name = metadata.getColumnLabel(index);
			if (name == null){
				name = metadata.getColumnName(index);
			}
			return name;
		}catch (Exception ex){
			return null;
		}
	}

}
