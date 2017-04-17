package com.alogic.rpc.spring;

import org.apache.commons.lang3.StringUtils;

import com.alogic.rpc.call.local.LocalCall;
import com.anysoft.util.Settings;

public class SpringCall extends LocalCall {

	@Override
	protected Object createInstance(String id)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		String beanId = null;
		Class<?> _interface = null;
		if (StringUtils.contains(id, ".")) {
			_interface = Settings.getClassLoader().loadClass(id);
		} else {
			beanId = id;
		}
		return SpringObjectFactory.getBean(beanId, _interface);
	}

}
