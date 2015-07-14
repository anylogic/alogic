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
	
	@Override
	public Expression customize(String funcName) {
		if (funcName.equals("not_nvl")){
			return new NotNull(funcName,bindedListener);
		}
		
		if (funcName.equals("value")){
			return new Value(funcName,bindedListener);
		}
		
		return null;
	}

}
