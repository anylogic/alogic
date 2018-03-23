package com.alogic.event;

import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.anysoft.stream.Handler;
import com.anysoft.util.Configurable;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

/**
 * 事件服务器
 * @author duanyy
 * @since 1.6.11.25
 */
public interface EventServer extends Handler<Event>,Configurable{
	/**
	 * 启动服务器
	 */
	public void start();
	
	/**
	 * 停止服务器
	 */
	public void stop();
	
	/**
	 * 等待线程结束直到超时
	 */
	public void join(long timeout);
	
	/**
	 * 虚基类
	 * @author duanyy
	 *
	 */
	public abstract static class Abstract implements EventServer{
		
		/**
		 * a logger of slf4j
		 */
		public static final Logger LOG = LoggerFactory.getLogger(EventServer.class);
		
		/**
		 * id
		 */
		protected String id;
		
		/**
		 * sink
		 */
		protected Handler<Event> sink = null;

		@Override
		public void flush(long timestamp) {
			// nothing to do
		}

		@Override
		public String getHandlerType() {
			return "handler";
		}

		@Override
		public void pause() {
			// nothing to do
		}

		@Override
		public void resume() {
			// nothing to do
		}

		@Override
		public String getId() {
			return id;
		}

		@Override
		public void configure(Element e, Properties p) {
			Properties props = new XmlElementProperties(e,p);
			
			Element elem = XmlTools.getFirstElementByPath(e, this.getHandlerType());
			if (elem != null){
				try {
					sink = EventBus.loadFromElement(elem, props);
				}catch (Exception ex){
					LOG.error(ExceptionUtils.getStackTrace(ex));
					LOG.error("Can not create event handler:" + XmlTools.node2String(elem));
				}
			}
			
			configure(props);
		}
		
		/**
		 * 分发事件
		 * @param event
		 */
		protected void dispatch(Event event){
			if (sink == null){
				LOG.warn("The sink event handler is null");
				return;
			}
			
			sink.handle(event, System.currentTimeMillis());
		}
		
		@Override
		public void configure(Properties p){
			id = PropertiesConstants.getString(p, "id", "");
		}

		@Override
		public void close() throws Exception {
			// nothing to do
		}

		@Override
		public void report(Element xml) {
			if (xml != null){
				XmlTools.setString(xml,"module",getClass().getName());
			}
		}

		@Override
		public void report(Map<String, Object> json) {
			if (json != null){
				JsonTools.setString(json,"module",getClass().getName());
			}
		}
	}
}
