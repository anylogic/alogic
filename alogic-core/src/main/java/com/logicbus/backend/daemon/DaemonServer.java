package com.logicbus.backend.daemon;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alogic.event.Event;
import com.alogic.event.EventServer;
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;

/**
 * 守护服务器
 * 
 * @author yyduan
 *
 */
public class DaemonServer extends EventServer.Abstract{
	
	protected List<EventServer> daemons = new ArrayList<EventServer>();

	@Override
	public void configure(Element e, Properties p) {
		Properties props = new XmlElementProperties(e,p);
		
		NodeList nodeList = XmlTools.getNodeListByPath(e, getHandlerType());
		String scope = PropertiesConstants.getString(p, "ketty.scope", "runtime");
		Factory<EventServer> f = new Factory<EventServer>();
		
		for (int i= 0 ;i < nodeList.getLength() ; i ++){
			Node node = nodeList.item(i);
			if (node.getNodeType() != Node.ELEMENT_NODE){
				continue;
			}
			
			Element elem = (Element)node;
			
			String itemScope = XmlTools.getString(e, "scope", "");
			if (StringUtils.isNotEmpty(itemScope) && !itemScope.equals(scope)){
				continue;
			}
						
			try {
				EventServer es = f.newInstance(elem, props, "module");
				if (es != null){
					daemons.add(es);
					LOG.info(String.format("Event server [%s:%s] is found.",es.getId(),es.getClass().getName()));
				}
			}catch (Exception ex){
				LOG.error("Can not create event server with : " + XmlTools.node2String(elem));
				LOG.error(ExceptionUtils.getStackTrace(ex));
			}
		}
		
		configure(props);
	}
	
	@Override
	public void start() {
		for (EventServer es:daemons){
			try {
				es.start();
				LOG.info(String.format("Event server [%s:%s] start..",es.getId(),es.getClass().getName()));
				String id = es.getId();
				if (StringUtils.isNotEmpty(id)){
					Settings.get().registerObject(id, es);
					LOG.info(String.format("Event server [%s:%s] has registered to Settings.",es.getId(),es.getClass().getName()));
				}
			}catch (Exception ex){
				LOG.info(String.format("Event server [%s:%s] failed to start.",es.getId(),es.getClass().getName()));
			}
		}
	}

	@Override
	public void stop() {
		for (EventServer es:daemons){
			try {
				es.stop();
				LOG.info(String.format("Event server [%s:%s] stop..",es.getId(),es.getClass().getName()));
				String id = es.getId();
				if (StringUtils.isNotEmpty(id)){
					Settings.get().unregisterObject(id);
					LOG.info(String.format("Event server [%s:%s] has unregistered to Settings.",es.getId(),es.getClass().getName()));
				}
			}catch (Exception ex){
				LOG.info(String.format("Event server [%s:%s] failed to stop.",es.getId(),es.getClass().getName()));
			}
		}
	}

	@Override
	public void join(long timeout) {
		for (EventServer es:daemons){
			es.join(timeout);
		}
	}

	@Override
	public void handle(Event _data, long timestamp) {
		// nothing to do
	}
	
	@Override
	public String getHandlerType() {
		return "daemon";
	}

	public static DaemonServer loadFrom(String master,String secondary,Properties p){
		ResourceFactory rm = Settings.getResourceFactory();
		InputStream in = null;
		try {
			in = rm.load(master,secondary, null);
			Document doc = XmlTools.loadFromInputStream(in);
			if (doc != null){
				return loadFrom(doc,p);
			}
		} catch (Exception ex){
			LOG.error("Error occurs when load xml file,source=" + master, ex);
			LOG.error(ExceptionUtils.getStackTrace(ex));
		}finally {
			IOTools.closeStream(in);
		}
		return null;
	}

	public static DaemonServer loadFrom(Document doc, Properties p) {
		return loadFrom(doc.getDocumentElement(),p);
	}

	private static DaemonServer loadFrom(Element root, Properties p) {
		Factory<DaemonServer> f = new Factory<DaemonServer>();		
		return f.newInstance(root, p, "module", DaemonServer.class.getName());
	}
	
	
}
