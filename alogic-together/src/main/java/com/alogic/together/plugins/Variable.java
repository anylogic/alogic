package com.alogic.together.plugins;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import com.alogic.together.AbstractLogiclet;
import com.alogic.together.ExecuteWatcher;
import com.alogic.together.Logiclet;
import com.alogic.together.LogicletContext;
import com.alogic.together.util.MapProperties;
import com.anysoft.selector.Selector;
import com.anysoft.selector.impl.Constants;
import com.anysoft.util.Properties;
import com.anysoft.util.XmlElementProperties;

public class Variable extends AbstractLogiclet {
	protected String id;
	protected Selector selector = null;
	
	public Variable(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Element e, Properties p) {
		Properties props = new XmlElementProperties(e,p);
		configure(props);
		
		id = e.getAttribute("id");
		selector = Selector.newInstanceWithDefault(e, p, Constants.class.getName());
		
		if (StringUtils.isEmpty(id) && selector != null){
			id = selector.getId();
		}
	}	

	@Override
	protected void onExecute(Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx, ExecuteWatcher watcher) {
		if (selector != null){
			MapProperties p = new MapProperties(current,ctx);
			ctx.SetValue(id, selector.onSelect(p));
		}
	}

}
