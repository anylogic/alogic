package com.alogic.rpc.spring;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

import com.logicbus.backend.ServantException;

public class SpringObjectFactory {
	private SpringObjectFactory() {
	}

	private static ApplicationContext applicationContext;

	@SuppressWarnings("unchecked")
	public static <T> T getBean(String id, Class<T> cl) {
		if (applicationContext == null) {
			throw new ServantException("core.instance_failed", "Spring object factory not initialized!");
		}
		if (StringUtils.isNotEmpty(id)) {
			try {
				return (T) applicationContext.getBean(id);
			} catch (Exception e) {
				throw new ServantException("core.instance_failed", "Can't find bean with id: " + id);
			}
		}
		try {
			return applicationContext.getBean(cl);
		} catch (Exception e) {
			throw new ServantException("core.instance_failed", "Can't find bean with interface: " + cl);
		}
	}

	public static void setApplicationContext(ApplicationContext applicationContext) {
		SpringObjectFactory.applicationContext = applicationContext;
	}

}
