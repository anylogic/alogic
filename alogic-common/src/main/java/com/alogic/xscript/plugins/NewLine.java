package com.alogic.xscript.plugins;

import org.apache.commons.lang3.StringUtils;

import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * NewLine
 * @author yyduan
 * @since 1.6.11.27
 */
public class NewLine extends AbstractLogiclet {
	/**
	 * 变量id
	 */
	protected String id;

	/**
	 * 是否unix风格
	 */
	protected boolean unix = true;

	public NewLine(String tag, Logiclet p) {
		super(tag, p);
	}

	public void configure(Properties p){
		super.configure(p);
		
		id = PropertiesConstants.getString(p,"id","",true);
		unix = PropertiesConstants.getBoolean(p,"unix",unix,true);
	}

	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		if (StringUtils.isNotEmpty(id)){
			if (unix){
				ctx.SetValue(id, "\n");
			}else{
				ctx.SetValue(id, "\r\n");
			}
		}
	}

}
