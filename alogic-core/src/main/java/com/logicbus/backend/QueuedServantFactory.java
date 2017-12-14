package com.logicbus.backend;

import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.logicbus.models.catalog.Path;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 基于QueuedPool的ServantFactory
 * 
 * @author duanyy
 * @since 1.2.4
 * 
 * @version 1.2.6 [20140807 duanyy] <br>
 * - ServantPool和ServantFactory插件化 <br>
 * 
 * @version 1.2.8.2 [20141014 duanyy] <br>
 * - 修正异常处理 <br>
 * - servant.pool缺省改为QueuedServantPool2 <br>
 * 
 * @version 1.6.6.9 [20161209 duanyy] <br>
 * - 淘汰QueuedServantPool <br>
 * 
 * @version 1.6.6.13 [20170112 duanyy] <br>
 * - 主容器由hashtable改为ConcurrentHashMap，增强并发性 <br>
 * 
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 */
public class QueuedServantFactory extends ServantFactory.Abstract {

	/**
	 * 服务资源池列表
	 */
	private Map<String, ServantPool> m_pools = null;
	
	/**
	 * 资源池的Class
	 */
	protected Class<? extends ServantPool> poolClazz = null;

	/**
	 * m_pools对象锁
	 */
	protected ReentrantLock lockPools = new ReentrantLock();	
	
	public QueuedServantFactory(){
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void configure(Properties props){
		m_pools = new ConcurrentHashMap<String, ServantPool>();
		
		String poolClass = PropertiesConstants.getString(props, 
				"servant.pool","com.logicbus.backend.QueuedServantPool2",false);
		
		ClassLoader cl = Settings.getClassLoader();
		try {
			poolClazz = (Class<? extends ServantPool>)cl.loadClass(poolClass);
		}catch (Throwable t){
			poolClazz = QueuedServantPool2.class;
			logger.error("Can not load servant pool class,using default:" + QueuedServantPool2.class.getName(),t);
		}
	}
	
	/**
	 * 获得服务资源池列表
	 * @return 服务资源池列表
	 */
	public ServantPool [] getPools(){
		return m_pools.values().toArray(new ServantPool[0]);
	}
	
	/**
	 * 获取指定服务的服务资源池
	 * @param id 服务ID
	 * @return 服务资源池
	 */
	protected ServantPool getServantPool(Path id)
	{
		ServantRegistry sm = getServantRegistry();
		ServiceDescription sd = sm.get(id);
		if (sd == null){
			throw new ServantException("core.e1014","No service desc is found:" + id);
		}

		try {
			Constructor<? extends ServantPool> constructor = 
				poolClazz.getConstructor(ServiceDescription.class);
			return constructor.newInstance(sd);
		}catch (Throwable t){
			logger.error("Can not create servant pool instance,using default:",t);
			return new QueuedServantPool2(sd);
		}
	}
	
	
	/**
	 * 重新装入指定服务的资源池
	 * @param _id 服务id
	 * @return 服务资源池
	 */
	public ServantPool reloadPool(Path _id){
		lockPools.lock();
		try {
			ServantPool temp = m_pools.get(_id.getPath());
			if (temp != null){
				//重新装入的目的是因为更新了服务描述信息			
				ServantRegistry sm = getServantRegistry();
				ServiceDescription sd = sm.get(_id);
				temp.reload(sd);
			}
			return temp;
		}finally{
			lockPools.unlock();
		}
	}
	
	/**
	 * 获取指定服务的的服务资源池
	 * @param _id 服务Id
	 * @return 服务资源池
	 * @throws ServantException
	 */
	public ServantPool getPool(Path _id){
		ServantPool found = m_pools.get(_id.getPath());
		if (found == null){
			lockPools.lock();
			try {
				found = m_pools.get(_id.getPath());
				if (found == null){		
					found = getServantPool(_id);
					if (found != null)
					{
						m_pools.put(_id.getPath(), found);
					}
				}
			}finally{
				lockPools.unlock();
			}
		}
		return found;
	}
	
	/**
	 * 关闭
	 */
	public void close(){
		super.close();
		lockPools.lock();
		try {
			Iterator<ServantPool> iter = m_pools.values().iterator();
			
			while (iter.hasNext()){
				ServantPool sp = iter.next();
				IOTools.close(sp);
			}
		}finally{
			lockPools.unlock();
		}
	}
	
	public void changed(Path id, ServiceDescription desc) {
		lockPools.lock();
		try {
			logger.info("changed" + id);
			ServantPool temp = m_pools.get(id);
			if (temp != null){
				//重新装入的目的是因为更新了服务描述信息			
				logger.info("Service has been changed,reload it:" + id);
				temp.reload(desc);
			}
		}finally{
			lockPools.unlock();
		}
	}
	
	
	public void removed(Path id){
		lockPools.lock();
		try {
			logger.info("removed:" + id);
			ServantPool temp = m_pools.get(id);
			if (temp != null){
				//服务被删除了
				logger.info("Service has been removed,close it:" + id);
				try {
					temp.close();
				}catch (Throwable t){
					
				}
				m_pools.remove(id);
			}
		}finally{
			lockPools.unlock();
		}		
	}
}
