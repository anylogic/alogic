package com.alogic.pool.context;

import com.alogic.naming.context.XmlInner;
import com.alogic.pool.Pool;
import com.alogic.pool.impl.Default;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * XmlInner
 * 
 * @author duanyy
 * @since 1.6.6.8
 * 
 */
public class Inner extends XmlInner<Pool> {
	protected String dftClass = Default.class.getName();
	
	@Override
	public String getObjectName() {
		return "pool";
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
