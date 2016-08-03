package com.alogic.xscript.plugins;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.EnvProperties;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 从env中获取指定的值，并设置为变量
 * 
 * @author duanyy
 *
 */
public class FromEnv extends AbstractLogiclet {
	protected String id;
	protected String value;
	protected static Properties env = new EnvProperties();
	
	public FromEnv(String tag, Logiclet p) {
		super(tag, p);
	}

	public void configure(Properties p){
		super.configure(p);
		
		id = PropertiesConstants.getString(p,"id","",true);
		value = p.GetValue("value", "", false, true);
	}

	@Override
	protected void onExecute(Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx, ExecuteWatcher watcher) {
		if (StringUtils.isNotEmpty(id)){
			ctx.SetValue(id, env.transform(value));
		}
	}

}
