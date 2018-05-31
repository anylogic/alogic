package com.alogic.lucene.xscript;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import com.alogic.lucene.client.IndexerTool;
import com.alogic.lucene.core.Indexer;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.plugins.Segment;
import com.anysoft.util.BaseException;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 分词工具
 * 
 * @author yyduan
 *
 */
public class XsSegment extends Segment{
	/**
	 * 父节点的上下文id
	 */
	protected String pid = "$indexer";
	
	/**
	 * 当不存在父节点时自动连接indexer
	 */
	protected String indexerId = "";
	
	/**
	 * 待分析的文本
	 */
	protected String $text = "";
	
	protected String $field = "default";
	
	protected String valueId = "$lucene-value";
	protected String typeId = "$lucene-type";
	protected String startId = "$lucene-start";
	protected String endId = "$lucene-end";
	
	public XsSegment(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		pid = PropertiesConstants.getString(p,"pid",pid,true);
		indexerId = PropertiesConstants.getString(p,"indexerId",indexerId,true);
		$text = PropertiesConstants.getRaw(p,"text",$text);
		$field = PropertiesConstants.getRaw(p,"field",$field);
		
		valueId = PropertiesConstants.getString(p,"valueId",valueId,true);
		typeId = PropertiesConstants.getString(p,"typeId",typeId,true);
		startId = PropertiesConstants.getString(p,"startId",startId,true);
		endId = PropertiesConstants.getString(p,"endId",endId,true);
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
		
		String text = PropertiesConstants.transform(ctx, $text, "");
		
		if (StringUtils.isNotEmpty(text)){
			Analyzer analyzer = indexer.getAnalyzer();		
			TokenStream ts = null;
			try {
				 ts = analyzer.tokenStream(PropertiesConstants.transform(ctx, $field, "default"), text);
				
				CharTermAttribute cta = ts.getAttribute(CharTermAttribute.class);				
				TypeAttribute ta = ts.getAttribute(TypeAttribute.class);
				OffsetAttribute oa = ts.getAttribute(OffsetAttribute.class);
				
				ts.reset();
				while (ts.incrementToken()){
					ctx.SetValue(valueId, cta.toString());
					ctx.SetValue(typeId, ta.type());
					ctx.SetValue(startId, String.valueOf(oa.startOffset()));
					ctx.SetValue(endId,String.valueOf(oa.endOffset()));
					
					super.onExecute(root, current, ctx, watcher);
				}
			} catch (IOException e) {
				logger.error("Failed to analyze text : " + text);
			}finally{
				IOTools.close(ts);
			}
		}
	}	
}
