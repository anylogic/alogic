package com.alogic.event;

import org.apache.commons.lang3.StringUtils;
import com.alogic.event.xscript.SetProperties;
import com.alogic.event.xscript.SetProperty;
import com.alogic.metrics.Fragment;
import com.alogic.metrics.impl.DefaultFragment;
import com.alogic.metrics.stream.MetricsHandlerFactory;
import com.alogic.metrics.xscript.AddDimension;
import com.alogic.metrics.xscript.AddMeasure;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.plugins.Segment;
import com.anysoft.stream.Handler;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 事件发送插件
 * 
 * @author yyduan
 * @since 1.6.11.2
 */
public class EventSender extends Segment {
	/**
	 * 事件id
	 */
	protected String id = "";
	
	/**
	 * 事件类型
	 */
	protected String type = "";
	
	/**
	 * 上下文id
	 */
	protected String cid = "$event";
	
	public EventSender(String tag, Logiclet p) {
		super(tag, p);	
		
		registerModule("evt-property",SetProperty.class);
		registerModule("evt-properties",SetProperties.class);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		id = PropertiesConstants.getRaw(p,"id", "");
		cid = PropertiesConstants.getString(p,"cid",cid,true);
		type = PropertiesConstants.getRaw(p,"type",type);
	}
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		String eventType = ctx.transform(type);
		
		if (StringUtils.isNotEmpty(eventType)){
			Event e = EventBus.newEvent(ctx.transform(id), eventType);
			try{
				ctx.setObject(cid, e);
				super.onExecute(root, current, ctx, watcher);
				EventBus.getDefault().handle(e, e.getCreateTime());
			}finally{
				ctx.removeObject(cid);
			}
		}
	}

}
