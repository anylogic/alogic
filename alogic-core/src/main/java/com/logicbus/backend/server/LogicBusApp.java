package com.logicbus.backend.server;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alogic.metrics.Fragment;
import com.alogic.metrics.stream.MetricsHandlerFactory;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.Script;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.doc.json.JsonObject;
import com.anysoft.stream.Handler;
import com.anysoft.util.DefaultProperties;
import com.anysoft.util.IOTools;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;
import com.anysoft.webloader.WebApp;
import com.logicbus.backend.AccessController;
import com.logicbus.backend.IpAndServiceAccessController;
import com.logicbus.backend.ServantFactory;
import com.logicbus.backend.bizlog.BizLogger;


/**
 * anyLogicBus基于anyWebLoader的应用
 * 
 * @author duanyy
 * @version 1.2.4.5 [20140709 duanyy]
 * - 增加扩展的配置文件
 * 
 * @version 1.2.5 [20140723 duanyy]
 * - 修正ResourceFactory的bug
 * 
 * @version 1.2.6 [20140807 duanyy] <br>
 * - ServantPool和ServantFactory插件化
 * 
 * @version 1.2.6.5 [20140828 duanyy] <br>
 * - 增加onInit/onDestroy事件，以便子类更好的进行初始化和销毁. <br>
 * 
 * @version 1.2.7 [20140828 duanyy] <br>
 * - 重写BizLogger
 * 
 * @version 1.2.8 [20140914 duanyy] <br>
 * - 增加指标收集体系
 * 
 * @version 1.2.8.1 [20140919 duanyy] <br>
 * - MetricsHandler:getInstance拆分为getClientInstance和getServerInstance
 * 
 * @version 1.3.0.2 [20141031 duanyy] <br>
 * - 增加全局配置文件，变量名为settings.global.master和settings.global.secondary
 * 
 * @version 1.6.3.37 [20140806 duanyy] <br>
 * - 淘汰旧的timer框架，采用新的timer框架 <br>
 * 
 * @version 1.6.4.35 [20160315 duanyy] <br>
 * - AccessController接口变动  <br>
 * 
 * @version 1.6.4.36 [20160321 duanyy] <br>
 * - 增加ketty.web.xml文件，用于替代web.xml中的部分内容 <br>
 * 
 * @version 1.6.4.38 [20160324 duanyy] <br>
 * - 优化WEB退出时的清理工作 <br>
 * 
 * @version 1.6.5.20 [20160715 duanyy] <br>
 * - 服务器启动和关闭时可触发脚本执行 <br>
 * 
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 * 
 * @version 1.6.7.20 <br>
 * - 改造ServantManager模型,增加服务配置监控机制 <br>
 * 
 * @version 1.6.8.14 <br>
 * - 调整init及destroy时各组件的启动次序 <br>
 */
public class LogicBusApp implements WebApp {
	/**
	 * a logger of log4j
	 */
	protected static Logger logger = LoggerFactory.getLogger(LogicBusApp.class);
		
	protected void onInit(Settings settings){	
		XmlTools.setDefaultEncoding(settings.GetValue("http.encoding","utf-8"));
		
		//初始化AccessController
		
		AccessController ac = null;

		try {
			ac = AccessController.TheFactory.get(settings);
			logger.info("AccessController is initialized,module:"
					+ ac.getClass().getName());
		} catch (Exception ex) {
			logger.error("Failed to create an AccessController.", ex);
		}

		if (ac == null) {
			ac = new IpAndServiceAccessController();
			ac.configure(settings);
			logger.error("Using default:"
					+ IpAndServiceAccessController.class.getName());
		}
		settings.registerObject("accessController", ac);
		

		//初始化BizLogger
		
		String bizLogHome = PropertiesConstants.getString(settings,
				"bizlog.home", "");
		if (bizLogHome == null || bizLogHome.length() <= 0) {
			logger.info("bizlog.home is not set.Set it to /var/log/bizlog");
			settings.SetValue("bizlog.home", "var/log/bizlog");
		}
		BizLogger bizLogger = BizLogger.TheFactory.getLogger(settings);
		if (bizLogger != null) {
			logger.info("BizLogger is initialized,module:"
					+ bizLogger.getClass().getName());
			settings.registerObject("bizLogger", bizLogger);
		} else {
			logger.error("Can not create a bizlogger instance..");
		}
		
		Handler<Fragment> handler = MetricsHandlerFactory.getClientInstance();
		if (handler != null) {
			logger.info("MetricsHandler is initalized,module:"
					+ handler.getClass().getName());
			settings.registerObject("metricsHandler", handler);
		} else {
			logger.error("Can not create a metrics handler instance.");
		}

		ServantFactory sf = ServantFactory.TheFactory.get(settings);
		if (sf != null){
			logger.info("Servant Factory is initializing,module:" + sf.getClass().getName());
			settings.registerObject("servantFactory", sf);
		}else{
			logger.error("Can not create a servant factory instance.");
		}
	}
	
	@Override
	public void init(DefaultProperties props,ServletContext sc) {
		Settings settings = Settings.get();
		settings.addSettings(props);
		
		// 初始化一些object		
		ClassLoader classLoader = (ClassLoader) sc.getAttribute("classLoader");
		if (classLoader == null){
			classLoader = LogicBusApp.class.getClassLoader();
		}
		settings.registerObject("classLoader", classLoader);
		
		//装入ketty.web.xml
		String webXml = settings.GetValue("ketty.web", "file:///${ketty.home}/conf/ketty.web.xml");
		if (!StringUtils.isEmpty(webXml)){
			logger.info("load ketty web xml settings.");
			logger.info("Url = " + webXml);
			settings.addSettings(webXml, null, null);
			logger.info("Load ketty web xml settings..OK!");
		}
		
		//resourceFactory
		String rf = settings.GetValue("resource.factory","com.anysoft.util.resource.ResourceFactory");
		ResourceFactory resourceFactory = null;
		try {
			logger.info("Use resource factory:" + rf);
			resourceFactory = (ResourceFactory) classLoader.loadClass(rf).newInstance();
		} catch (Exception e) {
			logger.error("Can not create instance of :" + rf,e);
		}
		if (resourceFactory == null){
			resourceFactory = new ResourceFactory();
			logger.info("Use default:" + ResourceFactory.class.getName());
		}
		settings.registerObject("ResourceFactory", resourceFactory);
		
		//装入全局配置文件
		String globalProfile = settings.GetValue("settings.global.master", "");
		String globalSecondaryProfile = settings.GetValue("settings.global.secondary","");
		if (globalProfile != null && globalProfile.length() > 0){
			logger.info("Load global xml settings");
			logger.info("Url = " + globalProfile);
			settings.addSettings(globalProfile, globalSecondaryProfile, resourceFactory);
			logger.info("Load global xml settings..OK!");
		}
		
		//装入扩展的配置文件
		String extProfile = settings.GetValue("settings.ext.master", "");
		String extSecondaryProfile = settings.GetValue("settings.ext.secondary", "");
		if (extProfile != null && extProfile.length() > 0){
			logger.info("Load ext xml settings");
			logger.info("Url = " + extProfile);
			settings.addSettings(extProfile,extSecondaryProfile,resourceFactory);
			logger.info("Load xml settings..OK!");
		}
		
		// 装入配置文件
		String profile = settings.GetValue("settings.master",
				"java:///com/logicbus/backend/server/profile.default.xml#com.logicbus.backend.server.LogicBusApp");	
		String secodaryProfile = settings.GetValue("settings.secondary",
				"java:///com/logicbus/backend/server/profile.default.xml#com.logicbus.backend.server.LogicBusApp");
		
		logger.info("Load xml settings..");
		logger.info("Url = " + profile);
		settings.addSettings(profile,secodaryProfile,resourceFactory);
		logger.info("Load xml settings..OK!");

		onInit(settings);
	}
	
	@Override
	public void start(){
		Settings settings = Settings.get();
		String script = settings.GetValue("script.bootup","");
		if (StringUtils.isNotEmpty(script)){
			logger.info("Execute script:" + script);
			try {
				Script logiclet = Script.create(script, settings);
				if (logiclet != null){
					Map<String,Object> root = new HashMap<String,Object>();
					XsObject doc = new JsonObject("root",root);
					logiclet.execute(doc, doc, new LogicletContext(settings), new ExecuteWatcher.Quiet());
				}
			}catch (Exception ex){
				logger.error("Failed to execute script:" + script);
			}
		}		
	}
	
	@Override
	public void stop(){
		Settings settings = Settings.get();
		String script = settings.GetValue("script.shutdown","");
		if (StringUtils.isNotEmpty(script)){
			logger.info("Execute script:" + script);
			try {
				Script logiclet = Script.create(script, settings);
				if (logiclet != null){
					Map<String,Object> root = new HashMap<String,Object>();
					XsObject doc = new JsonObject("root",root);
					logiclet.execute(doc, doc, new LogicletContext(settings), new ExecuteWatcher.Quiet());
				}
			}catch (Exception ex){
				logger.error("Failed to execute script:" + script);
			}
		}		
	}

	protected void onDestroy(Settings settings){		
		ServantFactory sf = (ServantFactory)settings.get("servantFactory");
		if (sf != null){
			logger.info("The servantFactory is closing..");
			settings.unregisterObject("servantFactory");
			IOTools.close(sf);
		}

		BizLogger bizLogger = (BizLogger)settings.get("bizLogger");
		if (bizLogger != null){
			bizLogger.flush(System.currentTimeMillis());
			logger.info("The bizLogger is closing..");
			settings.unregisterObject("bizLogger");
			IOTools.close(bizLogger);
		}
		
		// since 1.2.8
		@SuppressWarnings("unchecked")
		Handler<Fragment> metricsHandler = (Handler<Fragment>)settings.get("metricsHandler");
		if (metricsHandler != null){
			metricsHandler.flush(System.currentTimeMillis());
			logger.info("The metrics handler is closing..");
			settings.unregisterObject("metricsHandler");
			IOTools.close(metricsHandler);
		}
		
		settings.unregisterObject("accessController");
	}
	
	@Override
	public void destroy(ServletContext sc) {			
		Settings settings = Settings.get();		
		onDestroy(settings);
		settings.unregisterObject("classLoader");
		settings.unregisterObject("ResourceFactory");
	}
}
