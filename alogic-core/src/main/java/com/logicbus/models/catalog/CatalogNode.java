package com.logicbus.models.catalog;

import com.anysoft.util.XmlSerializer;

/**
 * 目录节点
 * @author hmyyduan
 *
 */
public interface CatalogNode extends XmlSerializer{
	
	/**
	 * 获取名称
	 * @return
	 */
	public String getName();
	
	/**
	 * 获取实现模块
	 * @return
	 */
	public String getModule();
	
	/**
	 * 获取路径
	 * @return
	 */
	public Path getPath();
	
	/**
	 * 获取附加数据
	 * @return
	 */
	public Object getData();
}
