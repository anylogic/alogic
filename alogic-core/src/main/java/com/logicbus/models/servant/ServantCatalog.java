package com.logicbus.models.servant;

import com.logicbus.models.catalog.CatalogModel;
import com.logicbus.models.catalog.Path;

/**
 * Servant目录
 * @author hmyyduan
 *
 */
public interface ServantCatalog extends CatalogModel {
	/**
	 * 在目录中查找指定ID的服务
	 * @param id 服务Id
	 * @return 服务描述信息
	 */
	public ServiceDescription findService(Path id);
	
	public void addWatcher(ServiceDescriptionWatcher watcher);
}
