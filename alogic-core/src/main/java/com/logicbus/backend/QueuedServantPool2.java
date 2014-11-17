package com.logicbus.backend;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.metrics.core.Dimensions;
import com.anysoft.metrics.core.Fragment;
import com.anysoft.metrics.core.Measures;
import com.anysoft.metrics.core.MetricsCollector;
import com.anysoft.pool.QueuedPool2;
import com.anysoft.util.BaseException;
import com.anysoft.util.Counter;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 基于队列的ServantPool
 * 
 * @author duanyy
 * @since 1.2.4
 * 
 * @version 1.2.6 [20140807 duanyy]
 * - 实现ServantPool接口
 * 
 * @version 1.2.6.3 [20140815 duanyy]
 * - 配合基础类库Pool修改
 * 
 * @since 1.2.8.2
 * @version 1.3.0.3 [20141102 duanyy]
 *  - 修正bug:服务统计的统计口径问题
 */
public class QueuedServantPool2 extends QueuedPool2<Servant> implements ServantPool{
	/**
	 * 服务描述
	 */
	private ServiceDescription m_desc;
	
	/**
	 * 服务统计
	 */
	private Counter m_stat;
	/**
	 * 指标ID
	 */
	protected String metricsId = "svc.pool";
	
	/**
	 * 状态
	 */
	protected String status = "running";
	
	/**
	 * 获取服务描述
	 * @return ServiceDescription
	 */
	public ServiceDescription getDescription(){return m_desc;}
	
	/**
	 * 获取服务统计
	 * @return 服务统计
	 */
	public Counter getStat(){return m_stat;}
	
	/**
	 * 设置资源池为暂停
	 */
	public void pause(){
		status = "pause";
	}
	/**
	 * 恢复资源池为运行
	 */
	public void resume(){
		status = "running";
	}
	/**
	 * 判断资源池是否运行状态
	 * @return 是否运行状态
	 */
	public boolean isRunning(){
		return status.equals("running");
	}
	
	
	protected String getIdOfMaxQueueLength() {
		return "servant.maxActive";
	}

	
	protected String getIdOfIdleQueueLength() {
		return "servant.maxIdle";
	}
	
	
	protected Servant createObject() throws BaseException {
		return createServant(m_desc);
	}
	protected int queueTimeout = 0;
	/**
	 * 通过服务描述构造资源池
	 * @param sd 服务描述
	 */
	public QueuedServantPool2(ServiceDescription sd){
		m_desc = sd;

		Properties props = m_desc.getProperties();
		m_stat = createCounter(props);

		queueTimeout = PropertiesConstants.getInt(props, "servant.queueTimeout", 10);
		metricsId = PropertiesConstants.getString(props, "servant.metrics.id", metricsId);
		create(props);
		
		logger.info("Initialize the servant pool..");
		logger.info("Id:" + m_desc.getServiceID());
		logger.info("Name:" + m_desc.getName());
		logger.info("Module:" + m_desc.getModule());
		logger.info("MaxActive:" + getMaxActive());
		logger.info("MaxIdle:" + getMaxIdle());
	}	
	
	protected Counter createCounter(Properties p){
		String module = PropertiesConstants.getString(p,"servant.stat.module", ServantStat.class.getName());
		try {
			return Counter.TheFactory.getCounter(module, p);
		}catch (Exception ex){
			logger.warn("Can not create servant counter:" + module + ",default counter is instead.");
			return new ServantStat(p);
		}
	}
	
	/**
	 * 重新装入服务资源池
	 * 
	 * <br>目的是按照新的服务描述装入资源池
	 * 
	 * @param sd 服务描述
	 */
	public void reload(ServiceDescription sd){
		m_desc = sd;
		close();
	}	
	
	/**
	 * 访问一次
	 * @param duration 本次访问的时长
	 * @param code 本次访问的错误代码
	 */
	public void visited(long duration,String code){
		lockStat.lock();
		try{
			m_stat.count(duration,!code.equals("core.ok"));
		}finally{
			lockStat.unlock();
		}
	}
	
	public Servant borrowObject(int priority) throws BaseException{
		return borrowObject(priority,queueTimeout);
	}
	
	protected ReentrantLock lockStat = new ReentrantLock();	
	
	/**
	 * 根据服务描述创建服务员
	 * @param desc 服务描述
	 * @return 服务员
	 * @throws ServantException
	 */
	protected Servant createServant(ServiceDescription desc) throws ServantException{
		String class_name = desc.getModule();
		Servant temp = null;
		try {			
			//ClassLoader采用当前ClassLoader
			//1.2.0
			ClassLoader cl = null;
			{
				Settings settings = Settings.get();
				cl = (ClassLoader)settings.get("classLoader");
			}
			cl = cl == null ? Thread.currentThread().getContextClassLoader() : cl;
			
			String [] modules = desc.getModules();
			if (modules != null && modules.length > 0){
				logger.info("Load class from remote..");
				URL[] urls = new URL[modules.length];
				int i = 0;
				for (String module:modules){
					String url = desc.getProperties().transform(module);
					urls[i] = new URL(url);
					logger.info("url=" + url);
					i++;
				}
				URLClassLoader classLoader = new URLClassLoader(urls,cl);
				try {
					temp = (Servant)classLoader.loadClass(class_name).newInstance();
				}finally{
					if (classLoader != null){
						IOTools.closeStream(classLoader);
					}
				}
			}else{
				temp = (Servant)(cl.loadClass(class_name).newInstance());
			}
			temp.create(desc);			
			return temp;
		}catch (ServantException e){
			throw e;
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new ServantException("core.error_module",e.getMessage());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new ServantException("core.error_module",e.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new ServantException("core.error_module",e.getMessage());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new ServantException("core.error_remote_module",e.getMessage());
		}
	}

	
	public void report(MetricsCollector collector) {
		if (collector != null){
			Fragment f = new Fragment(metricsId);
			
			Dimensions dims = f.getDimensions();
			if (dims != null)
				dims.lpush(m_desc.getPath());
			
			Measures meas = f.getMeasures();
			if (meas != null)
				meas.lpush(new Object[]{
						getIdleCnt(),
						getWaitCnt(),
						getCreatingCnt(),
						getWorkingCnt(),
						getMaxActive(),
						getMaxIdle()
				});
			
			collector.metricsIncr(f);
		}
	}
	
	
	public void report(Element xml){
		if (xml != null){
			Document doc = xml.getOwnerDocument();
			
			Element runtime = doc.createElement("runtime");
			runtime.setAttribute("status", status);
			
			Element stat = doc.createElement("stat");
			m_stat.report(stat);
			runtime.appendChild(stat);
			
			Element pool = doc.createElement("pool");
			super.report(pool);
			runtime.appendChild(pool);
			
			xml.appendChild(runtime);
		}
	}
	
	
	public void report(Map<String,Object> json){
		if (json != null){
			Map<String,Object> runtime = new HashMap<String,Object>();
			runtime.put("status", status);
			Map<String,Object> stat = new HashMap<String,Object>();
			m_stat.report(stat);
			runtime.put("stat", stat);
			
			Map<String,Object> pool = new HashMap<String,Object>();
			super.report(pool);
			runtime.put("pool", pool);
			
			json.put("runtime", runtime);
		}
	}	
}
