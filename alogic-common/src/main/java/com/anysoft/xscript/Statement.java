package com.anysoft.xscript;

import org.w3c.dom.Element;

import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.Reportable;

/**
 * 脚本语句
 * 
 * @author duanyy
 * @since 1.6.3.22
 * @version 1.6.3.23 [20150513 duanyy] <br>
 * - 优化编译模式 <br>
 * @version 1.6.3.25 <br>
 * - 统一脚本的日志处理机制 <br>
 * 
 */
public interface Statement extends Reportable{
	
	/**
	 * 编译语句
	 * @param e 对应的XML节点
	 * @param p 编译参数
	 * @param watcher 编译监控器
	 * @return 编译结果
	 * 
	 * @since 1.6.3.23
	 */
	public int compile(Element e,Properties p,CompileWatcher watcher);
	
	/**
	 * 执行语句
	 * @param p 参数
	 * @param watcher 执行监控器
	 * @return 执行结果
	 */
	public int execute(Properties p,ExecuteWatcher watcher) throws BaseException;
	
	/**
	 * 写出日志
	 * @param logInfo 日志信息
	 * 
	 * @since 1.6.3.25
	 */
	public void log(ScriptLogInfo logInfo);
	
	/**
	 * 获取父语句
	 * @return 语句
	 */
	public Statement parent();
	
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
	 * 通过xml tag创建Statement实例
	 * 
	 * @param xmlTag xmltag
	 * @return Statement实例
	 */
	public Statement createStatement(String xmlTag,Statement parent);
	
	/**
	 * 注册Statement的实现模块
	 * <p>
	 * 所注册的module在该节点及其子节点有效。
	 * 
	 * @param xmltag xml tag
	 * @param clazz Class实例
	 */
	public void registerModule(String xmltag,Class<?extends Statement> clazz);
	
	/**
	 * 注册异常处理模块
	 * @param id 异常code
	 * @param exceptionHandler 异常处理段
	 */
	public void registerExceptionHandler(String id,Statement exceptionHandler);
	
	/**
	 * 注册日志处理器
	 * 
	 * @param logger 日志处理器
	 * 
	 * @since 1.6.3.25
	 */
	public void registerLogger(ScriptLogger logger);
}
