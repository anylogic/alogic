package com.anysoft.xscript;

import org.w3c.dom.Element;

import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;

/**
 * Log语句
 * @author duanyy
 * @version 1.6.3.25 <br>
 * - 统一脚本的日志处理机制 <br>
 */
public class Log extends AbstractStatement {
	protected String pattern;
	protected String level;
	protected int progress = -2;
	
	public Log(String xmlTag,Statement _parent) {
		super(xmlTag,_parent);
	}

	protected int compiling(Element _e, Properties _properties,CompileWatcher watcher){
		XmlElementProperties p = new XmlElementProperties(_e,_properties);
		
		pattern = p.GetValue("msg", "", false, true);
		level = p.GetValue("msg", "info", false, true);
		progress = PropertiesConstants.getInt(p,"progress", -2);
		return 0;
	}
	
	public int onExecute(Properties p,ExecuteWatcher watcher) {
		log(p.transform(pattern),level,progress);
		return 0;
	}	
}
