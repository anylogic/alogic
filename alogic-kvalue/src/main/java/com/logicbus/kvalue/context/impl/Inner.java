package com.logicbus.kvalue.context.impl;

import java.util.Map;

import org.w3c.dom.Element;

import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.Watcher;
import com.logicbus.kvalue.context.KValueContext;
import com.logicbus.kvalue.context.SchemaHolder;
import com.logicbus.kvalue.core.Schema;

public class Inner implements KValueContext {
	protected SchemaHolder holder = new SchemaHolder();
	
	public void close() throws Exception {
		if (holder != null){
			holder.close();
		}
	}

	
	public void configure(Element _e, Properties _properties)
			throws BaseException {
		if (holder != null){
			holder.configure(_e, _properties);
		}
	}

	
	public void report(Element xml) {
		if (holder != null){
			holder.report(xml);
		}
	}

	
	public void report(Map<String, Object> json) {
		if (holder != null){
			holder.report(json);
		}
	}

	
	public Schema getSchema(String id) {
		return holder != null ? holder.getSchema(id):null;
	}

	
	public void addWatcher(Watcher<Schema> watcher) {
		// do nothing
	}

	
	public void removeWatcher(Watcher<Schema> watcher) {
		// do nothing
	}

}
