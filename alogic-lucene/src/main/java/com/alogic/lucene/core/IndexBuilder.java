package com.alogic.lucene.core;

import org.apache.lucene.index.IndexWriter;

import com.anysoft.util.Configurable;
import com.anysoft.util.XMLConfigurable;


/**
 * Index构建器
 * 
 * @author duanyy
 * 
 * @since 1.6.4.1
 * 
 */
public interface IndexBuilder extends Configurable,XMLConfigurable{
	
	/**
	 * 通过Writer来构建索引
	 * 
	 * @param writer IndexWriter
	 */
	public void build(IndexWriter writer);
}
