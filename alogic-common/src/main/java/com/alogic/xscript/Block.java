package com.alogic.xscript;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.alogic.tracer.Tool;
import com.alogic.tracer.TraceContext;
import com.anysoft.stream.Handler;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.XmlElementProperties;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.log.LogInfo;

/**
 * 逻辑块
 * 
 * @author duanyy
 * 
 * @version 1.6.8.14 [20170509 duanyy] <br>
 * - 增加xscript的中间文档模型,以便支持多种报文协议 <br>
 * 
 */
public abstract class Block extends AbstractLogiclet{

	/**
	 * 子节点
	 */
	protected List<Logiclet> children = new ArrayList<Logiclet>(); // NOSONAR
	
	/**
	 * 异常处理器
	 */
	protected Hashtable<String,Logiclet> exceptionAndFinallyHandlers = new Hashtable<String,Logiclet>(); // NOSONAR
	
	/**
	 * 日志处理器
	 */
	protected Handler<LogInfo> logHandler = null;

	public Block(String tag, Logiclet p) {
		super(tag, p);
	}	
	
	@Override
	public void configure(Element element, Properties props) {
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
			Logiclet statement = createLogiclet(xmlTag, this);
			
			if (statement != null){
				statement.configure(e, p);
				if (statement.isExecutable()){
					children.add(statement);
				}
			}
		}
		
		configure(p);
	}	

	@Override
	public void execute(XsObject root,XsObject current,final LogicletContext ctx,final ExecuteWatcher watcher) {
		long start = System.currentTimeMillis();
		boolean error = false;
		String msg = "OK";
		TraceContext tc = null;
		if (traceEnable()){
			tc = Tool.start();
		}			
		try {
			onExecute(root,current,ctx,watcher);
		}catch (BaseException ex){
			error = true;
			String id = ex.getCode();
			Logiclet handler = exceptionAndFinallyHandlers.get(id);
			if (handler == null){
				handler = exceptionAndFinallyHandlers.get(STMT_EXCEPTION);
			}
			if (handler != null){
				handler.execute(root,current,ctx,watcher);
			}else{
				throw ex;
			}
		}finally{
			Logiclet handler = exceptionAndFinallyHandlers.get(STMT_FINALLY);
			if (handler != null){
				handler.execute(root,current,ctx,watcher);
			}			
			if (watcher != null){
				watcher.executed(this, ctx, error,start, System.currentTimeMillis() - start);
			}
			if (traceEnable()){
				Tool.end(tc, "LOGICLET", getXmlTag(), error?"FAILED":"OK", msg);
			}				
		}
	}	

	@Override
	public void registerExceptionHandler(String id,Logiclet exceptionHandler){
		exceptionAndFinallyHandlers.put(id, exceptionHandler);
	}	
	
	@Override
	public void registerLogger(Handler<LogInfo> logger) {
		logHandler = logger;
	}		
}
