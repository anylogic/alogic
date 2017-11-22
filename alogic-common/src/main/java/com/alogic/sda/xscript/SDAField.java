package com.alogic.sda.xscript;

import org.apache.commons.lang3.StringUtils;

import com.alogic.sda.SecretDataArea;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 基于SDA的Field操作
 * 
 * @author yyduan
 * @since 1.6.10.8
 */
public class SDAField extends AbstractLogiclet {
	protected String pid;	
	protected String id;
	protected String field;
	protected String dft;
	
	public SDAField(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		pid = PropertiesConstants.getRaw(p,"pid","$sda");
		id = PropertiesConstants.getRaw(p,"id","");
		field = PropertiesConstants.getRaw(p,"field","");
		dft = PropertiesConstants.getRaw(p,"dft","");
	}
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		String contextId = PropertiesConstants.transform(ctx, pid, "$sda");
		SecretDataArea sda = ctx.getObject(contextId);
		if (sda != null){
			onExecute(sda,root,current,ctx,watcher);
		}
	}

	protected void onExecute(SecretDataArea sda, XsObject root, XsObject current,
			LogicletContext ctx, ExecuteWatcher watcher){
		String varId = PropertiesConstants.transform(ctx, id, "");
		String fieldId = PropertiesConstants.transform(ctx, field, "");
		if (StringUtils.isNotEmpty(varId) && StringUtils.isNotEmpty(fieldId)){
			String dftValue = PropertiesConstants.transform(ctx, dft, "");
			ctx.SetValue(varId, sda.getField(fieldId, dftValue));
		}
	}

}
