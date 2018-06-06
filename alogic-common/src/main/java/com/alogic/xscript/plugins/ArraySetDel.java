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
 * 删除集合成员
 * @author yyduan
 * @since 1.6.11.34
 */
public class ArraySetDel extends AbstractLogiclet {
	protected String pid = "$set";
	protected String $value = "";
	
	public ArraySetDel(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		pid = PropertiesConstants.getString(p,"pid",pid,true);
		$value = PropertiesConstants.getRaw(p,"member",$value);
	}
	
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		Set<String> list = ctx.getObject(pid);
		if (list != null){
			String value = PropertiesConstants.transform(ctx, $value, "");
			if (StringUtils.isNotEmpty(value)){
				list.remove(value);
			}
		}
	}	
}

