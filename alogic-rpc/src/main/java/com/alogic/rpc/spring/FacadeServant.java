package com.alogic.rpc.spring;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.FactoryBean;

import com.alogic.rpc.facade.FacadeFactory;

public class FacadeServant<T> implements FactoryBean<T> {
	private Class<T> _interface;

	private FacadeFactory facadeFactory = FacadeFactory.DEFAULT;

	private String _callId;
	
	private String _beanId;
	
	public void setFacadeFactory(FacadeFactory facadeFactory) {
		this.facadeFactory = facadeFactory;
	}

	public void setCallId(String callId){
		this._callId = callId;
	}
	
	public void setBeanId(String beanId){
		this._beanId = beanId;
	}
	
	public void setInterface(Class<T> _interface) {
		this._interface = _interface;
	}

	@Override
	public T getObject() throws Exception {
		return StringUtils.isEmpty(_beanId)?
				facadeFactory.getInterface(_callId,_interface):
				facadeFactory.getInterface(_callId, _beanId, _interface);
	}

	@Override
	public Class<?> getObjectType() {
		return this._interface;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
