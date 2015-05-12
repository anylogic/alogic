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
 */
abstract public class Block extends AbstractStatement {
	/**
	 * 子节点
	 */
	protected List<Statement> children = new ArrayList<Statement>();
	
	/**
	 * 异常处理器
	 */
	protected Hashtable<String,Statement> exceptionAndFinallyHandlers = new Hashtable<String,Statement>();
	
	public Block(String xmlTag,Statement _parent) {
		super(xmlTag,_parent);
	}

	public void configure(Element _e, Properties _properties)
			throws BaseException {
		XmlElementProperties p = new XmlElementProperties(_e,_properties);
		
		NodeList _children = _e.getChildNodes();
		
		for (int i = 0 ; i < _children.getLength() ; i ++){
			Node n = _children.item(i);
			
			if (n.getNodeType() != Node.ELEMENT_NODE){
				//只处理Element节点
				continue;
			}
			
			Element e = (Element)n;
			String xmlTag = e.getNodeName();		
			Statement statement = createStatement(xmlTag, this);
			
			if (statement == null){
				logger.warn("Can not find plugin:" + xmlTag + ",Ignored.");
			}else{
				statement.configure(e, p);
				if (statement.isExecutable()){
					children.add(statement);
				}
			}
		}
		
		onConfigure(_e,p);
	}
	
	public int execute(Properties p,ExecuteWatcher watcher) throws BaseException{
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
	
	public void registerExceptionHandler(String id,Statement exceptionHandler){
		exceptionAndFinallyHandlers.put(id, exceptionHandler);
	}
	
	protected Properties getLocalVariables(Properties parent){
		return new DefaultProperties("Default",parent);
	}
	
	protected abstract void onConfigure(Element _e,Properties p);
}
