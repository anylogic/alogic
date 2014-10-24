package com.logicbus.dbcp.sql;

/**
 * 数据记录监听器
 * 
 * @author duanyy
 * @since 1.2.5
 * 
 */
public interface RowListener {
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
	 * @param name 数据行名称(SQL语句中指定，小写)
	 * @param value 数据对象
	 */
	public void columnFound(Object cookies,int columnIndex,String name,Object value);
	
	/**
	 * 数据行结束
	 * @param cookies 数据行记录
	 */
	public void rowEnd(Object cookies);
}
