package com.alogic.remote.backend;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import com.anysoft.loadbalance.DefaultCounter;
import com.anysoft.loadbalance.Load;
import com.anysoft.loadbalance.LoadCounter;
import com.anysoft.util.Configurable;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

/**
 * 远程调用的后端节点信息
 * 
 * @author yyduan
 * @since 1.6.8.12
 */
public interface Backend extends Load {
	
	/**
	 * 获取后端节点的ip
	 * @return ip
	 */
	public String getIp();
	
	/**
	 * 获取后端节点的服务端口
	 * @return port
	 */
	public String getPort();
	
	/**
	 * 获取后端节点的标签(用于特定情况下的路由选择)
	 * @return 标签列表
	 */
	public String[] getLabels();
	
	/**
	 * 获取后端节点的版本(用于特定情况下的路由选择)
	 * @return 版本
	 */
	public String getVersion();
	
	/**
	 * Web context路径
	 * @return
	 */
	public String getContextPath();
	
	/**
	 * 虚基类实现
	 * 
	 * @author yyduan
	 *
	 */
	public static class Default implements Backend,XMLConfigurable,Configurable{
		/**
		 * 计数器
		 */
		protected LoadCounter counter = null;
		
		/**
		 * ip地址
		 */
		protected String ip;
		
		/**
		 * 端口
		 */
		protected String port;
		
		/**
		 * 标签
		 */
		protected String labels;

		/**
		 * 版本
		 */
		protected String version;
		
		/**
		 * 权重
		 */
		protected int weight;
		
		/**
		 * 优先级
		 */
		protected int priority;
		
		protected String contextPath;
		
		@Override
		public LoadCounter getCounter(boolean create) {
			if (counter == null && create){
				synchronized (this){
					if (counter == null){
						counter = new DefaultCounter(Settings.get());
					}
				}
			}
			
			return counter;
		}

		@Override
		public void count(long duration, boolean error) {
			if (counter != null){
				counter.count(duration, error);
			}
		}

		@Override
		public boolean isValid() {
			boolean valid = counter == null ? true:counter.isValid();
			return valid;
		}

		@Override
		public void report(Element xml) {
			if (xml != null){
				XmlTools.setString(xml, "module", getClass().getName());
				XmlTools.setString(xml, "ip", ip);
				XmlTools.setString(xml, "port", port);
				XmlTools.setString(xml, "labels", labels);
				XmlTools.setString(xml, "version", version);
				XmlTools.setInt(xml, "weight", weight);
				XmlTools.setInt(xml, "priority", priority);
				XmlTools.setString(xml, "contextPath", contextPath);
			}
		}

		@Override
		public void report(Map<String, Object> json) {
			if (json != null){
				JsonTools.setString(json, "module", getClass().getName());
				JsonTools.setString(json, "ip", ip);
				JsonTools.setString(json, "port", port);
				JsonTools.setString(json, "labels", labels);
				JsonTools.setString(json, "version", version);
				JsonTools.setInt(json, "weight", weight);
				JsonTools.setInt(json, "priority", priority);
				JsonTools.setString(json, "contextPath", contextPath);
			}
		}

		@Override
		public String getId() {
			if (StringUtils.isEmpty(contextPath)){
				return ip + ':' + port;
			}else{
				return ip + ':' + port + '$' + contextPath;
			}
		}

		@Override
		public int getWeight() {
			return weight;
		}

		@Override
		public int getPriority() {
			return priority;
		}

		@Override
		public void configure(Properties p) {
			ip = PropertiesConstants.getString(p,"ip","");
			port = PropertiesConstants.getString(p,"port","");
			labels = PropertiesConstants.getString(p,"labels","default");
			version = PropertiesConstants.getString(p,"version","");
			weight = PropertiesConstants.getInt(p,"weight",1);
			priority = PropertiesConstants.getInt(p,"priority",1);
			contextPath = PropertiesConstants.getString(p,"contextPath","");
			
			counter = new DefaultCounter(p);
		}

		@Override
		public void configure(Element e, Properties p) {
			XmlElementProperties props = new XmlElementProperties(e,p);
			configure(props);
		}

		@Override
		public String getIp() {
			return ip;
		}

		@Override
		public String getPort() {
			return port;
		}

		@Override
		public String[] getLabels() {
			return StringUtils.isEmpty(labels)?new String[0]:labels.split(",");
		}

		@Override
		public String getVersion() {
			return version;
		}
		
		@Override
		public String getContextPath(){
			return contextPath;
		}
	}
}
