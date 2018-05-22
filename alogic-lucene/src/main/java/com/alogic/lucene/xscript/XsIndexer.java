package com.alogic.lucene.xscript;

import org.apache.commons.lang3.StringUtils;

import com.alogic.lucene.client.IndexerTool;
import com.alogic.lucene.core.Indexer;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 打开一个基于lucene的Indexer
 * @author yyduan
 * @since 1.6.11.31
 */
public class XsIndexer extends NS{
	/**
	 * indexer的id
	 */
	protected String indexerId = "default";
	
	/**
	 * 当前对象id
	 */
	protected String cid = "$indexer";
	
	public XsIndexer(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		indexerId = PropertiesConstants.getString(p,"indexerId",indexerId,true);
		cid = PropertiesConstants.getString(p,"cid",cid,true);
	}
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		if (StringUtils.isEmpty(indexerId)){
			throw new BaseException("core.e1003","The relational lucene indexer is not defined");
		}
		
		Indexer indexer = IndexerTool.getIndexer(indexerId);
		
		if (indexer == null){
			throw new BaseException("core.e1003","The lucene indexer is not found,id=" + indexerId);
		}
		
		ctx.setObject(cid, indexer);
		try {
			super.onExecute(root, current, ctx, watcher);
		}finally{
			ctx.removeObject(cid);
		}		
	}
}
