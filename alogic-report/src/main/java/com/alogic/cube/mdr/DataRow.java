package com.alogic.cube.mdr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.anysoft.util.Properties;

/**
 * 数据行
 * @author duanyy
 * @since 1.6.11.35
 */
public class DataRow  implements DimensionRow{
	/**
	 * 列维度
	 */
	protected Dimension dim = null;
	
	/**
	 * 量度列表
	 */
	protected List<Measure> measures = null;
	
	/**
	 * 列数据
	 */
	protected Map<String,DataCell> columns = new HashMap<String,DataCell>();
	
	/**
	 * 合计数据
	 */
	protected DataCell total = null;
	
	/**
	 * 行id
	 */
	protected String id;
	
	/**
	 * 维度id
	 */
	protected String dimId;
	
	protected String [] headerColumns;
	
	public DimensionRow setHeaderColumns(String[] columns){
		this.headerColumns = columns;
		return this;
	}
	
	public String[] getHeaderColumns(){
		return this.headerColumns;
	}
	
	protected DataRow(String id,String dimId,final Dimension dim,final List<Measure> measures){
		this.id = id;
		this.dimId = dimId;
		this.dim = dim;
		this.measures = measures;
	}
	
	public String getId(){
		return this.id;
	}
	
	public String getDimId(){
		return this.dimId;
	}
	
	public Dimension getColDimension(){
		return this.dim;
	}
	
	public List<Measure> getMeasures(){
		return this.measures;
	}
	
	/**
	 * 获取列的id列表
	 * @return
	 */
	public String[] getColumnList(){
		return columns.keySet().toArray(new String[0]);
	}

	@Override
	public void sum(final Properties provider){
		String id = dim.getValue(provider);
		if (StringUtils.isNotEmpty(id)){
			DataCell found = columns.get(id);
			if (found == null){
				found = new DataCell(measures);
				columns.put(id, found);
			}
			found.sum(provider);
			
			if (total == null){
				total = new DataCell(measures);
			}
			total.sum(provider);
		}
	}

	/**
	 * 根据列id获取数据格
	 * @param columnId 列id
	 * @return 数据格
	 */
	public DataCell getCell(String columnId){
		return columns.get(columnId);
	}
	
	/**
	 * 获取合计数据格
	 * @return 数据格
	 */
	public DataCell getTotal(){
		return total;
	}
	
	public void report(DataReport report) {
		if (this.headerColumns == null){
			this.headerColumns = this.getColumnList();
		}
		report.onRow(this);
	}	
}
