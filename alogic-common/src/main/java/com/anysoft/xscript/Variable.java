package com.anysoft.xscript;

import org.w3c.dom.Element;

import com.anysoft.selector.Selector;
import com.anysoft.selector.impl.Constants;
import com.anysoft.util.Properties;

/**
 * var语句
 * 
 * @author duanyy
 *
 */
public class Variable extends AbstractStatement {
	protected String id;
	protected Selector selector = null;
	
	public Variable(String _tag, Statement _parent) {
		super(_tag, _parent);
	}

	protected int compiling(Element _e, Properties _properties,CompileWatcher watcher) {
		id = _e.getAttribute("id");
		selector = Selector.newInstanceWithDefault(_e, _properties, Constants.class.getName());
		if (selector == null){
			if (watcher != null){
				watcher.message(this, "error", "Can not create selector.tag=" + getXmlTag());
			}
		}
		return 0;
	}

	protected int onExecute(Properties p, ExecuteWatcher watcher) {
		if (selector == null){
			return -1;
		}
		
		String _id = id;
		if (isNull(_id)){
			_id = selector.getId();
		}
		
		if (isNull(_id)){
			return -1;
		}
		
		String value = selector.select(p);
		if (! isNull(value)){
			p.SetValue(_id, value);
		}
		return 0;
	}
	
	protected boolean isNull(String value){
		return value == null || value.length() <= 0;
	}
}
