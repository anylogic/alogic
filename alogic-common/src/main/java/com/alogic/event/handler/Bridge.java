package com.alogic.event.handler;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import com.alogic.event.Event;
import com.alogic.event.EventServer;
import com.anysoft.stream.Handler;
import com.anysoft.stream.SlideHandler;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlElementProperties;

/**
 * 桥接器
 * @author yyduan
 * @since 1.6.11.26
 * 
 * @version 1.6.11.27 [20180417 duanyy] <br>
 * - 修正事件处理器Bridge的初始化问题 <br>
 */
public class Bridge extends SlideHandler<Event>{	
	protected String esId;
	protected EventServer es;
	@Override
	protected void onConfigure(Element e, Properties p) {
		super.onConfigure(e, p);
		
		Properties props = new XmlElementProperties(e,p);
		
		esId = PropertiesConstants.getString(props, "esId", esId);
	}

	@Override
	protected void onHandle(Event evt, long timestamp) {
		if (es != null){
			es.handle(evt, timestamp);
		}else{
			if (StringUtils.isNotEmpty(esId)){
				Object found = Settings.get().get(esId);
				if (found instanceof EventServer){
					es = (EventServer)found;
				}else{
					LOG.error(String.format("Can not find event server %s", esId));
				}
			}else{
				LOG.error("Parameter esId is not set..");
			}
			
			if (es != null){
				es.handle(evt, timestamp);
			}else{
				evt.setProperty("$code", "core.e1003", true);
				evt.setProperty("$reason", String.format("Can not find event server %s",esId), true);
			}
		}
		Handler<Event> dftHandler = this.getSlidingHandler();			
		if (dftHandler != null){
			dftHandler.handle(evt, timestamp);
		}
	}

}
