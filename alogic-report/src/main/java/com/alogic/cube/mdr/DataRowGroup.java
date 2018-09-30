package com.alogic.cube.mdr;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.anysoft.util.Properties;

/**
 * 行组
 * @author duanyy
 * @since 1.6.11.35
 */
public class DataRowGroup implements DimensionRow{
	/**
	 * 行维度
	 */
	protected Dimension row = null;
	
	/**
	 * 列维度
	 */
	protected Dimension col = null;
	
	/**
	 * 量度
	 */
	protected List<Measure> measures = null;
	
	/**
	 * 数据行列表
	 */
	protected Map<String,DimensionRow> rows = new HashMap<String,DimensionRow>();
	
	/**
	 * 汇总数据行
	 */
	protected DataRow total = null;
	
	protected String [] headerColumns;

	/**
	 * 行id
	 */
	protected String id;
	
	/**
	 * 维度id
	 */
	protected String dimId;	
	
	public String getId(){
		return this.id;
	}
	
	public String getDimId(){
		return this.dimId;
	}	
	
	public Dimension getColDimension(){
		return this.col;
	}	
	
	public DimensionRow setHeaderColumns(String[] columns){
		this.headerColumns = columns;
		return this;
	}
	
	public String[] getHeaderColumns(){
		return this.headerColumns;
	}	
	
	/**
	 * 获取列的id列表
	 * @return id列表
	 */
	public String[] getColumnList(){
		return total.getColumnList();
	}	
	
	protected DataRowGroup(final String id,final String dimId,final Dimension row,final Dimension col,final List<Measure> measures){
		this.row = row;
		this.id = id;
		this.dimId = dimId;
		this.col = col;
		this.measures = measures;
	}
	
	@Override
	public void sum(final Properties provider){
		String id = row.getValue(provider);
		if (StringUtils.isNotEmpty(id)){
			DimensionRow found = rows.get(id);
			if (found == null){
				Dimension rowDim = row.next();
				if (rowDim != null){
					found = new DataRowGroup(id,row.getId(),rowDim,col,measures);
				}else{
					found = new DataRow(id,row.getId(),col,measures);
				}
				rows.put(id, found);
			}
			found.sum(provider);
			
			if (total == null){
				total = new DataRow(row.getId(),row.getId(),col,measures);
			}
			total.sum(provider);
		}
	}

	public void report(DataReport report) {
		if (report != null){
			if (this.headerColumns == null){
				this.headerColumns = this.getColumnList();
			}			
			String[] columns = this.getHeaderColumns();

			report.onRowStart(dimId, id);
			Iterator<DimensionRow> iter = rows.values().iterator();			
			while (iter.hasNext()){
				DimensionRow row = iter.next();
				row.setHeaderColumns(columns).report(report);
			}
			report.onTotal(total.setHeaderColumns(columns));	
			report.onRowEnd(dimId, id);
		}
	}	
}
