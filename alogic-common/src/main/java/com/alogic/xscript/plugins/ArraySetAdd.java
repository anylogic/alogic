package com.alogic.xscript.plugins;

import org.apache.commons.lang3.StringUtils;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import java.util.Set;

/**
 * 新增集合成员
 * @author yyduan
 * @since 1.6.11.34
 */
public class ArraySetAdd extends AbstractLogiclet {
	protected String pid = "$set";
	protected String $value = "";
	
	public ArraySetAdd(String tag, Logiclet p) {
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
				list.add(value);
			}
		}
	}	
}
