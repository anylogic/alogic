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
import com.anysoft.util.Settings;

/**
 * 动态执行
 * @author yyduan
 * 
 * @version 1.6.11.44 [20180713 duanyy] <br>
 * - 在脚本编译时采用Settings作为变量集 <br>
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
			Script script = Script.createFromContent(content, Settings.get(), true);
			if (script != null){
				script.execute(root, current, ctx, watcher);
			}else{
				logger.warn("The script is null:" + content);
			}
		}
	}	
}
