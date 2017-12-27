package com.alogic.uid.naming;

import com.alogic.naming.context.XmlInner;
import com.alogic.uid.IdGenerator;
import com.alogic.uid.impl.Simple;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * XML内部的context
 * 
 * @author yyduan
 * @since 1.6.11.5
 */
public class Inner extends XmlInner<IdGenerator>{
	protected String dftClass = Simple.class.getName();
	
	@Override
	public String getObjectName() {
		return "uid";
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
