package com.alogic.remote.httpclient;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anysoft.util.Configurable;
import com.anysoft.util.Factory;
import com.anysoft.util.Properties;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

/**
 * HttpClient过滤器
 * @author yyduan
 * @since 1.6.10.6
 */
public interface HttpCientFilter extends Configurable,XMLConfigurable{
	/**
	 * 处理Request事件
	 * @param request request
	 */
	public void onRequest(HttpClientRequest request);
	
	/**
	 * 处理Response事件
	 * @param response response
	 */
	public void onResponse(HttpClientResponse response);
	
	/**
	 * 虚基类
	 * @author yyduan
	 *
	 */
	public abstract static class Abstract implements HttpCientFilter{
		/**
		 * a logger of slf4j
		 */
		protected final Logger LOG = LoggerFactory.getLogger(HttpCientFilter.class);
		
		@Override
		public void configure(Element e, Properties p) {
			Properties props = new XmlElementProperties(e,p);
			configure(props);
		}
		
		@Override
		public void configure(Properties p){
			
		}
	}
	
	/**
	 * Sinkable
	 * @author yyduan
	 *
	 */
	public abstract static class Sinkable extends Abstract {
		protected List<HttpCientFilter> children = new ArrayList<HttpCientFilter>();
		
		@Override
		public void configure(Element e, Properties p) {
			Properties props = new XmlElementProperties(e,p);
			configure(props);
			
			Factory<HttpCientFilter> f = new Factory<HttpCientFilter>();
			
			NodeList nodeList = XmlTools.getNodeListByPath(e, "filter");
			for (int i = 0 ;i < nodeList.getLength() ; i ++){
				Node node = nodeList.item(i);
				if (Node.ELEMENT_NODE != node.getNodeType()){
					continue;
				}
				Element elem = (Element)node;
				
				try {
					HttpCientFilter filter = f.newInstance(elem, props, "module");
					if (filter != null){
						children.add(filter);
					}
				}catch (Exception ex){
					LOG.error("Can not create filter from element:" + XmlTools.node2String(elem));
					LOG.error(ExceptionUtils.getStackTrace(ex));
				}
			}
		}

		@Override
		public void onRequest(HttpClientRequest request) {
			if (!children.isEmpty()){
				for (HttpCientFilter f:children){
					f.onRequest(request);
				}
			}
		}

		@Override
		public void onResponse(HttpClientResponse response) {
			if (!children.isEmpty()){
				for (HttpCientFilter f:children){
					f.onResponse(response);
				}
			}
		}
	}
}
