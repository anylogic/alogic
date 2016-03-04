package com.anysoft.xscript;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anysoft.util.BaseException;
import com.anysoft.util.DefaultProperties;
import com.anysoft.util.Properties;
import com.anysoft.util.XmlElementProperties;

/**
 * Block代码段
 * 
 * @author duanyy
 * @since 1.6.3.22
 * @version 1.6.3.23 [20150513 duanyy] <br>
 * - 优化编译模式 <br>
 * @version 1.6.3.25 <br>
 * - 统一脚本的日志处理机制 <br>
 * 
 * @version 1.6.4.33 [20160304 duanyy] <br>
 * - 根据sonar建议优化代码 <br>
 */
public abstract class Block extends AbstractStatement {
	/**
	 * 子节点
	 */
	protected List<Statement> children = new ArrayList<Statement>(); // NOSONAR
	
	/**
	 * 异常处理器
	 */
	protected Hashtable<String,Statement> exceptionAndFinallyHandlers = new Hashtable<String,Statement>(); // NOSONAR
	
	/**
	 * 日志处理器（只支持一个）
	 */
	protected ScriptLogger scriptLogger = null;
	
	public Block(String xmlTag,Statement parent) {
		super(xmlTag,parent);
	}

	@Override
	protected int compiling(Element element, Properties props,CompileWatcher watcher){
		XmlElementProperties p = new XmlElementProperties(element, props);
		NodeList nodeList = element.getChildNodes();
		
		for (int i = 0 ; i < nodeList.getLength() ; i ++){
			Node n = nodeList.item(i);
			
			if (n.getNodeType() != Node.ELEMENT_NODE){
				//只处理Element节点
				continue;
			}
			
			Element e = (Element)n;
			String xmlTag = e.getNodeName();		
			Statement statement = createStatement(xmlTag, this);
			
			if (statement == null){
				if (watcher != null){
					watcher.message(this, "warn", "Can not find plugin:" + xmlTag + ",Ignored.");
				}
			}else{
				statement.compile(e, p,watcher);
				if (statement.isExecutable()){
					children.add(statement);
				}
			}
		}
		return onCompiling(element,p,watcher);
	}
	
	protected abstract int onCompiling(Element e, Properties p,CompileWatcher watcher);
	
	@Override
	public int execute(Properties p,ExecuteWatcher watcher){
		long start = System.currentTimeMillis();
		try {
			return onExecute(p,watcher);
		}catch (BaseException ex){
			String id = ex.getCode();
			Statement handler = exceptionAndFinallyHandlers.get(id);
			if (handler == null){
				handler = exceptionAndFinallyHandlers.get(STMT_EXCEPTION);
			}
			if (handler != null){
				handler.execute(p, watcher);
				return 0;
			}else{
				throw ex;
			}
		}finally{
			Statement handler = exceptionAndFinallyHandlers.get(STMT_FINALLY);
			if (handler != null){
				handler.execute(p, watcher);
			}			
			if (watcher != null){
				watcher.executed(this, p, start, System.currentTimeMillis() - start);
			}
		}
	}
	
	@Override
	public void registerExceptionHandler(String id,Statement exceptionHandler){
		exceptionAndFinallyHandlers.put(id, exceptionHandler);
	}
	
	@Override
	public void registerLogger(ScriptLogger logger){
		scriptLogger = logger;
	}
	
	@Override
	public void log(ScriptLogInfo logInfo){
		if (scriptLogger == null){
			Statement parent = parent();
			if (parent != null){
				parent.log(logInfo);
			}
		}else{
			scriptLogger.handle(logInfo, System.currentTimeMillis());
		}
	}
	
	protected Properties getLocalVariables(Properties parent){
		return new DefaultProperties("Default",parent);
	}
}
