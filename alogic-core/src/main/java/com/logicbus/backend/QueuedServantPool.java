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
import com.anysoft.pool.QueuedPool;
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
 * @version 1.2.6 [20140807 duanyy] <br>
 *  - 实现ServantPool接口 <br>
 * 
 * @version 1.2.6.3 [20140815 duanyy] <br>
 *  - 配合基础类库Pool修改 <br>
 *  
 * @version 1.2.8.2 [20141014 duanyy] <br>
 *  - ServantStat变更 <br>
 *  - 实现Reportable和MetricsReportable <br>
 * 
 * @version 1.2.9.2 [20141017 duanyy] <br>
 *  - 修改ServantStat模型 <br>
 *  
 * @version 1.3.0.3 [20141102 duanyy] <br>
 *  - 修正bug:服务统计的统计口径问题 <br>
 *  
 * @version 1.6.4.31 [20160129 duanyy] <br>
 * - 改造计数器体系 <br>
 */
public class QueuedServantPool extends QueuedPool<Servant> implements ServantPool{
	/**
	 * 服务描述
	 */
	private ServiceDescription desc;
	
	/**
	 * 服务统计
	 */
	private Counter statCounter;
	
	/**
	 * 指标ID
	 */
	protected String metricsId = "svc.pool";
	
	/**
	 * 运行状态
	 */
	protected String status = "running"; // NOSONAR
	
	protected int queueTimeout = 0;
	
	protected ReentrantLock lockStat = new ReentrantLock();		
	
	/**
	 * 通过服务描述构造资源池
	 * @param sd 服务描述
	 */
	public QueuedServantPool(ServiceDescription sd){
		desc = sd;
		
		Properties props = desc.getProperties();
		props.SetValue("counter.id", desc.getPath());
		statCounter = createCounter(props);

		queueTimeout = PropertiesConstants.getInt(props, "servant.queueTimeout", 10);
		
		metricsId = PropertiesConstants.getString(props, "servant.metrics.id", metricsId);
		
		create(props);
		
		logger.info("Initialize the servant pool..");
		logger.info("Id:" + desc.getServiceID());
		logger.info("Name:" + desc.getName());
		logger.info("Module:" + desc.getModule());
		logger.info("MaxActive:" + getMaxActive());
		logger.info("MaxIdle:" + getMaxIdle());
	}	
	
	/**
	 * 获取服务描述
	 * @return ServiceDescription
	 */
	@Override
	public ServiceDescription getDescription(){return desc;}
	
	/**
	 * 获取服务统计
	 * @return 服务统计
	 */
	public Counter getStat(){return statCounter;}
	
	/**
	 * 设置资源池为暂停
	 */
	@Override
	public void pause(){
		status = "pause";
	}
	/**
	 * 恢复资源池为运行
	 */
	@Override
	public void resume(){
		status = "running";
	}
	/**
	 * 判断资源池是否运行状态
	 * @return 资源池是否运行状态
	 */
	@Override
	public boolean isRunning(){
		return status.equals("running");
	}
	
	@Override
	protected String getIdOfMaxQueueLength() {
		return "servant.maxActive";
	}

	@Override
	protected String getIdOfIdleQueueLength() {
		return "servant.maxIdle";
	}
	
	@Override
	protected Servant createObject(){
		return createServant(desc);
	}
	
	protected Counter createCounter(Properties p){
		String module = PropertiesConstants.getString(p,"servant.stat.module", ServantStat.class.getName());
		try {
			return Counter.TheFactory.getCounter(module, p);
		}catch (Exception ex){
			logger.warn("Can not create servant counter:" + module + ",default counter is instead.",ex);
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
	@Override
	public void reload(ServiceDescription sd){
		desc = sd;
		close();
	}	
	
	/**
	 * 访问一次
	 * @param duration 本次访问的时长
	 * @param code 本次访问的错误代码
	 */
	@Override
	public void visited(long duration,String code){
		lockStat.lock();
		try{
			statCounter.count(duration,!code.equals("core.ok"));
		}finally{
			lockStat.unlock();
		}
	}
	
	@Override
	public Servant borrowObject(int priority){
		return borrowObject(priority,queueTimeout);
	}
	
	/**
	 * 根据服务描述创建服务员
	 * @param desc 服务描述
	 * @return 服务员
	 * @throws ServantException
	 */
	protected Servant createServant(ServiceDescription desc){
		String className = desc.getModule();
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
					temp = (Servant)classLoader.loadClass(className).newInstance();
				}finally{
					if (classLoader != null){
						IOTools.closeStream(classLoader);
					}
				}
			}else{
				temp = (Servant)(cl.loadClass(className).newInstance());
			}
			temp.create(desc);			
			return temp;
		}catch (ServantException e){
			throw e;
		} catch (InstantiationException e) {
			throw new ServantException("core.error_module",e.getMessage());
		} catch (IllegalAccessException e) {
			throw new ServantException("core.error_module",e.getMessage());
		} catch (ClassNotFoundException e) {
			throw new ServantException("core.error_module",e.getMessage());
		} catch (MalformedURLException e) {
			throw new ServantException("core.error_remote_module",e.getMessage());
		}
	}

	@Override
	public void report(MetricsCollector collector) {
		if (collector != null){
			Fragment f = new Fragment(metricsId);
			
			Dimensions dims = f.getDimensions();
			if (dims != null)
				dims.lpush(desc.getPath());
			
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
	
	@Override
	public void report(Element xml){
		if (xml != null){
			Document doc = xml.getOwnerDocument();
			
			Element runtime = doc.createElement("runtime");
			
			runtime.setAttribute("status", status);
			
			Element stat = doc.createElement("stat");
			statCounter.report(stat);
			runtime.appendChild(stat);
			
			Element pool = doc.createElement("pool");
			super.report(pool);
			runtime.appendChild(pool);
			
			xml.appendChild(runtime);
		}
	}
	
	@Override
	public void report(Map<String,Object> json){
		if (json != null){
			Map<String,Object> runtime = new HashMap<String,Object>();
			runtime.put("status", status);
			
			Map<String,Object> stat = new HashMap<String,Object>();
			statCounter.report(stat);
			runtime.put("stat", stat);
			
			Map<String,Object> pool = new HashMap<String,Object>();
			super.report(pool);
			runtime.put("pool", pool);
			
			json.put("runtime", runtime);
		}
	}

	@Override
	public int getHealthScore() {
		return statCounter.getHealthScore();
	}

	@Override
	public int getActiveScore() {
		return statCounter.getActiveScore();
	}
}
