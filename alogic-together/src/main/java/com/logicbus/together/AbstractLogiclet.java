package com.logicbus.together;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.Message;
import com.logicbus.models.servant.Argument;
import com.logicbus.models.servant.DefaultArgument;


/**
 * Logiclet的虚类
 * 
 * <br>
 * 本类为子类提供了下列框架：<br>
 * - 参数提取框架 <br>
 * - 父节点的连接 <br>
 * - Logiclet的工厂类
 * 
 * 
 * @author duanyy
 * 
 * @since 1.1.0
 * @version 1.2.0 增加对JSON支持
 * 
 * @version 1.2.1 ExecuteWatcher修改了方法
 */
abstract public class AbstractLogiclet implements Logiclet {

	/**
	 * a logger of log4j
	 */
	protected static final Logger logger = LogManager.getLogger(AbstractLogiclet.class);

	private String code = "core.ok";
	private String reason = "It is ok.";
	
	/**
	 * 获取结果代码
	 * @return
	 */
	public String getCode(){return code;}

	/**
	 * 获取结果原因
	 * @return
	 */
	public String getReason(){return reason;}
	
	/**
	 * 是否存在错误
	 * @return
	 */
	public boolean hasError(){return !code.equals("core.ok");}
	/**
	 * 设置结果
	 * @param _code 
	 * @param _reason
	 */
	protected void setResult(String _code,String _reason){
		code = _code ; reason = _reason;
	}
	
	
	public void compile(Element config, Properties props, Logiclet parent,LogicletFactory factory){
		//建立树型结构
		theParent = parent;
		
		//读取参数配置信息
		NodeList eArguments = XmlTools.getNodeListByPath(config, "arguments/argu");
		if (eArguments != null){
			if (argumentList == null){
				argumentList = new HashMap<String,Argument>();
			}else{
				argumentList.clear();
			}
			
			for (int i = 0 ; i < eArguments.getLength() ; i ++){
				Node n = eArguments.item(i);
				if (n.getNodeType() != Node.ELEMENT_NODE){
					continue;
				}
				Element e = (Element) n;
				Argument argu = new DefaultArgument();
				argu.fromXML(e);
				if (argu.getId().length() <= 0){
					continue;
				}
				argumentList.put(argu.getId(),argu);
			}
		}
		
		//读取参数
		XmlElementProperties myProps = new XmlElementProperties(config,props);
		
		ignoreException = PropertiesConstants.getBoolean(myProps, "ignoreException", false);
		outputDuration = PropertiesConstants.getBoolean(myProps, "outputDuration", false);
		try {
			onCompile(config,myProps,factory);
		}catch (ServantException ex){
			if (!ignoreException){
				setResult(ex.getCode(),ex.getMessage());
			}else{
				// 1.2.0 当忽略异常的时候 ，在log中打印
				logger.error("Exception occurs.",ex);
			}
		}
		
	}

	/**
	 * 编译配置文档
	 * 
	 * <br>
	 * 留给子类实现的接口。
	 * 
	 * @param config 配置文档
	 * @param myProps 变量集
	 */
	abstract protected void onCompile(Element config, Properties myProps,LogicletFactory factory) throws ServantException;
	
	
	public void execute(Element target, Message msg, Context ctx,ExecuteWatcher watcher){
		//在一次执行之后，缓存参数需要清掉
		if (parameterCache != null)
			parameterCache.clear();
		
		long start = System.currentTimeMillis();
		try {
			// 1.1.3 修正bug
			setResult("core.ok","It is ok");
			onExecute(target,msg,ctx,watcher);
		}catch (ServantException ex){
			if (!ignoreException){
				setResult(ex.getCode(),ex.getMessage());
			}else{
				// 1.2.0 当忽略异常的时候 ，在log中打印
				logger.error("Exception occurs.",ex);
			}
		}finally{
			long end = System.currentTimeMillis();
			if (outputDuration)
				target.setAttribute("duration", String.valueOf(end - start));
			
			if (watcher != null){
				watcher.executed(this, ctx,end - start,TimeUnit.MILLISECONDS);
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	
	public void execute(Map target, Message msg, Context ctx,ExecuteWatcher watcher){
		//在一次执行之后，缓存参数需要清掉
		if (parameterCache != null)
			parameterCache.clear();
		
		long start = System.currentTimeMillis();
		try {
			// 1.1.3 修正bug
			setResult("core.ok","It is ok");
			onExecute(target,msg,ctx,watcher);
		}catch (ServantException ex){
			if (!ignoreException){
				setResult(ex.getCode(),ex.getMessage());
			}else{
				// 1.2.0 当忽略异常的时候 ，在log中打印
				logger.error("Exception occurs.",ex);
			}
		}finally{
			long end = System.currentTimeMillis();
			if (outputDuration)
				target.put("duration", String.valueOf(end - start));
			
			if (watcher != null){
				watcher.executed(this, ctx,end - start,TimeUnit.MILLISECONDS);
			}
		}
	}	
	
	/**
	 * 执行
	 * 
	 * <br>
	 * 留给子类实现
	 * @param target 输出的XML节点
	 * @param msg 消息
	 * @param ctx 上下文
	 * @throws ServantException
	 */
	abstract protected void onExecute(Element target,Message msg,Context ctx,ExecuteWatcher watcher) throws ServantException;
	/**
	 * 执行
	 * 
	 * <br>
	 * 留给子类实现
	 * @param target 输出的JSON节点
	 * @param msg 消息
	 * @param ctx 上下文
	 * @throws ServantException
	 */
	@SuppressWarnings("rawtypes")
	abstract protected void onExecute(Map target,Message msg,Context ctx,ExecuteWatcher watcher) throws ServantException;
	
	public String getArgument(String id, String defaultValue, Element target,Message msg,
			Context ctx) throws ServantException {
		
		if (parameterCache != null){
			//看看是否有缓存参数
			String value = parameterCache.get(id);
			if (value != null && value.length() > 0){
				return value;
			}
		}
		
		String value = null;
		
		Argument argument = getArgument(id);
		
		if (argument != null){
			//已经配置了参数
			value = argument.getValue(msg, ctx);	
			if (argument.isCached()){
				//参数可以缓存起来供下一次使用
				if (parameterCache == null){
					parameterCache = new HashMap<String,String>();
				}
				
				parameterCache.put(id, value);
			}			
		}else{
			//如果没有定义
			//从target的属性中提取
			if (target != null){
				value = target.getAttribute(id);
			}
			if (value == null || value.length() <= 0){
				//没有取到，从父节点提取
				if (theParent != null){
					//父节点不取target
					value = theParent.getArgument(id, defaultValue, (Element)null, msg, ctx);
				}
			}			
		}
		if (value == null || value.length() <= 0){
			value = defaultValue;
		}
		
		return value;
	}	
	@SuppressWarnings("rawtypes")
	
	public String getArgument(String id, String defaultValue, Map target,Message msg,
			Context ctx) throws ServantException {
		
		if (parameterCache != null){
			//看看是否有缓存参数
			String value = parameterCache.get(id);
			if (value != null && value.length() > 0){
				return value;
			}
		}
		
		String value = null;
		
		Argument argument = getArgument(id);
		
		if (argument != null){
			//已经配置了参数
			value = argument.getValue(msg, ctx);	
			if (argument.isCached()){
				//参数可以缓存起来供下一次使用
				if (parameterCache == null){
					parameterCache = new HashMap<String,String>();
				}
				
				parameterCache.put(id, value);
			}			
		}else{
			//如果没有定义
			//从target的属性中提取
			if (target != null){
				Object found = target.get(id);
				if (found instanceof String){
					value = (String)found;
				}
			}
			if (value == null || value.length() <= 0){
				//没有取到，从父节点提取
				if (theParent != null){
					//父节点不取target
					value = theParent.getArgument(id, defaultValue, (Element)null, msg, ctx);
				}
			}			
		}
		if (value == null || value.length() <= 0){
			value = defaultValue;
		}
		
		return value;
	}

	/**
	 * 获取服务调用参数列表
	 * @return
	 */
	public Argument [] getArgumentList(){
		if (argumentList == null)
			return null;
		
		return argumentList.values().toArray(new Argument[0]);
	}
	
	/**
	 * 获取数据库连接
	 * 
	 * @param ctx 上下文
	 * @return
	 * @throws ServantException
	 * 
	 * @since 1.2.0
	 */
	public Connection getConnection(Context ctx)throws ServantException{
		Connection conn = ctx.getConnection();
		if (conn == null){
			throw new ServantException("core.noconnection","Can not get a connection from context.");
		}
		return conn;
	}
	
	/**
	 * 获取指定ID的参数
	 * @param id 参数Id
	 * @return 
	 */
	public Argument getArgument(String id){
		return argumentList == null ? null : argumentList.get(id);
	}	
	
	/**
	 * logiclet所用参数列表
	 */
	protected HashMap<String,Argument> argumentList = null;	
	
	/**
	 * 缓存起来的参数
	 */
	protected HashMap<String,String> parameterCache = null;
	
	/**
	 * 父节点
	 */
	private Logiclet theParent = null;
	
	/**
	 * 获取父节点实例
	 * @return
	 */
	public Logiclet getParent(){return theParent;}
	
	/**
	 * 是否忽略异常
	 */
	boolean ignoreException = true;
	
	/**
	 * 是否输出耗时
	 */
	boolean outputDuration = true;
	
	/**
	 * 是否忽略异常
	 * @return
	 */
	public boolean ignoreException(){return ignoreException;}
	
	/**
	 * 根据类名创建logiclet实例
	 * @param module logiclet类名
	 * @return
	 */
	public static Logiclet newLogiclet(String module){				
		return factory.newLogiclet(module);
	}

	private static LogicletFactory factory = new LogicletFactory.Default(null);
	
	/**
	 * id种子，用于为每个logiclet实例生成唯一的实例
	 */
	private static volatile int idSeeds = 0;
	
	/**
	 * 为每个logiclet实例生成唯一实例
	 * @return
	 */
	synchronized static public String getUniqueId(){
		if (idSeeds > Integer.MAX_VALUE){
			idSeeds = 0;
		}
		return "logiclet" + String.valueOf(idSeeds ++);
	}

}
