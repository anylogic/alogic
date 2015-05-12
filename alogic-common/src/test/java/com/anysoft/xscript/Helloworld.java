package com.anysoft.xscript;

import org.w3c.dom.Element;

import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 插件：Helloworld
 * 
 * @author duanyy
 * @since 1.6.3.22
 */
public class Helloworld extends AbstractStatement {

	public Helloworld(String _tag, Statement _parent) {
		super(_tag, _parent);
	}

	@Override
	public void configure(Element _e, Properties _properties)
			throws BaseException {
	}

	@Override
	protected int onExecute(Properties p, ExecuteWatcher watcher) throws BaseException {
		logger.info("java.vm.name=" + PropertiesConstants.getString(p,"java.vm.name",""));
		logger.info("java.vm.version=" + PropertiesConstants.getString(p,"java.vm.version",""));
		logger.info("java.vm.vendor=" + PropertiesConstants.getString(p,"java.vm.vendor",""));
		return 0;
	}
}
