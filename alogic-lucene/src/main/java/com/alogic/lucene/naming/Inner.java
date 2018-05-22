package com.alogic.lucene.naming;

import com.alogic.lucene.core.Indexer;
import com.alogic.lucene.indexer.RAM;
import com.alogic.naming.context.XmlInner;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * Inner实现
 * 
 * @author yyduan
 * @since 1.6.11.31
 */
public class Inner extends XmlInner<Indexer>{
	protected String dftClass = RAM.class.getName();
	
	@Override
	public String getObjectName() {
		return "indexer";
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
