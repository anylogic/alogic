package com.alogic.xscript.plugins;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 将array保存到变量中
 * @author yyduan
 * @since 1.6.11.60 [20180912 duanyy]
 */
public class ArraySetSave extends AbstractLogiclet {
	protected String pid = "$set";
	protected String id="";
	protected String delimeter = ",";
	
	public ArraySetSave(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		pid = PropertiesConstants.getString(p,"pid",pid,true);
		delimeter = PropertiesConstants.getRaw(p,"delimeter",delimeter);
		id = PropertiesConstants.getString(p,"id","$" + this.getXmlTag(),true);
	}
	
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		Set<String> list = ctx.getObject(pid);
		if (list != null){
			if (StringUtils.isNotEmpty(id)){
				ctx.SetValue(id, StringUtils.join(list, delimeter));
			}
		}
	}	
}
