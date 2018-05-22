package com.alogic.lucene.indexer;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import com.alogic.lucene.core.Indexer;

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
	
}
