package com.alogic.lucene.indexer;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import com.alogic.lucene.core.Indexer;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;


/**
 * 基于RAMDirectory的Indexer
 * 
 * @author duanyy
 *
 */
public class RAM extends Indexer.Abstract{
	/**
	 * dircectory
	 */
	protected Directory index = new RAMDirectory();
	
	public Directory getDirectory() {
		return index;
	}
	
	public void configure(Properties p) throws BaseException {
		super.configure(p);
		
		build(true);
	}
}
