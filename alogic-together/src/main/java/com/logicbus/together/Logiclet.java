package com.logicbus.together;

import java.util.Map;

import org.w3c.dom.Element;

import com.anysoft.util.Properties;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.Message;


/**
 * 逻辑单元
 * 
 * <br>
 * 用于对XML文档中的逻辑单元进行处理，类似于JSP中的Tag.
 * 
 * @author duanyy
 * 
 * @since 1.1.0
 * @version 1.2.0 增加对JSON支持
 */
public interface Logiclet {

	/**
	 * 编译配置文档
	 * 
	 * @param config 配置文档中本logiclet所对应的节点
	 * @param props 环境变量
	 * @param parent 父节点
	 * @param factory 实例创建工厂
	 * @throws ServantException
	 */
	public void compile(Element config,Properties props,Logiclet parent,LogicletFactory factory);

	/**
	 * 执行
	 * 
	 * @param target 输出的XML节点
	 * @param msg 服务消息
	 * @param ctx 服务调用上下文
	 * @param watcher 执行监视器
	 * @return
	 * @throws ServantException
	 */
	public void execute(Element target,Message msg,Context ctx,ExecuteWatcher watcher);
	
	
	/**
	 * 执行
	 * 
	 * @param target 输出的JSON节点
	 * @param msg 服务消息
	 * @param ctx 服务调用上下文
	 * @param watcher 执行监视器
	 * @return
	 * @throws ServantException
	 * @since 1.2.0
	 */
	@SuppressWarnings("rawtypes")
	public void execute(Map target,Message msg,Context ctx,ExecuteWatcher watcher);	
	
	/**
	 * 获取logiclet的参数
	 * 
	 * @param id 参数ID
	 * @param defaultValue 缺省值
	 * @param target 目标节点
	 * @param msg 服务消息
	 * @param ctx 服务调用上下文
	 * @return
	 * @throws ServantException
	 */
	public String getArgument(String id,String defaultValue,Element target,Message msg, Context ctx) throws ServantException;

	/**
	 * 获取logiclet的参数
	 * 
	 * @param id 参数ID
	 * @param defaultValue 缺省值
	 * @param target 目标节点
	 * @param msg 服务消息
	 * @param ctx 服务调用上下文
	 * @return
	 * @throws ServantException
	 */
	@SuppressWarnings("rawtypes")
	public String getArgument(String id,String defaultValue,Map target,Message msg, Context ctx) throws ServantException;	
	
	/**
	 * 获取结果代码
	 * @return
	 */
	public String getCode();

	/**
	 * 获取结果原因
	 * @return
	 */
	public String getReason();
	
	/**
	 * 是否存在错误
	 * @return
	 */
	public boolean hasError();
	
}
