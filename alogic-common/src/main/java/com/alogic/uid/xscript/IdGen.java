package com.alogic.uid.xscript;

import org.apache.commons.lang3.StringUtils;

import com.alogic.uid.IdTool;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * IdGenerator的脚本插件
 * @author yyduan
 * @since 1.6.11.5
 */
public class IdGen extends AbstractLogiclet {
	protected String id;
	protected String domain = "default";
	
	public IdGen(String tag, Logiclet p) {
		super(tag, p);
	}

	public void configure(Properties p){
		super.configure(p);
		
		id = PropertiesConstants.getString(p,"id","",true);
		domain = PropertiesConstants.getString(p,"domain","domain",true);
	}

	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		if (StringUtils.isNotEmpty(id)){
			ctx.SetValue(id, IdTool.nextId(domain));
		}
	}

}