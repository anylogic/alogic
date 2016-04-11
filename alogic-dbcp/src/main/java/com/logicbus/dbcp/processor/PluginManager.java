package com.logicbus.dbcp.processor;

import com.anysoft.formula.Expression;
import com.anysoft.formula.FunctionHelper;

/**
 * 插件管理器
 * 
 * @author duanyy
 * @since 1.6.3.30
 * 
 */
public class PluginManager implements FunctionHelper {
	protected BindedListener bindedListener = null;
	
	public PluginManager(BindedListener listener){
		bindedListener = listener;
	}
	
	public Expression customize(String funcName) {
		if (funcName.equals("not_nvl")){
			return new Plugin.NotNull(funcName,bindedListener);
		}
		
		if (funcName.equals("bind")){
			return new Plugin.Bind(funcName,bindedListener);
		}
		
		if (funcName.equals("bind_raw")){
			return new Plugin.BindRaw(funcName, bindedListener);
		}
		
		if (funcName.equals("uuid")){
			return new Plugin.UUId(funcName, bindedListener);
		}
		
		return null;
	}

}
