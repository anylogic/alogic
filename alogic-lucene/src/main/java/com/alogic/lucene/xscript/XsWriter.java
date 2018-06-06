package com.alogic.lucene.xscript;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.IndexWriter;

import com.alogic.lucene.client.IndexerTool;
import com.alogic.lucene.core.Indexer;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.BaseException;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 打开一个IndexerWriter
 * 
 * @author yyduan
 * @since 1.6.11.31
 */
public class XsWriter extends NS{
	/**
	 * 父节点的上下文id
	 */
	protected String pid = "$indexer";
	
	/**
	 * 当前节点的上下文id
	 */
	protected String cid = "$indexer-writer";
	
	/**
	 * 当不存在父节点时自动连接indexer
	 */
	protected String indexerId = "";
	
	protected String $rebuild = "false";
	
	public XsWriter(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		pid = PropertiesConstants.getString(p,"pid",pid,true);
		cid = PropertiesConstants.getString(p,"cid",cid,true);
		indexerId = PropertiesConstants.getString(p,"indexerId",indexerId,true);
		$rebuild = PropertiesConstants.getRaw(p,"rebuild",$rebuild);
	}
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		Indexer indexer = ctx.getObject(pid);
		if (indexer == null){
			if (StringUtils.isNotEmpty(indexerId)){
				indexer = IndexerTool.getIndexer(indexerId);
			}
			if (indexer == null){
				throw new BaseException("core.e1001","It must be in a lucene context,check your together script.");
			}
		}
		
		IndexWriter writer = indexer.newWriter(PropertiesConstants.transform(ctx, $rebuild, false));
		try {
			ctx.setObject(cid, writer);
			super.onExecute(root, current, ctx, watcher);
		}finally{
			ctx.removeObject(cid);
			IOTools.close(writer);
		}	
	}	
}
