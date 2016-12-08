package com.alogic.naming;

import java.io.InputStream;
import java.util.ArrayList;
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
import com.anysoft.util.Settings;
import com.anysoft.util.Watcher;
import com.anysoft.util.WatcherHub;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;

/**
 * 命名服务入口
 * 
 * @author duanyy
 * @since 1.6.6.8 
 */
public abstract class Naming <O extends Reportable> implements Context<O>,Watcher<O> {

	/**
	 * a logger of log4j
	 */
	protected static final Logger LOG = LogManager.getLogger(Naming.class);
	
	/**
	 * Watcher Hub
	 */
	protected WatcherHub<O> watcherHub = new WatcherHub<O>(); // NOSONAR
	
	/**
	 * 缓存的对象
	 */
	protected Hashtable<String,O> caches = new Hashtable<String,O>(); // NOSONAR	
	
	/**
	 * 子配置环境列表
	 */
	protected List<Context<O>> subContexts = new ArrayList<Context<O>>(); // NOSONAR
	
	/**
	 * 获取配置文件中代表context的tag名
	 * @return tag名
	 */
	protected String getContextName(){
		return "context";
	}
	
	/**
	 * 创建Context对象实例
	 * @param e xml配置节点
	 * @param p 环境变量
	 * @param attrName 类名所对应的属性名称
	 * @return 对象实例
	 */
	protected abstract Context<O> newInstance(Element e, Properties p, String attrName);
	
	@Override
	public void configure(Element root, Properties props) {
		Properties p = new XmlElementProperties(root,props);
		
		NodeList children = XmlTools.getNodeListByPath(root, getContextName());				
		for (int i = 0 ; i < children.getLength() ; i ++){
			Node n = children.item(i);
			if (n.getNodeType() != Node.ELEMENT_NODE){
				continue;
			}
			
			Element e = (Element)n;
			
			try {
				Context<O> ctx = newInstance(e, p,"module"); // NOSONAR
				if (ctx != null){
					ctx.addWatcher(this);
					subContexts.add(ctx);
				}
			}catch (Exception ex){
				LOG.error("Can not create context instance,check your configuration.",ex);
			}
		}
		
		configure(p);
	}
	
	@Override
	public void configure(Properties p) {
		// nothing to do
	}	

	@Override
	public void close() throws Exception {
		caches.clear();
		
		for (Context<O> s:subContexts){
			s.removeWatcher(this);
			IOTools.close(s);
		}
		
		subContexts.clear();
	}

	@Override
	public void report(Element xml) {
		if (xml != null) {
			xml.setAttribute("module", getClass().getName());
			xml.setAttribute("ctxName", getContextName());

			Document doc = xml.getOwnerDocument();

			for (Context<O> c : subContexts) {
				Element context = doc.createElement(getContextName());

				c.report(context);

				xml.appendChild(context);
			}
		}
	}

	@Override
	public void report(Map<String, Object> json) {
		if (json != null){
			json.put("module", getClass().getName());
			json.put("ctxName", getContextName());
			
			List<Object> contexts = new ArrayList<Object>(); // NOSONAR
			
			for (Context<O> c:subContexts){
				Map<String,Object> ctx = new HashMap<String,Object>(); // NOSONAR
				
				c.report(ctx);
				
				contexts.add(ctx);
			}
			
			json.put(getContextName(), contexts);
		}
	}

	@Override
	public void added(String id, O _data) {
		watcherHub.added(id, _data);
	}

	@Override
	public void removed(String id, O _data) {
		caches.remove(id);
		watcherHub.removed(id, _data);
	}

	@Override
	public void changed(String id, O _data) {
		caches.remove(id);
		watcherHub.changed(id, _data);
	}

	@Override
	public void allChanged() {
		caches.clear();
		watcherHub.allChanged();
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
	public O lookup(String name) {
		O found = caches.get(name);
		if (found == null){
			synchronized (caches){
				found = caches.get(name);
				if (found == null){
					found = lookupInSubContexts(name);
					if (found != null){
						caches.put(name, found);
					}
				}
			}
		}
		return found;
	}

	private O lookupInSubContexts(String id) {
		for (Context<O> c:subContexts){
			O found = c.lookup(id);
			if (found != null){
				return found;
			}
		}
		return null;
	}

	/**
	 * 从主/备地址中装入XML文档
	 * @param master 主地址
	 * @param secondary 备地址
	 * @return XML文档
	 */
	protected static Document loadDocument(String master,String secondary){
		ResourceFactory rm = Settings.getResourceFactory();
		Document ret = null;
		InputStream in = null;
		try {
			in = rm.load(master,secondary, null);
			ret = XmlTools.loadFromInputStream(in);		
		} catch (Exception ex){
			LOG.error("Error occurs when load xml file,source=" + master, ex);
		}finally {
			IOTools.closeStream(in);
		}		
		return ret;
	}		
}
