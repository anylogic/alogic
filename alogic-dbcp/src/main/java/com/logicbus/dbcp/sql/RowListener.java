package com.logicbus.dbcp.sql;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 数据记录监听器
 * 
 * @author duanyy
 * @since 1.2.5
 * 
 * @version 1.6.3.3 [20150227 duanyy] <br>
 * - 增加{@link #result()}方法，以便获取结果<br>
 */
public interface RowListener<T> {
	/**
	 * 数据行开始
	 * @param column 列数
	 * @return 数据行对象(由具体实现去定)
	 */
	public Object rowStart(int column);
	
	/**
	 * 发现数据列
	 * @param cookies 数据行对象
	 * @param columnIndex 列索引(以0开始)
	 * @param name 本次查询的元数据
	 * @param value 数据对象
	 */
	public void columnFound(Object cookies,int columnIndex,ResultSetMetaData metadata,T value);
	
	/**
	 * 数据行结束
	 * @param cookies 数据行记录
	 */
	public void rowEnd(Object cookies);
	
	/**
	 * 获取结果数据
	 * @return 结果数据
	 * @since 1.6.3.3
	 */
	public Object result();
	
	/**
	 * 内置的行数据监听器
	 * 
	 * @author duanyy
	 * @since 1.6.2.4
	 */
	public static class Default<T> implements RowListener<T>{
		public Default(){
			renderer = new RowRenderer.Default<T>();
		}
		
		public Default(RowRenderer<T> _renderer){
			renderer  = _renderer;
			if (renderer == null){
				renderer = new RowRenderer.Default<T>();
			}
		}
		
		protected RowRenderer<T> renderer = null;
		
		protected ArrayList<Map<String,T>> result = new ArrayList<Map<String,T>>();

		public List<Map<String,T>> getResult(){
			return result;
		}
		
		/**
		 * @since 1.6.3.3
		 */
		public Object result(){
			return result;
		}

		public Object rowStart(int column) {
			return renderer.newRow(column);
		}
		
		public void columnFound(Object cookies,int columnIndex, ResultSetMetaData metadata, T value) {
			if (value != null){
				@SuppressWarnings({ "unchecked", "rawtypes" })
				Map<String, T> map = (Map)cookies;
				String id = renderer.getColumnId(metadata, columnIndex);
				if (id != null)
					map.put(id, value);
			}
		}
		
		public void rowEnd(Object cookies) {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			Map<String, T> map = (Map)cookies;
			Map<String, T> row = renderer.render(map);
			if (row != null){
				result.add(row);
			}
		}
		
	}
}
