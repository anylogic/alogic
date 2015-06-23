package com.logicbus.models.catalog;

import com.anysoft.util.JsonSerializer;
import com.anysoft.util.XmlSerializer;

/**
 * 目录节点
 * @author hmyyduan
 * 
 * @version 1.6.3.27 [20150623 duanyy] <br>
 * - 增加JSON的序列化支持
 */
public interface CatalogNode extends XmlSerializer,JsonSerializer{
	
	/**
	 * 获取名称
	 * @return 名称
	 */
	public String getName();
	
	/**
	 * 获取实现模块
	 * @return 实现模块
	 */
	public String getModule();
	
	/**
	 * 获取路径
	 * @return 路径
	 */
	public Path getPath();
	
	/**
	 * 获取附加数据
	 * @return 附加数据
	 */
	public Object getData();
}
