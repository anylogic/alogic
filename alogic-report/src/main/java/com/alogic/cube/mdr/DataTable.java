package com.alogic.cube.mdr;

import java.util.ArrayList;
import java.util.List;

import com.anysoft.util.Properties;

/**
 * 数据表
 * @author yyduan
 * @since 1.6.11.35
 */
public class DataTable  implements Summarable{
	/**
	 * 行的维度定义
	 */
	protected Dimension row = null;
	
	/**
	 * 列的维度定义
	 */
	protected Dimension col = null;	
	
	/**
	 * 量度的定义
	 */
	protected List<Measure> measures = new ArrayList<Measure>();
	
	/**
	 * 数据行组
	 */
	protected DataRowGroup rootRow = null;
	
	public DataTable(){
		
	}
	
	/**
	 * 设置数据行的维度定义
	 * @param dim 维度
	 */
	public void setRowDimension(Dimension dim){
		this.row = dim;
	}
	
	/**
	 * 增加一个维度
	 * @param dim 维度
	 */
	public void addRowDimension(Dimension dim){
		if (this.row != null){
			this.row.append(dim);
		}else{
			this.row = dim;
		}
	}
	
	/**
	 * 设置数据列的维度定义
	 * @param dim 维度
	 */
	public void setColDimension(Dimension dim){
		this.col = dim;
	}
	
	/**
	 * 增加一个维度
	 * @param dim 维度
	 */
	public void addColDimension(Dimension dim){
		if (this.col != null){
			this.col.append(dim);
		}else{
			this.col =dim;
		}
	}	
	
	/**
	 * 增加一个量度
	 * @param measure 量度
	 */
	public void addMeasure(Measure measure){
		if (measure != null){
			this.measures.add(measure);
		}
	}
	
	@Override
	public void sum(final Properties provider){
		if (rootRow == null){
			rootRow = new DataRowGroup(row.getId(),row.getId(),row,col,measures);
		}
		rootRow.sum(provider);
	}
	
	public void report(DataReport report){
		String [] columns = rootRow.getColumnList();		
		report.onHeader(rootRow.setHeaderColumns(columns));				
		rootRow.report(report);
	}
}
