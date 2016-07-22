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
 * 将取值全部转为小写，并设置为变量
 * @author duanyy
 *
 */
public class Uppercase extends AbstractLogiclet {
	protected String id;
	protected String value;
	
	public Uppercase(String tag, Logiclet p) {
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
			MapProperties p = new MapProperties(current,ctx);
			ctx.SetValue(id, p.transform(value).toUpperCase());
		}
	}

}
