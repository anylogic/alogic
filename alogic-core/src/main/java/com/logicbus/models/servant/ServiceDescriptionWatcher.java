package com.logicbus.models.servant;

import com.logicbus.models.catalog.Path;


/**
 * 服务描述信息监听器
 *  
 * @author duanyy
 *
 */
public interface ServiceDescriptionWatcher {
	
	/**
	 * 当服务描述有变动的时候触发
	 * @param id id
	 * @param newDesc 新的描述信息
	 */
	public void changed(Path id,ServiceDescription desc);
	
	/**
	 * 服务被删除
	 * @param id 服务ID
	 */
	public void removed(Path id);
}
