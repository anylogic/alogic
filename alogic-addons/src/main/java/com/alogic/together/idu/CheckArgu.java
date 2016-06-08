package com.alogic.together.idu;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alogic.together.AbstractLogiclet;
import com.alogic.together.ExecuteWatcher;
import com.alogic.together.Logiclet;
import com.alogic.together.LogicletContext;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.backend.ServantException;

public class CheckArgu extends AbstractLogiclet{
	protected String arguId;	
	protected String code = "client.args_not_found";
	protected String reason = "Can not find parameter:%s";
	public CheckArgu(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p) {
		super.configure(p);
		
		arguId = PropertiesConstants.getString(p,"id", arguId);
		code = PropertiesConstants.getString(p,"code", code);
		reason = PropertiesConstants.getString(p,"reason", reason);
	}		
	
	@Override
	protected void onExecute(Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		if (StringUtils.isNotEmpty(arguId)){
			String value = ctx.GetValue(arguId, "");
			if (StringUtils.isEmpty(value)){
				throw new ServantException(code,String.format(reason,arguId));
			}
		}
	}

}
