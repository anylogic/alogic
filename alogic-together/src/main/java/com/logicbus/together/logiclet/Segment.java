package com.logicbus.together.logiclet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlTools;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.Message;
import com.logicbus.together.AbstractLogiclet;
import com.logicbus.together.ExecuteWatcher;
import com.logicbus.together.Logiclet;
import com.logicbus.together.LogicletFactory;

/**
 * Segment
 * 
 * <br>
 * Segment是一种容器，包含一段XML协议内容和logiclet.
 * 
 * @author duanyy
 * 
 * @since 1.1.0
 * 
 * @version 1.2.0 增加对JSON支持
 */
public class Segment extends AbstractLogiclet {
	
	/**
	 * 异步模式开启，缺省为true
	 */
	private boolean async = true;
	
	/**
	 * 是否开启异步模式
	 * @return
	 */
	public boolean isAsync(){return async;}
	
	/**
	 * 异步模式下，主线程超时时间,缺省为5s
	 */
	private int timeout = 5000;
	
	
	protected void onCompile(Element config, Properties myProps,LogicletFactory factory)
			throws ServantException {
		//异步模式
		async = PropertiesConstants.getBoolean(myProps, "async", async);
		//超时时间
		timeout = PropertiesConstants.getInt(myProps, "timeout", timeout);
		
		content = XmlTools.getFirstElementByTagName(config, "content");
		
		if (content != null){
			//对协议内容进行编译
			compile(content,myProps,factory);
		}
	}

	/**
	 * 迭代编译节点内容
	 * 
	 * 
	 * @param _content 协议内容节点
	 * @param _props 变量集
	 */
	protected void compile(Element _content, Properties _props,LogicletFactory factory) throws ServantException {
		NodeList children = _content.getChildNodes();
		
		for (int i = 0 ,length = children.getLength();i < length ; i ++){
			Node n = children.item(i);
			if (n.getNodeType() != Node.ELEMENT_NODE){
				continue;
			}
			
			Element e = (Element) n;
			
			if (e.getNodeName().equals("logiclet")){
				//对logiclet节点进行处理
				String module = e.getAttribute("module");
				
				if (module == null || module.length() <= 0){
					//logiclet节点必须显式的指定module
					continue;
				}
				
				try {
					Logiclet instance = null;
					if (factory != null){
						instance = factory.newLogiclet(module);
					}
					if (instance == null){
						instance = newLogiclet(module);
					}					
					instance.compile(e, _props, this,factory);
					
					String uniqueId = getUniqueId();
					e.setAttribute("___uniqueId", uniqueId);
					logiclets.put(uniqueId, instance);
				}catch (ServantException ex){
					if (!ignoreException()){
						throw ex;
					}else{
						// 1.2.0 当忽略异常的时候 ，在log中打印
						logger.error("Exception occurs.",ex);
						continue;
					}
				}
			}else{
				//其他节点，递归进行编译
				compile(e,_props,factory);
			}
		}
	}

	
	public void onExecute(Element target, Message msg, Context ctx,ExecuteWatcher watcher)
			throws ServantException {
		if (content == null){
			return ;
		}
		if (isAsync()){
			//如果异步模式开启,通过门闩来协调进程，子进程数为logiclet数
			int thread = logiclets.size();
			CountDownLatch latch = new CountDownLatch(thread);
			
			executeAsync(content,target,msg,ctx,latch,watcher);
			
			try {
				if (!latch.await(timeout, TimeUnit.MILLISECONDS)){
					throw new ServantException("core.time_out","Time is out or be interrupted.");
				}
				
				//检测错误
				Collection<Logiclet> _logiclets = logiclets.values();
				for (Logiclet logiclet:_logiclets){
					if (logiclet.hasError()){
						throw new ServantException(logiclet.getCode(),logiclet.getReason());
					}
				}
			}catch (ServantException ex){
				throw ex;
			}catch (Exception ex){
				throw new ServantException("core.fatalerror",ex.getMessage());
			}
			
		}else{
			execute(content,target,msg,ctx,watcher);
		}
	}

	/**
	 * 异步迭代执行节点内容
	 * @param content 当前节点
	 * @param target 目标节点
	 * @param msg 消息
	 * @param ctx 上下文
	 * @param latch 同步控制门闩
	 */
	protected void executeAsync(Element _content, Element target, Message msg,
			Context ctx, CountDownLatch latch,ExecuteWatcher watcher) throws ServantException {
		
		Document doc = target.getOwnerDocument();
		
		NodeList children = _content.getChildNodes();
		
		for (int i = 0 ,length = children.getLength();i < length ; i ++){
			Node n = children.item(i);
			int nodeType = n.getNodeType();
			
			switch (nodeType){
				case Node.TEXT_NODE:
					synchronized (target){
						target.appendChild(doc.createTextNode(n.getNodeValue()));
					}
					break;
				case Node.ELEMENT_NODE:
					Element e = (Element)n;
					if (e.getNodeName().equals("logiclet")){
						String id = e.getAttribute("___uniqueId");
						if (id == null || id.length() <= 0){
							//无法找到___uniqueId,我们认为在编译节点就不成功，忽略
							continue;
						}
						Logiclet logiclet = logiclets.get(id);
						if (logiclet == null){
							//找不到logiclet实例，不知道怎么回事，忽略
							continue;
						}
						
						//target节点是其目标节点
						WorkThread thread = new WorkThread(logiclet,latch,target,msg,ctx,watcher);
						thread.start();
					}else{
						//非logiclet节点，直接clone
						Element newElement  = doc.createElement(e.getNodeName());
						//clone attribute
						{
							NamedNodeMap attrs = e.getAttributes();
							for (int j = 0 ; j < attrs.getLength() ; j ++){
								Node attr = attrs.item(j);
								newElement.setAttribute(attr.getNodeName(), attr.getNodeValue());
							}
						}
						
						//process children
						executeAsync(e,newElement,msg,ctx,latch,watcher);
						
						synchronized (target){
							target.appendChild(newElement);
						}
					}
					break;
			}
		}		
	}

	/**
	 * 迭代执行节点内容
	 * @param content 当前节点
	 * @param target 目标节点
	 * @param msg 消息
	 * @param ctx 上下文
	 * @throws ServantException
	 */
	protected void execute(Element _content,Element target, Message msg, Context ctx,ExecuteWatcher watcher) 
			throws ServantException {
		
		Document doc = target.getOwnerDocument();
		
		NodeList children = _content.getChildNodes();
		
		for (int i = 0 ,length = children.getLength();i < length ; i ++){
			Node n = children.item(i);
			int nodeType = n.getNodeType();
			
			switch (nodeType){
				case Node.TEXT_NODE:
					target.appendChild(doc.createTextNode(n.getNodeValue()));
					break;
				case Node.ELEMENT_NODE:
					Element e = (Element)n;
					if (e.getNodeName().equals("logiclet")){
						String id = e.getAttribute("___uniqueId");
						if (id == null || id.length() <= 0){
							//无法找到___uniqueId,我们认为在编译节点就不成功，忽略
							continue;
						}
						Logiclet logiclet = logiclets.get(id);
						if (logiclet == null){
							//找不到logiclet实例，不知道怎么回事，忽略
							continue;
						}
						
						//target节点是其目标节点
						logiclet.execute(target, msg, ctx,watcher);
						
						if (logiclet.hasError()){
							throw new ServantException(logiclet.getCode(),logiclet.getReason());
						}
					}else{
						//非logiclet节点，直接clone
						Element newElement  = doc.createElement(e.getNodeName());
						//clone attribute
						{
							NamedNodeMap attrs = e.getAttributes();
							for (int j = 0 ; j < attrs.getLength() ; j ++){
								Node attr = attrs.item(j);
								newElement.setAttribute(attr.getNodeName(), attr.getNodeValue());
							}
						}
						
						//process children
						execute(e,newElement,msg,ctx,watcher);
						
						target.appendChild(newElement);
					}
					break;
			}
		}		
	}

	/**
	 * 内容节点
	 */
	protected Element content = null;
	
	/**
	 * 本segment所包含的logiclet
	 */
	protected HashMap<String,Logiclet> logiclets = new HashMap<String,Logiclet>();
	
	/**
	 * 异步模式下执行logiclet的工作线程
	 * @author duanyy
	 *
	 */
	protected static class WorkThread extends Thread{
		protected Logiclet logiclet = null;
		protected CountDownLatch latch = null;
		protected Element target = null;
		protected Message msg = null;
		protected Context ctx = null;
		protected ExecuteWatcher watcher = null;
		public WorkThread(Logiclet _logiclet,CountDownLatch _latch,Element _target,Message _msg,Context _ctx,ExecuteWatcher _watcher){
			logiclet = _logiclet;
			latch = _latch;
			target = _target;
			msg = _msg;
			ctx = _ctx;
			watcher = _watcher;
		}
		public void run(){
			try {
				//在这里不对target进行并发控制，留给logiclet实现中对target进行控制
				//synchronized (target){
				if (logiclet != null)
					logiclet.execute(target, msg, ctx,watcher);
				//}
			}finally{
				if (latch != null)
					latch.countDown();
			}
		}
	}

	@SuppressWarnings("rawtypes")
	
	protected void onExecute(Map target, Message msg, Context ctx,
			ExecuteWatcher watcher) throws ServantException {
		if (content == null){
			return ;
		}
		if (isAsync()){
			//如果异步模式开启,通过门闩来协调进程，子进程数为logiclet数
			int thread = logiclets.size();
			CountDownLatch latch = new CountDownLatch(thread);
			
			executeAsync(content,target,msg,ctx,latch,watcher);
			
			try {
				if (!latch.await(timeout, TimeUnit.MILLISECONDS)){
					throw new ServantException("core.time_out","Time is out or be interrupted.");
				}
				
				//检测错误
				Collection<Logiclet> _logiclets = logiclets.values();
				for (Logiclet logiclet:_logiclets){
					if (logiclet.hasError()){
						throw new ServantException(logiclet.getCode(),logiclet.getReason());
					}
				}
			}catch (ServantException ex){
				throw ex;
			}catch (Exception ex){
				throw new ServantException("core.fatalerror",ex.getMessage());
			}
			
		}else{
			execute(content,target,msg,ctx,watcher);
		}
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void executeAsync(Element _content, Map target, Message msg,
			Context ctx, CountDownLatch latch,ExecuteWatcher watcher) throws ServantException {
		NodeList children = _content.getChildNodes();
		
		for (int i = 0 ,length = children.getLength();i < length ; i ++){
			Node n = children.item(i);
			int nodeType = n.getNodeType();
			
			switch (nodeType){
				case Node.TEXT_NODE:
					//JSON模式下放弃Text节点
					break;
				case Node.ELEMENT_NODE:
					Element e = (Element)n;
					if (e.getNodeName().equals("logiclet")){
						String id = e.getAttribute("___uniqueId");
						if (id == null || id.length() <= 0){
							//无法找到___uniqueId,我们认为在编译节点就不成功，忽略
							continue;
						}
						Logiclet logiclet = logiclets.get(id);
						if (logiclet == null){
							//找不到logiclet实例，不知道怎么回事，忽略
							continue;
						}
						
						//target节点是其目标节点
						JsonWorkThread thread = new JsonWorkThread(logiclet,latch,target,msg,ctx,watcher);
						thread.start();
					}else{
						//非logiclet节点，直接clone
						//Element影射为array
						List array = null;
						String key = e.getNodeName();
						synchronized(target){
							Object found = target.get(key);
							if (found != null){
								if (found instanceof List){
									array = (List)found;
								}else{
									array = new ArrayList();
									Object removed = target.remove(key);
									if (removed != null)
									array.add(removed);
									target.put(key, array);
								}
							}
						}
						
						Map map = new HashMap();
						//clone attribute
						{
							NamedNodeMap attrs = e.getAttributes();
							for (int j = 0 ; j < attrs.getLength() ; j ++){
								Node attr = attrs.item(j);
								map.put(attr.getNodeName(), attr.getNodeValue());
							}
						}
						
						//process children
						executeAsync(e,map,msg,ctx,latch,watcher);
						
						if (array != null){
							synchronized(array){
								array.add(map);		
							}
						}else{
							synchronized(target){
								target.put(key, map);		
							}
						}
					}
					break;
			}
		}		
	}	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void execute(Element _content,Map target, Message msg, Context ctx,ExecuteWatcher watcher) 
			throws ServantException {
		
		NodeList children = _content.getChildNodes();
		
		for (int i = 0 ,length = children.getLength();i < length ; i ++){
			Node n = children.item(i);
			int nodeType = n.getNodeType();
			
			switch (nodeType){
				case Node.TEXT_NODE:
					//JSON模式下放弃Text节点
					break;
				case Node.ELEMENT_NODE:
					Element e = (Element)n;
					if (e.getNodeName().equals("logiclet")){
						String id = e.getAttribute("___uniqueId");
						if (id == null || id.length() <= 0){
							//无法找到___uniqueId,我们认为在编译节点就不成功，忽略
							continue;
						}
						Logiclet logiclet = logiclets.get(id);
						if (logiclet == null){
							//找不到logiclet实例，不知道怎么回事，忽略
							continue;
						}
						
						//target节点是其目标节点
						logiclet.execute(target, msg, ctx,watcher);
						
						if (logiclet.hasError()){
							throw new ServantException(logiclet.getCode(),logiclet.getReason());
						}
					}else{
						//非logiclet节点，直接clone
						//Element影射为array
						List array = null;
						String key = e.getNodeName();
						Object found = target.get(key);
						if (found != null){
							if (found instanceof List){
								array = (List)found;
							}else{
								array = new ArrayList();
								Object removed = target.remove(key);
								if (removed != null)
								array.add(removed);
								target.put(key, array);
							}
						}
						
						Map map = new HashMap();
						//clone attribute
						{
							NamedNodeMap attrs = e.getAttributes();
							for (int j = 0 ; j < attrs.getLength() ; j ++){
								Node attr = attrs.item(j);
								map.put(attr.getNodeName(), attr.getNodeValue());
							}
						}
						
						//process children
						execute(e,map,msg,ctx,watcher);
						
						if (array == null){
							target.put(key, map);
						}else{
							array.add(map);
						}
					}
					break;
			}
		}		
	}	
	/**
	 * 异步模式下执行logiclet的工作线程
	 * @author duanyy
	 *
	 */
	protected static class JsonWorkThread extends Thread{
		protected Logiclet logiclet = null;
		protected CountDownLatch latch = null;
		@SuppressWarnings("rawtypes")
		protected Map target = null;
		protected Message msg = null;
		protected Context ctx = null;
		protected ExecuteWatcher watcher = null;
		@SuppressWarnings("rawtypes")
		public JsonWorkThread(Logiclet _logiclet,CountDownLatch _latch,Map _target,Message _msg,Context _ctx,ExecuteWatcher _watcher){
			logiclet = _logiclet;
			latch = _latch;
			target = _target;
			msg = _msg;
			ctx = _ctx;
			watcher = _watcher;
		}
		public void run(){
			try {
				//在这里不对target进行并发控制，留给logiclet实现中对target进行控制
				//synchronized (target){
				if (logiclet != null)
					logiclet.execute(target, msg, ctx,watcher);
				//}
			}finally{
				if (latch != null)
					latch.countDown();
			}
		}
	}	
}
