package com.logicbus.dbcp.processor;

import com.anysoft.formula.Function;

/**
 * 插件
 * @author duanyy
 *
 */
abstract public class Plugin extends Function {
	protected BindedListener bindedListener = null;
	
	public Plugin(String name,BindedListener listener){
		super(name);
		bindedListener = listener;
	}
	
	public void bind(Object value){
		if (bindedListener != null){
			bindedListener.bind(value);
		}
	}
}
