package com.alogic.pool.context;

import com.alogic.naming.context.XmlOutter;
import com.alogic.pool.Pool;
import com.alogic.pool.impl.Default;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * XmlOutter 
 * 
 * @author duanyy
 * @since 1.6.6.8
 * 
 */
public class Outter extends XmlOutter<Pool> {
	protected String dftClass = Default.class.getName();
	
	@Override
	public void configure(Properties p) {
		dftClass = PropertiesConstants.getString(p,"dftClass",dftClass);
	}

	@Override
	public String getObjectName() {
		return "pool";
	}

	@Override
	public String getDefaultClass() {
		return dftClass;
	}

	@Override
	public String getDefaultXrc() {
		return "java:///com/alogic/pool/naming.pool.context.xml#" + Outter.class.getName();
	}

}
