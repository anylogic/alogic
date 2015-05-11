package com.anysoft.xscript;

import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;

/**
 * 脚本语句
 * 
 * @author duanyy
 * @since 1.6.3.22
 */
public interface Statement extends XMLConfigurable,Reportable{
	/**
	 * 执行语句
	 * @param p 参数
	 * @return 执行结果
	 */
	public int execute(Properties p,ExecuteWatcher watcher) throws BaseException;
	
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
}
