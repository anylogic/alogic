package com.logicbus.backend;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.metrics.Dimensions;
import com.alogic.metrics.Fragment;
import com.alogic.metrics.Measures;
import com.alogic.metrics.Fragment.Method;
import com.alogic.metrics.impl.DefaultFragment;
import com.alogic.metrics.stream.MetricsCollector;
import com.alogic.pool.impl.Queued;
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
 * - 实现ServantPool接口 <br>
 * 
 * @version 1.2.6.3 [20140815 duanyy] <br>
 * - 配合基础类库Pool修改 <br>
 * 
 * @since 1.2.8.2
 * @version 1.3.0.3 [20141102 duanyy] <br>
 *  - 修正bug:服务统计的统计口径问题 <br>
 *  
 * @version 1.6.4.31 [20160129 duanyy] <br>
 * - 改造计数器体系 <br>
 * 
 * @version 1.6.6.9 [20161209 duanyy] <br>
 * - 从新的框架下继承 <br>
 * 
 * @version 1.6.7.4 [20170118 duanyy] <br>
 * - 淘汰com.anysoft.metrics包 ，改用新的指标框架<br>
 * 
 * @version 1.6.9.9 [20170829 duanyy] <br>
 * - Pool的returnObject接口增加是否出错的参数 <br>
 */
public class QueuedServantPool2 extends Queued implements ServantPool{
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
	 * 超时缺省时间
	 */
	protected int queueTimeout = 0;
	
	/**
	 * 更新统计信息的锁
	 */
	protected ReentrantLock lockStat = new ReentrantLock();	
		
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
	
	@Override
	protected String getIdOfMaxQueueLength() {
		return "servant.maxActive";
	}

	@Override
	protected String getIdOfIdleQueueLength() {
		return "servant.maxIdle";
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	protected <pooled> pooled createObject() {
		return (pooled)createServant(m_desc);
	}
	
	/**
	 * 通过服务描述构造资源池
	 * @param sd 服务描述
	 */
	public QueuedServantPool2(ServiceDescription sd){
		m_desc = sd;

		Properties props = m_desc.getProperties();
		props.SetValue("counter.id", m_desc.getPath());
		m_stat = createCounter(props);

		queueTimeout = PropertiesConstants.getInt(props, "servant.queueTimeout", 10);
		metricsId = PropertiesConstants.getString(props, "servant.metrics.id", metricsId);
		configure(props);
		
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
			p.SetValue("counter.id", m_desc.getPath());
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
	
	@Override
	public Servant borrowObject(int priority){
		return borrowObject(priority,queueTimeout);
	}
	
	@Override
	public void returnObject(Servant obj,boolean hasError) {
		super.returnObject(obj,hasError);
	}		
		
	/**
	 * 根据服务描述创建服务员
	 * @param desc 服务描述
	 * @return 服务员
	 */
	protected Servant createServant(ServiceDescription desc){
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
			logger.error(ExceptionUtils.getStackTrace(e));
			throw new ServantException("core.e1002",e.getMessage());
		} catch (IllegalAccessException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
			throw new ServantException("core.e1002",e.getMessage());
		} catch (ClassNotFoundException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
			throw new ServantException("core.e1002",e.getMessage());
		} catch (MalformedURLException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
			throw new ServantException("core.e1002",e.getMessage());
		}
	}

	
	public void report(MetricsCollector collector) {
		if (collector != null){
			Fragment f = new DefaultFragment(metricsId);
			
			Dimensions dims = f.getDimensions();
			if (dims != null){
				dims.set("svc", m_desc.getPath(), true);
			}
			Measures meas = f.getMeasures();
			if (meas != null){
				meas.set("idle", getIdleCnt(), Method.avg);
				meas.set("wait", getWaitCnt(),Method.avg);
				meas.set("creating", getCreatingCnt(),Method.avg);
				meas.set("working", getWorkingCnt(),Method.avg);
				meas.set("maxActive", getMaxActive(),Method.avg);
				meas.set("maxIdle", getMaxIdle(),Method.avg);
			}
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

	@Override
	public int getHealthScore() {
		return m_stat.getHealthScore();
	}

	@Override
	public int getActiveScore() {
		return m_stat.getActiveScore();
	}


}
