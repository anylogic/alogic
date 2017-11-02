package com.alogic.zk.xscript;

import org.apache.commons.lang3.StringUtils;

import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.zk.ZooKeeperConnector;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 将带有/的路径转化为可以在zk路径中使用的id
 * 
 * @author yyduan
 *
 */
public class ZKEscapePath extends AbstractLogiclet{
	protected String id;
	protected String value;
	
	public ZKEscapePath(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		id = PropertiesConstants.getString(p,"id","",true);
		value = PropertiesConstants.getRaw(p,"value","");
	}	

	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		if (StringUtils.isNotEmpty(id)){
			String v = ctx.transform(value);
			ctx.SetValue(id, ZooKeeperConnector.escapePath(v));
		}
	}
}
