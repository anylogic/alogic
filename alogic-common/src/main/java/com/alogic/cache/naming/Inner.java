package com.alogic.cache.naming;

import com.alogic.cache.CacheObject;
import com.alogic.cache.LocalCacheStore;
import com.alogic.load.Store;
import com.alogic.naming.context.XmlInner;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * XML内部的context
 * 
 * @author yyduan
 * 
 * @since 1.6.11.6
 */
public class Inner extends XmlInner<Store<CacheObject>>{
	protected String dftClass = LocalCacheStore.class.getName();
	
	@Override
	public String getObjectName() {
		return "cache";
	}

	@Override
	public String getDefaultClass() {
		return dftClass;
	}

	@Override
	public void configure(Properties p) {
		dftClass = PropertiesConstants.getString(p,"dftClass",dftClass);
	}

}