package com.anysoft.xscript;

import org.w3c.dom.Element;
import com.anysoft.util.Properties;

/**
 * Log语句
 * @author duanyy
 *
 */
public class Log extends AbstractStatement {
	protected String pattern;
	
	public Log(String xmlTag,Statement _parent) {
		super(xmlTag,_parent);
	}

	protected int compiling(Element _e, Properties _properties,CompileWatcher watcher){
		pattern = _e.getAttribute("msg");
		return 0;
	}
	
	public int onExecute(Properties p,ExecuteWatcher watcher) {
		logger.info(p.transform(pattern));
		return 0;
	}	
}
