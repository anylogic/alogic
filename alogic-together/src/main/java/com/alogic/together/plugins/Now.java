package com.alogic.together.plugins;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alogic.together.AbstractLogiclet;
import com.alogic.together.ExecuteWatcher;
import com.alogic.together.Logiclet;
import com.alogic.together.LogicletContext;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 将当前时间(毫米数设置到上下文变量)
 * 
 * @author duanyy
 *
 */
public class Now extends AbstractLogiclet {
	protected String id;
	
	public Now(String tag, Logiclet p) {
		super(tag, p);
	}

	public void configure(Properties p){
		super.configure(p);
		
		id = PropertiesConstants.getString(p,"id","",true);
	}

	@Override
	protected void onExecute(Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx, ExecuteWatcher watcher) {
		if (StringUtils.isNotEmpty(id)){
			ctx.SetValue(id, String.valueOf(System.currentTimeMillis()));
		}
	}

}
