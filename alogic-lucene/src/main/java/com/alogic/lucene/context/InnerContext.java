package com.alogic.lucene.context;

import com.alogic.lucene.core.Indexer;
import com.alogic.lucene.indexer.RAM;
import com.anysoft.context.Inner;


/**
 * Inner Context of Indexer
 * 
 * @author duanyy
 * @since 1.6.4.1
 * 
 */
public class InnerContext extends Inner<Indexer>{

	public String getObjectName() {
		return "indexer";
	}

	public String getDefaultClass() {
		return RAM.class.getName();
	}

}
