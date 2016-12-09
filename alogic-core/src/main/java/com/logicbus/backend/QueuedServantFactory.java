package com.logicbus.backend;

import java.lang.reflect.Constructor;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.logicbus.models.catalog.Path;
import com.logicbus.models.servant.ServantManager;
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
 */
public class QueuedServantFactory implements ServantFactory {
	/**
	 * a logger of log4j
	 */
	protected Logger logger = LogManager.getLogger(QueuedServantFactory.class);
	
	/**
	 * 服务资源池列表
	 */
	private Hashtable<String, ServantPool> m_pools = null;
	
	protected Class<? extends ServantPool> poolClazz = null;
	
	/**
	 * constructor
	 */
	@SuppressWarnings("unchecked")
	public QueuedServantFactory(Properties props){
		ServantManager sm = ServantManager.get();
		sm.addWatcher(this);
		m_pools = new Hashtable<String, ServantPool>();
		
		String poolClass = PropertiesConstants.getString(props, 
				"servant.pool", 
				"com.logicbus.backend.QueuedServantPool2");
		
		ClassLoader cl = getClassLoader();
		try {
			poolClazz = (Class<? extends ServantPool>)cl.loadClass(poolClass);
		}catch (Throwable t){
			poolClazz = QueuedServantPool2.class;
			logger.error("Can not load servant pool class,using default:" + QueuedServantPool2.class.getName(),t);
		}
	}
	
	protected ClassLoader getClassLoader(){
		Settings settings = Settings.get();
		ClassLoader cl = (ClassLoader) settings.get("classLoader");
		return cl != null ? cl : Thread.currentThread().getContextClassLoader();
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
	 * @throws ServantException 当没有找到服务定义时抛出
	 */
	protected ServantPool getServantPool(Path id)throws ServantException
	{
		ServantManager sm = ServantManager.get();
		ServiceDescription sd = sm.get(id);
		if (sd == null){
			throw new ServantException("core.service_not_found","No service desc is found:" + id);
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
	 * @throws ServantException
	 */
	public ServantPool reloadPool(Path _id) throws ServantException{
		lockPools.lock();
		try {
			ServantPool temp = m_pools.get(_id.getPath());
			if (temp != null){
				//重新装入的目的是因为更新了服务描述信息			
				ServantManager sm = ServantManager.get();
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
	public ServantPool getPool(Path _id) throws ServantException{
		Object found = m_pools.get(_id.getPath());
		if (found != null){
			return (ServantPool)found;
		}
		lockPools.lock();
		try {
			Object temp = m_pools.get(_id.getPath());
			if (temp != null){		
				ServantPool pool = (ServantPool)temp;
				return pool;
			}

			ServantPool newPool = getServantPool(_id);
			if (newPool != null)
			{
				m_pools.put(_id.getPath(), newPool);
				return newPool;
			}
			return null;
		}finally{
			lockPools.unlock();
		}
	}
	
	/**
	 * m_pools对象锁
	 */
	protected ReentrantLock lockPools = new ReentrantLock();
	
	/**
	 * 关闭
	 */
	public void close(){
		lockPools.lock();
		try {
			Enumeration<ServantPool> pools = m_pools.elements();
			
			while (pools.hasMoreElements()){
				ServantPool sp = pools.nextElement();
				if (sp != null){
					try {
					sp.close();
					}catch (Throwable t){
						
					}
				}
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
