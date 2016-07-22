package com.alogic.xscript.plugins;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.Properties;

/**
 * 在当前文档增加一个对象
 * 
 * @author duanyy
 *
 */
public class Obj extends Segment {
	protected String tag = "data";
	
	public Obj(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		tag = p.GetValue("tag", tag, false, true);
	}
	
	protected void onExecute(Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx, ExecuteWatcher watcher) {
		String tagValue = ctx.transform(tag);
		if (StringUtils.isNotEmpty(tagValue)){
			Map<String,Object> template = new HashMap<String,Object>();
			current.put(tagValue, template);
			super.onExecute(root, (Map<String,Object>)template, ctx, watcher);
		}
	}	
}
