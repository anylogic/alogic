package com.alogic.event.xscript;

import org.apache.commons.lang3.StringUtils;

import com.alogic.event.Event;
import com.alogic.event.EventBuilder;
import com.alogic.event.EventBus;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 设置属性
 * 
 * @author yyduan
 * @since 1.6.11.2
 * 
 */
public class SetProperty extends EventBuilder {
	protected String id = "";
	protected String value = "";
	protected String dft = "";
	protected boolean overwrite = true;
	
	public SetProperty(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		id = PropertiesConstants.getRaw(p,"id",id);
		value = PropertiesConstants.getRaw(p,"value",value);
		dft = PropertiesConstants.getRaw(p,"dft",dft);
		overwrite = PropertiesConstants.getBoolean(p, "overwrite", true);
	}

	@Override
	protected void onExecute(Event e, XsObject root, XsObject current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		String k = PropertiesConstants.transform(ctx, id, "");
		if (StringUtils.isNotEmpty(k)){
			String v = PropertiesConstants.transform(ctx, value, "");
			if (StringUtils.isEmpty(v)){
				v = PropertiesConstants.transform(ctx, dft, "");
			}
			
			if (StringUtils.isNotEmpty(v)){
				EventBus.setEventProperty(e, k, v, overwrite);
			}
		}
	}

}
