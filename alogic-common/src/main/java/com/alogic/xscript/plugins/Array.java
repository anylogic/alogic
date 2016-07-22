package com.alogic.xscript.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 在当前文档增加一个数组
 * 
 * @author duanyy
 *
 */
public class Array extends Segment {
	protected String tag = "data";
	protected String id = "$array";
	
	public Array(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		id = PropertiesConstants.getString(p,"id",id,true);
		tag = PropertiesConstants.getRaw(p,"tag", tag);
	}
	
	protected void onExecute(Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx, ExecuteWatcher watcher) {
		String tagValue = ctx.transform(tag);
		if (StringUtils.isNotEmpty(tagValue)){
			List<Object> template = new ArrayList<Object>();
			current.put(tagValue, template);
			
			try {
				ctx.setObject(id, template);
				super.onExecute(root, current, ctx, watcher);
			}finally{
				ctx.removeObject(id);
			}
		}
	}	
}
