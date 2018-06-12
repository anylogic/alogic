package com.alogic.cube.mdr;

/**
 * 维度Row
 * @author yyduan
 * @since 1.6.11.35
 */
public interface DimensionRow extends Summarable{
	public String getId();	
	public String getDimId();
	public void report(DataReport report);
	public Dimension getColDimension();
	public String[] getHeaderColumns();
	public DimensionRow setHeaderColumns(String[] columns);
}
