package com.alogic.poi.util;

import org.apache.poi.ss.usermodel.CellStyle;

import com.anysoft.util.Manager;

/**
 * 样式管理器
 * @author yyduan
 * @since 1.6.11.35
 */
public interface StyleManager{
	/**
	 * 增加Style
	 * @param id id
	 * @param style 实例
	 */
	public void addStyle(String id,CellStyle style);
	
	/**
	 * 根据id获取style
	 * @param id id
	 * @return style实例
	 */
	public CellStyle getStyle(String id);
	
	/**
	 * 清除所有的style
	 */
	public void clear();
	
	/**
	 * 缺省实现
	 * @author yyduan
	 *
	 */
	public static class Default extends Manager<CellStyle> implements StyleManager{

		@Override
		public void addStyle(String id, CellStyle style) {
			this.add(id, style);
		}

		@Override
		public CellStyle getStyle(String id) {
			return this.get(id);
		}
		
	}
}
