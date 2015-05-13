package com.anysoft.xscript;

import java.util.List;

import org.w3c.dom.Element;

import com.anysoft.util.Properties;

/**
 * Segment语句
 * 
 * @author duanyy
 * @since 1.6.3.22
 * 
 */
public class Segment extends Block {
	public Segment(String xmlTag,Statement _parent) {
		super(xmlTag,_parent);
	}

	protected int onExecute(Properties p, ExecuteWatcher watcher) {
		List<Statement> _children = children;
		Properties variables = getLocalVariables(p);
		
		for (int i = 0 ; i < _children.size(); i ++){
			Statement statement = _children.get(i);
			statement.execute(variables,watcher);
		}
		return 0;
	}

	protected int onCompiling(Element _e, Properties p, CompileWatcher watcher) {
		return 0;
	}
}
