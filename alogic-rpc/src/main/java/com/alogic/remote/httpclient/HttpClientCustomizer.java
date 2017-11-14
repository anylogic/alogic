package com.alogic.remote.httpclient;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
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
 * HttpClient定制器
 * @author yyduan
 * @since 1.6.10.6
 */
public interface HttpClientCustomizer extends Configurable,XMLConfigurable{
	
	/**
	 * 定制http client
	 * @param builder HttpClientBuilder
	 */
	public HttpClientBuilder customizeHttpClient(HttpClientBuilder builder,Properties p);
	
	/**
	 * 定制http request
	 * @param builder RequestConfig.Builder
	 */
	public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder builder,Properties p);
	
	/**
	 * 虚基类
	 * @author yyduan
	 *
	 */
	public abstract static class Abstract implements HttpClientCustomizer{
		/**
		 * logger of slf4j
		 */
		protected static final Logger LOG = LoggerFactory.getLogger(HttpClientCustomizer.class);
		
		@Override
		public void configure(Element e, Properties p) {
			XmlElementProperties props = new XmlElementProperties(e,p);
			configure(props);
		}
		
		@Override
		public void configure(Properties p) {
			
		}
	}
	
	/**
	 * Sinkable
	 * @author yyduan
	 *
	 */
	public abstract static class Sinkable extends Abstract{
		/**
		 * 子节点
		 */
		protected List<HttpClientCustomizer> children = new ArrayList<HttpClientCustomizer>();

		@Override
		public void configure(Element e, Properties p) {
			Properties props = new XmlElementProperties(e,p);
			configure(props);
			
			Factory<HttpClientCustomizer> f = new Factory<HttpClientCustomizer>();
			
			NodeList nodeList = XmlTools.getNodeListByPath(e, "sink");
			for (int i = 0 ;i < nodeList.getLength() ; i ++){
				Node node = nodeList.item(i);
				if (Node.ELEMENT_NODE != node.getNodeType()){
					continue;
				}
				Element elem = (Element)node;
				
				try {
					HttpClientCustomizer filter = f.newInstance(elem, props, "module");
					if (filter != null){
						children.add(filter);
					}
				}catch (Exception ex){
					LOG.error("Can not create customizer from element:" + XmlTools.node2String(elem));
					LOG.error(ExceptionUtils.getStackTrace(ex));
				}
			}
		}
		
		@Override
		public HttpClientBuilder customizeHttpClient(HttpClientBuilder builder,Properties p){
			for (HttpClientCustomizer item:children){
				item.customizeHttpClient(builder, p);
			}
			return builder;
		}
		
		@Override
		public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder builder,Properties p){
			for (HttpClientCustomizer item:children){
				item.customizeRequestConfig(builder, p);
			}
			return builder;
		}
	}
}
