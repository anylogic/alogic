package com.alogic.xscript.plugins;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 为数组增加子项
 * 
 * @author duanyy
 *
 */
public class ArrayItem extends Segment {
	protected String id = "$array";
	
	public ArrayItem(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		id = PropertiesConstants.getString(p,"id",id,true);
	}
	
	protected void onExecute(Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx, ExecuteWatcher watcher) {
		List<Object> list = ctx.getObject(id);
		if (list != null){
			Map<String,Object> template = new HashMap<String,Object>();
			list.add(template);
			super.onExecute(root, (Map<String,Object>)template, ctx, watcher);			
		}
	}	
}