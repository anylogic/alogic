package com.anysoft.xscript;

import org.w3c.dom.Element;

import com.anysoft.selector.Selector;
import com.anysoft.selector.impl.Constants;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;

/**
 * choose语句
 * 
 * @author duanyy
 * @since 1.6.3.22
 */
public class Choose extends Block {
	protected Selector selector = null;
	public Choose(String _tag, Statement _parent) {
		super(_tag, _parent);
	}

	public void configure(Element _e, Properties _properties)
			throws BaseException {	
		selector = Selector.newInstanceWithDefault(_e, _properties, Constants.class.getName());
		if (selector == null){
			logger.error("Can not create selector.tag=" + getXmlTag());
		}
		super.configure(_e, _properties);
	}

	int onExecute(Properties p, ExecuteWatcher watcher) throws BaseException {
		if (children.size() <= 0){
			return -1;
		}
		
		boolean result = true;
		if (selector != null){
			String value = selector.select(p);
			result = !value.equals("false");
		}
		
		if (result){
			Statement stmt = children.get(0);
			stmt.execute(p, watcher);
		}else{
			if (children.size() > 1){
				Statement stmt = children.get(1);
				stmt.execute(p, watcher);
			}
		}
		return 0;
	}

	@Override
	void onConfigure(Element _e, Properties p) {
	}
}
