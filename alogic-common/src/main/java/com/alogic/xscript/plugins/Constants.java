package com.alogic.xscript.plugins;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 设置一个常量到上下文环境
 * 
 * @author duanyy
 * @version 1.6.5.26 <br>
 * - 增加设置变量模版功能 <br>
 */
public class Constants extends AbstractLogiclet {
	protected String id;
	protected String value;
	
	public Constants(String tag, Logiclet p) {
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
		if (StringUtils.isNotEmpty(id) && StringUtils.isNotEmpty(value)){
			ctx.SetValue(id, value);
		}
	}

}
