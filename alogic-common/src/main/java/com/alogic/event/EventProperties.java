package com.alogic.event;

import com.anysoft.util.Properties;

/**
 * 将Event包裹成一个Properties
 * 
 * @author yyduan
 *
 */
public class EventProperties extends Properties {
	
	/**
	 * 被包裹的event
	 */
	protected Event event = null;
	
	public EventProperties(Event event,Properties parent){
		super("default",parent);
	}
	
	@Override
	protected void _SetValue(String _name, String _value) {
		if (event != null){
			event.setProperty(_name, _value, true);
		}
	}

	@Override
	protected String _GetValue(String _name) {
		return event != null ? event.getProperty(_name, null) : null;
	}

	@Override
	public void Clear() {
		//无法清除event的属性
	}

}
