package com.alogic.xscript.plugins;

import java.util.Iterator;
import java.util.Set;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 遍历所有值
 * @author yyduan
 * @since 1.6.11.43
 */
public class ArraySetList extends Segment {
	protected String pid = "$set";
	protected String id = "$value";
	
	public ArraySetList(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		pid = PropertiesConstants.getString(p,"pid",pid,true);
		id = PropertiesConstants.getString(p,"id",id,true);
	}
	
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		Set<String> list = ctx.getObject(pid);
		if (list != null){
			Iterator<String> iter = list.iterator();
			
			while (iter.hasNext()){
				String v = iter.next();
				ctx.SetValue(id, v);
				super.onExecute(root, current, ctx, watcher);
			}
		}
	}	
}
