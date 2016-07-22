package com.alogic.xscript.plugins;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.util.MapProperties;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 从上下文变量中获取变量值，并设置到当前节点上
 * 
 * @author duanyy
 *
 */
public class Get extends AbstractLogiclet {
	protected String id;
	protected String value;
	
	public Get(String tag, Logiclet p) {
		super(tag, p);
	}

	public void configure(Properties p){
		super.configure(p);
		
		id = PropertiesConstants.getRaw(p,"id","");
		value = p.GetValue("value", "", false, true);
	}

	@Override
	protected void onExecute(Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx, ExecuteWatcher watcher) {
		MapProperties p = new MapProperties(current,ctx);
		String idValue = p.transform(id);
		if (StringUtils.isNotEmpty(idValue)){
			current.put(idValue, p.transform(value));
		}
	}

}
