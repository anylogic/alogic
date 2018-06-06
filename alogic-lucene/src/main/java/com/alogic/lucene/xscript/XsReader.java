package com.alogic.lucene.xscript;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alogic.lucene.client.IndexerTool;
import com.alogic.lucene.core.Indexer;
import com.alogic.lucene.core.QueryBuilder;
import com.alogic.lucene.query.ByTerm;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.BaseException;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

/**
 * 基于Indexer的查询
 * 
 * @author yyduan
 * @since 1.6.11.31
 */

public class XsReader extends NS{
	/**
	 * 父节点的上下文id
	 */
	protected String pid = "$indexer";
	
	/**
	 * 文档的id
	 */
	protected String docId = "$lucene-doc";
	
	/**
	 * 当不存在父节点时自动连接indexer
	 */
	protected String indexerId = "";
	
	protected String $offset = "0";
	
	protected String $limit = "100";
	
	protected String dftField = "all";
	
	protected String dftValue = "all";
	
	protected QueryBuilder queryBuilder = null;
	
	/**
	 * 查询所有
	 */
	protected Query queryAll = new TermQuery(new Term("all","all"));
	
	public XsReader(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		pid = PropertiesConstants.getString(p,"pid",pid,true);
		docId = PropertiesConstants.getString(p,"docId",docId,true);
		indexerId = PropertiesConstants.getString(p,"indexerId",indexerId,true);
		
		$offset = PropertiesConstants.getRaw(p,"offset",$offset);
		dftField = PropertiesConstants.getString(p,"dftField",dftField,true);
		dftValue = PropertiesConstants.getString(p,"dftValue",dftValue,true);
		$limit = PropertiesConstants.getRaw(p,"limit",$limit);
		
		queryAll = new TermQuery(new Term(dftField,dftValue));
		if (queryBuilder == null){
			queryBuilder = new ByTerm();
			queryBuilder.configure(p);
		}
	}
	
	@Override
	public void configure(Element element, Properties props) {
		XmlElementProperties p = new XmlElementProperties(element, props);
		
		Element queryElem = XmlTools.getFirstElementByPath(element, "query");
		if (queryElem != null){
			QueryBuilder.TheFactory f = new QueryBuilder.TheFactory();
			try {
				queryBuilder = f.newInstance(queryElem, p, "module", "Term");
			}catch (Exception ex){
				logger.error("Can not create query:" + XmlTools.node2String(queryElem));
			}			
		}
		
		NodeList nodeList = element.getChildNodes();
		
		for (int i = 0 ; i < nodeList.getLength() ; i ++){
			Node n = nodeList.item(i);
			
			if (n.getNodeType() != Node.ELEMENT_NODE){
				//只处理Element节点
				continue;
			}
			
			Element e = (Element)n;
			String xmlTag = e.getNodeName();		
			
			if (xmlTag.equals("query")){
				continue;
			}
			
			Logiclet statement = createLogiclet(xmlTag, this);
			
			if (statement != null){
				statement.configure(e, p);
				if (statement.isExecutable()){
					children.add(statement);
				}
			}
		}
		
		configure(p);
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

		int offset = PropertiesConstants.transform(ctx, $offset, 0);
		int limit = PropertiesConstants.transform(ctx, $limit, 100);
		
		IndexReader reader = indexer.newReader();
		try {
			Query query = getQuery(ctx);	
			IndexSearcher searcher = new IndexSearcher(reader);			

			TopDocs topDocs = searcher.search(query, offset + limit);

			ScoreDoc [] hits = topDocs.scoreDocs;

			ctx.SetValue("$offset", String.valueOf(offset));
			ctx.SetValue("$limit", String.valueOf(limit));
			ctx.SetValue("$all", String.valueOf(topDocs.totalHits));
			
			int total = (hits.length > offset)?hits.length - offset:0;
			ctx.SetValue("$total", String.valueOf(total));
			
			for (int i = offset ;  i < hits.length && i < limit + offset ; i ++ ){
			     try {
			    	 ctx.setObject(docId, searcher.doc(hits[i].doc));
			    	 super.onExecute(root, current, ctx, watcher);
			     }finally{
			    	 ctx.removeObject(docId);
			     }
			}
		}catch (IOException ex){
			logger.error(ExceptionUtils.getStackTrace(ex));
			throw new BaseException("core.e1004","Can not query from indexer.");
		}finally{
			IOTools.close(reader);
		}		
	}	
	
	protected  Query getQuery(Properties ctx){
		Query query =  queryBuilder.build(ctx);
		return query == null ? queryAll : query;
	}
}
