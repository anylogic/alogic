package com.alogic.lucene.core;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;

import com.anysoft.util.Configurable;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;


/**
 * Index库
 * 
 * @author duanyy
 * @since 1.6.4.1
 */
public interface Indexer extends Configurable,XMLConfigurable,Reportable{
	public Analyzer getAnalyzer();
	
	public Directory getDirectory();
	
	public Query newQuery(String field,String querystr);
	
	public IndexReader newReader();
}
