package com.anysoft.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.Reportable;
import com.anysoft.util.Watcher;
import com.anysoft.util.WatcherHub;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

/**
 * 通用配置来源
 * 
 * @author duanyy
 *
 * @param <O>
 * 
 * @since 1.5.0
 * 
 * @version 1.5.2 [20141017 duanyy] <br>
 * - 实现Reportable接口 <br>
 * 
 * @version 1.6.4.4 [20150910 duanyy] <br>
 * - 增加获取当前缓存对象列表的接口 <br>
 * - Report不在输出缓存对象列表 <br>
 * 
 * @version 1.6.4.20 [20151222 duanyy] <br>
 * - 根据sonar建议优化代码 <br>
 */
public abstract class Source<O extends Reportable> implements Context<O>,Watcher<O> {
	
	/**
	 * logger of log4j
	 */
	protected static final Logger logger = LogManager.getLogger(Source.class);	
	
	/**
	 * Watcher Hub
	 */
	protected WatcherHub<O> watcherHub = new WatcherHub<O>(); // NOSONAR
	
	/**
	 * 缓存的对象
	 */
	protected Hashtable<String,O> caches = new Hashtable<String,O>(); // NOSONAR

	/**
	 * 配置来源
	 */
	protected List<Context<O>> sources = new ArrayList<Context<O>>(); // NOSONAR
	
	@Override
	public void configure(Element root, Properties props){
		Properties p = new XmlElementProperties(root,props);
		
		NodeList children = XmlTools.getNodeListByPath(root, getContextName());
				
		for (int i = 0 ; i < children.getLength() ; i ++){
			Node n = children.item(i);
			if (n.getNodeType() != Node.ELEMENT_NODE){
				continue;
			}
			
			Element e = (Element)n;
			
			try {
				Context<O> source = newInstance(e, p,"module"); // NOSONAR
				if (source != null){
					source.addWatcher(this);
					sources.add(source);
				}
			}catch (Exception ex){
				logger.error("Can not create context instance,check your configuration.",ex);
			}
		}
	}
	
	/**
	 * 获取当前的cache列表
	 * @return cache列表
	 */
	public Collection<O> current(){
		return caches.values();
	}

	/**
	 * 创建实例
	 * 
	 * @param e XML配置根节点
	 * @param p 环境变量集
	 * @param attrName XML属性名
	 * @return Context<object>
	 */
	public abstract Context<O> newInstance(Element e, Properties p, String attrName);
	
	protected String getContextName(){
		return "context";
	}

	@Override
	public void close(){
		caches.clear();
		
		for (Context<O> s:sources){
			s.removeWatcher(this);
			IOTools.close(s);
		}
		
		sources.clear();
	}

	@Override
	public O get(String id) {
		O found = caches.get(id);
		if (found == null){
			synchronized (caches){
				found = caches.get(id);
				if (found == null){
					found = load(id);
					if (found != null){
						caches.put(id, found);
					}
				}
			}
		}
		return found;
	}

	private O load(String id) {
		for (Context<O> c:sources){
			O found = c.get(id);
			if (found != null){
				return found;
			}
		}
		return null;
	}

	@Override
	public void addWatcher(Watcher<O> watcher) {
		watcherHub.addWatcher(watcher);
	}

	@Override
	public void removeWatcher(Watcher<O> watcher) {
		watcherHub.removeWatcher(watcher);
	}

	@Override
	public void added(String id, O _data) {
		if (watcherHub != null){
			watcherHub.added(id, _data);
		}
	}

	@Override
	public void removed(String id, O _data) {
		caches.remove(id);
		if (watcherHub != null){
			watcherHub.removed(id, _data);
		}
	}

	@Override
	public void changed(String id, O _data) {
		caches.remove(id);
		if (watcherHub != null){
			watcherHub.changed(id, _data);
		}
	}
	
	@Override
	public void report(Element xml) {
		if (xml != null) {
			xml.setAttribute("module", getClass().getName());
			xml.setAttribute("ctxName", getContextName());

			Document doc = xml.getOwnerDocument();

			for (Context<O> c : sources) {
				Element context = doc.createElement(getContextName());

				c.report(context);

				xml.appendChild(context);
			}
		}
	}
	
	@Override
	public void report(Map<String,Object> json){
		if (json != null){
			json.put("module", getClass().getName());
			json.put("ctxName", getContextName());
			
			List<Object> contexts = new ArrayList<Object>(); // NOSONAR
			
			for (Context<O> c:sources){
				Map<String,Object> ctx = new HashMap<String,Object>(); // NOSONAR
				
				c.report(ctx);
				
				contexts.add(ctx);
			}
			
			json.put(getContextName(), contexts);
		}
	}
}
