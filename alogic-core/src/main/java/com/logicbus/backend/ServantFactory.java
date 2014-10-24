package com.logicbus.backend;

import com.anysoft.util.Factory;
import com.logicbus.models.catalog.Path;
import com.logicbus.models.servant.ServiceDescriptionWatcher;


/**
 * 服务员工厂
 * @author duanyy
 * @version 1.2.0 [20140607 duanyy]修正无法reload的bug
 * @version 1.2.2 [20140617 duanyy]
 * - 改进同步模型
 * 
 * @version 1.2.6 [20140807 duanyy]
 * - 修改为interface
 */
public interface ServantFactory extends ServiceDescriptionWatcher,AutoCloseable{
	/**
	 * 获得服务资源池列表
	 * @return 服务资源池列表
	 */
	public ServantPool [] getPools();
		
	/**
	 * 重新装入指定服务的资源池
	 * @param _id 服务id
	 * @return 服务资源池
	 * @throws ServantException
	 */
	public ServantPool reloadPool(Path _id);
	
	/**
	 * 获取指定服务的的服务资源池
	 * @param _id 服务Id
	 * @return 服务资源池
	 * @throws ServantException
	 */
	public ServantPool getPool(Path _id);	
	
	public static class TheFactory extends Factory<ServantFactory>{
		
	}
}
