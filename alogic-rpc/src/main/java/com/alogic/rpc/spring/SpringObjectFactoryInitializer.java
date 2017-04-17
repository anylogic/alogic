package com.alogic.rpc.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * SpringObjectFactory初始化器
 * <p>
 * 用于给SpringObjectFactory类设置静态属性Spring容器
 * </p>
 * 
 * @author xiongkw
 *
 */
public class SpringObjectFactoryInitializer implements ApplicationContextAware {

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringObjectFactory.setApplicationContext(applicationContext);
	}

}
