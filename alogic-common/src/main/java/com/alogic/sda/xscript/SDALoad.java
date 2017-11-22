package com.alogic.sda.xscript;

import org.apache.commons.lang3.StringUtils;

import com.alogic.load.Loader;
import com.alogic.sda.SDAFactory;
import com.alogic.sda.SecretDataArea;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.plugins.Segment;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 装入指定id的SDA
 * 
 * @author yyduan
 * @since 1.6.10.8
 */
public class SDALoad extends Segment {
	protected String cid;
	protected String sdaId;
	protected String cacheAllow = "true";
	
	public SDALoad(String tag, Logiclet p) {
		super(tag, p);
		
		registerModule("sda",SDALoad.class);
		registerModule("sda-field", SDAField.class);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		cid = PropertiesConstants.getRaw(p,"cid","$sda");
		sdaId = PropertiesConstants.getRaw(p, "id", "");
		cacheAllow = PropertiesConstants.getRaw(p, "cacheAllow", cacheAllow);
	}
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		String theId = PropertiesConstants.transform(ctx, sdaId, "");
		String contextId = PropertiesConstants.transform(ctx, cid, "$sda");
		
		if (StringUtils.isNotEmpty(theId)){
			Loader<SecretDataArea> loader = SDAFactory.getDefault();
			SecretDataArea sda = loader.load(theId,PropertiesConstants.transform(ctx, cacheAllow, true));
			if (sda != null){
				try {
					ctx.setObject(contextId, sda);
					super.onExecute(root, current, ctx, watcher);
				}finally{
					ctx.removeObject(contextId);
				}
			}else{
				log("Can not find sda :" + theId,"warning");
			}
		}else{
			log("The sda id is null");
		}
	}
}
