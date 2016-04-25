package com.logicbus.models.servant;

import com.anysoft.util.Reportable;
import com.logicbus.models.catalog.CatalogNode;

/**
 * 服务目录节点
 * 
 * @author duanyy
 * @version 1.6.4.46 [20160425 duanyy] <br>
 * - 实现Reportable接口 <br>
 */
public interface ServantCatalogNode extends CatalogNode,Reportable {
	
	/**
	 * 获取当前节点下所有服务信息
	 * @return 所有服务信息
	 */
	public ServiceDescription [] getServices();
	
	/**
	 * 按服务ID查找服务信息
	 * @param id
	 * @return 服务信息
	 */
	public ServiceDescription findService(String id);
}
