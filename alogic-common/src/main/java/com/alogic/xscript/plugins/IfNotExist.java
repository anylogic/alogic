package com.alogic.xscript.plugins;

import org.apache.commons.lang3.StringUtils;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 如果某个变量不存在，则执行
 * @author yyduan
 *
 * @since 1.6.10.9
 */
public class IfNotExist extends Segment{
	protected String id = "";
	
	public IfNotExist(String tag, Logiclet p) {
		super(tag, p);
	}	
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		id = PropertiesConstants.getRaw(p,"id",id);
	}

	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		String varId = PropertiesConstants.transform(ctx, id,"");
		if (StringUtils.isNotEmpty(varId)){
			String value = PropertiesConstants.getString(ctx,varId,"");
			if (StringUtils.isEmpty(value)){
				super.onExecute(root, current, ctx, watcher);
			}
		}
	}	
}