package com.alogic.xscript;

import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.log.LogInfo;
import com.anysoft.stream.Handler;
import com.anysoft.util.Configurable;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;

/**
 * 微服务单元
 * 
 * @author duanyy
 * 
 * @version 1.6.8.14 [20170509 duanyy] <br>
 * - 增加xscript的中间文档模型,以便支持多种报文协议 <br>
 * 
 * @version 1.6.11.27 [20180417 duanyy] <br>
 * - 增加对函数的支持 <br>
 */
public interface Logiclet extends Configurable,XMLConfigurable,Reportable{
	
	/**
	 * 执行
	 * @param root 结果文档根目录
	 * @param current 结果文档当前目录
	 * @param ctx 服务上下文
	 */	
	public void execute(XsObject root,XsObject current,LogicletContext ctx,ExecuteWatcher watcher);
	
	/**
	 * 记录日志
	 * @param logInfo
	 */
	public void log(LogInfo logInfo);
	
	/**
	 * 获取父节点
	 * @return 父节点实例
	 */
	public Logiclet parent();
	
	/**
	 * 是否可执行
	 * @return true|false
	 */
	public boolean isExecutable();
	
	/**
	 * 获取XML语法的tag
	 * @return tag
	 */
	public String getXmlTag();
	
	/**
	 * 根据xmlTag创建logiclet
	 * @param xmlTag xml tag
	 * @param parent 父节点
	 * @return logiclet实例
	 */
	public Logiclet createLogiclet(String xmlTag,Logiclet parent);
	
	/**
	 * 注册Logiclet的实现模块
	 * <p>
	 * 所注册的module在该节点及其子节点有效。
	 * 
	 * @param xmltag xml tag
	 * @param clazz Class实例
	 */
	public void registerModule(String xmltag,Class<?extends Logiclet> clazz);
	
	/**
	 * 注册异常处理模块
	 * @param id 异常code
	 * @param exceptionHandler 异常处理段
	 */
	public void registerExceptionHandler(String id,Logiclet exceptionHandler);	
	
	/**
	 * 注册函数
	 * @param id 函数id
	 * @param function 函数处理段
	 */
	public void registerFunction(String id,Logiclet function);
	
	/**
	 * 根据id查找函数
	 * @param id 函数id
	 * @return 函数处理段
	 */
	public Logiclet getFunction(String id);
	
	/**
	 * 注册日志处理器
	 * @param logger 日志处理器
	 */
	public void registerLogger(Handler<LogInfo> logger);
}
