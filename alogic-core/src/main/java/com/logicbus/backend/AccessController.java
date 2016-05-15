package com.logicbus.backend;

import java.io.InputStream;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.metrics.core.MetricsReportable;
import com.anysoft.util.Configurable;
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.Reportable;
import com.anysoft.util.Settings;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;
import com.logicbus.models.catalog.Path;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 访问控制器
 * 
 * <p>
 * 访问控制器用于控制客户端对服务器访问的权限，定义了anyLogicBus调度框架访问控制的基本行为。调度框架对AccessController的调用如下：<br>
 * 1. 首先，调度框架会调用{@link #createSessionId(Path, ServiceDescription, Context) createSessionId()}函数创建会话ID; <br>
 * 2. 在调用服务之前，调度框架会调用访问控制器的{@link #accessStart(String,Path, ServiceDescription, Context)}; <br>
 * 3. 在完成服务之后，会调用访问控制器的{@link #accessEnd(String,Path, ServiceDescription, Context)}. <br>
 * 
 * <p>
 * 访问控制器在{@link #accessStart(String, Path,ServiceDescription, Context)}中通过返回值和框架约定权限控制方式，如果返回值小于0，则表明
 * 本次无权访问；如果返回值大于1，则表明本次访问为高优先级访问；其他则表明本次访问为低优先级访问。<br>
 * 
 * @author duanyy
 * 
 * @version 1.0.1 [20140402 duanyy] <br>
 * - 增加{@link #createSessionId(Path, ServiceDescription, Context) createSessionId}函数以避免多次计算SessionId <br>
 * 
 * @version 1.2.8.2 [20141015 duanyy] <br>
 * - 扩展Reportable,MetricsReportable <br>
 * 
 * @version 1.6.4.35 [20160315 duanyy] <br>
 * - 实现XMLConfigurable和Configurable接口 <br>
 * 
 * @version 1.6.5.5 [20160515 duanyy] <br>
 * - 增加reload接口 <br>
 */
public interface AccessController extends Reportable,MetricsReportable,XMLConfigurable,Configurable{
	
	/**
	 * 重新装入配置
	 * @param id 指定对象
	 */
	public void reload(String id);
	
	/**
	 * 针对当前会话创建会话ID
	 * @param serviceId 本次访问的服务ID
	 * @param servant 本次访问服务的描述
	 * @param ctx 本次方位的上下文信息
	 * @return 会话
	 */
	public String createSessionId(Path serviceId,ServiceDescription servant,Context ctx);
	
	/**
	 * 开始访问
	 * 
	 * <p>
	 * 在调用服务之前调用
	 * 
	 * @param sessionId 会话ID
	 * @param serviceId 本次访问的服务ID
	 * @param servant 本次访问服务的描述
	 * @param ctx 本次访问的上下文
	 * @return 访问优先级 <0表明无权访问;>1表明为高优先级访问;其他为低优先级访问
	 */
	public int accessStart(String sessionId,Path serviceId,ServiceDescription servant,Context ctx);
	
	/**
	 * 结束访问
	 * 
	 * @param sessionId 会话
	 * @param serviceId 本次访问的服务ID
	 * @param servant 本次访问服务的描述
	 * @param ctx 本次访问的上下文
	 * @return 无用处，仅仅追求对称美
	 */
	public int accessEnd(String sessionId,Path serviceId,ServiceDescription servant,Context ctx);
	
	/**
	 * AccessController的工厂类
	 * @author duanyy
	 *
	 */
	public static class TheFactory extends Factory<AccessController>{
		/**
		 * 缺省配置文件
		 */
		public static final String DEFAULT = 
				"java:///com/logicbus/backend/ac.default.xml#com.logicbus.backend.AccessController";
		
		/**
		 * a logger of log4j
		 */
		protected static final Logger LOG = LogManager.getLogger(AccessController.class);
		
		/**
		 * 根据module映射类名
		 */
		@Override
		public String getClassName(String module){
			if (module.indexOf(".") < 0){
				return "com.logicbus.backend." + module;
			}
			return module;
		}
		
		public static AccessController get(){
			return get(Settings.get());
		}
		
		public static AccessController get(Properties props){
			String master = props.GetValue("acm.master",DEFAULT);
			String secondary = props.GetValue("acm.secondary",DEFAULT);
			
			ResourceFactory rf = Settings.getResourceFactory();
			
			InputStream in = null;
			try {
				in = rf.load(master,secondary, null);
				Document doc = XmlTools.loadFromInputStream(in);		
				if (doc != null){
					return getAccessController(doc.getDocumentElement(),props);
				}
			}catch (Exception ex){
				LOG.error("Error occurs when load xml file,source=" + master, ex);
			}finally {
				IOTools.closeStream(in);
			}
			return null;			
		}
		
		public static AccessController getAccessController(Element e,Properties p){
			TheFactory factory = new TheFactory();
			return factory.newInstance(e, p);
		}
	}
}
