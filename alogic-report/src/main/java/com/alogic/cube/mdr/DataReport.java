package com.alogic.cube.mdr;

/**
 * 数据报告
 * @author yyduan
 * @since 1.6.11.35
 */
public interface DataReport {
	/**
	 * 输出表头
	 * @param headRow 表头行数据
	 */
	public void onHeader(DimensionRow headRow);

	/**
	 * 输出汇总行
	 * @param totalRow 汇总行数据
	 */
	public void onTotal(DimensionRow totalRow);

	public void onRowStart(String dimId,String id);
	
	public void onRowEnd(String dimId,String id);
	
	/**
	 * 输出数据行
	 * @param columns 列id列表
	 * @param dataRow 行数据
	 */
	public void onRow(DimensionRow dataRow);
}
