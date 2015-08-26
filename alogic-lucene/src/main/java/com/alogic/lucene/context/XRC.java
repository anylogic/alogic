package com.alogic.lucene.context;

import com.alogic.lucene.core.Indexer;
import com.alogic.lucene.indexer.RAM;
import com.anysoft.context.XMLResource;

/**
 * XRC of Indexer
 * 
 * @author duanyy
 * @since 1.6.4.1
 * 
 */
public class XRC extends XMLResource<Indexer>{

	public String getObjectName() {
		return "indexer";
	}

	public String getDefaultClass() {
		return RAM.class.getName();
	}

	public String getDefaultXrc() {
		return "java:///com/alogic/lucene/context/indexer.default.xml#com.alogic.lucene.context.XRC";
	}

}
