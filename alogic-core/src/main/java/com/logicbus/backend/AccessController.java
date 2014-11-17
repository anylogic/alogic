package com.logicbus.backend;

import com.anysoft.metrics.core.MetricsReportable;
import com.anysoft.util.BaseException;
import com.anysoft.util.Factory;
import com.anysoft.util.Reportable;
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
 * - 扩展Reportable,MetricsReportable
 */
public interface AccessController extends Reportable,MetricsReportable{
	
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
		public TheFactory(ClassLoader cl){
			super(cl);
		}
		/**
		 * 根据module映射类名
		 */
		public String getClassName(String _module) throws BaseException{
			if (_module.indexOf(".") < 0){
				return "com.logicbus.backend." + _module;
			}
			return _module;
		}		
	}
}
