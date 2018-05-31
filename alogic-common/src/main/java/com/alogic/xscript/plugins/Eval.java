package com.alogic.xscript.plugins;

import org.apache.commons.lang3.StringUtils;

import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.Script;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 动态执行
 * @author yyduan
 *
 */
public class Eval extends AbstractLogiclet{
	protected String contentId = "";
	
	public Eval(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		contentId = PropertiesConstants.getRaw(p,"id",contentId);
	}
		
	@Override
	protected void onExecute(XsObject root,XsObject current,LogicletContext ctx, ExecuteWatcher watcher) {
		String content = PropertiesConstants.getRaw(ctx,contentId,"");
		if (StringUtils.isNotEmpty(content)){
			Script script = Script.createFromContent(content, ctx, true);
			if (script != null){
				script.execute(root, current, ctx, watcher);
			}else{
				logger.warn("The script is null:" + content);
			}
		}
	}	
}
