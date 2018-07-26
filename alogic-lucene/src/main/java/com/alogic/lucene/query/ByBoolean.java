package com.alogic.lucene.query;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alogic.lucene.core.QueryBuilder;
import com.anysoft.util.Properties;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

/**
 * BooleanQuery
 * 
 * @author yyduan
 * @since 1.6.11.31
 * 
 * @version 1.6.11.46 [20180726 duanyy] <br>
 * - build增加Analyzer上下文 <br>
 */
public class ByBoolean extends QueryBuilder.Abstract {
	
	/**
	 * 子Query列表
	 */
	protected List<QueryBuilder> builders = new ArrayList<QueryBuilder>();
	
	@Override
	public Query build(Properties ctx,Analyzer analyzer) {
		BooleanQuery.Builder builder = new BooleanQuery.Builder();
		boolean allNull = true;
		for (QueryBuilder b:builders){
			if (b.isEnable(ctx)){
				Query query = b.build(ctx,analyzer);
				if (query != null){
					allNull = false;					
					builder.add(query, b.getOccur(ctx));
				}
			}
		}
		return allNull?null:builder.build();
	}

	@Override
	public void configure(Element e, Properties p) {
		Properties props = new XmlElementProperties(e,p);
		
		NodeList nodeList = XmlTools.getNodeListByPath(e, "query");
		
		QueryBuilder.TheFactory f = new QueryBuilder.TheFactory();
		
		for (int i = 0 ; i < nodeList.getLength() ; i ++){
			Node n = nodeList.item(i);
			
			if (Node.ELEMENT_NODE != n.getNodeType()){
				continue;
			}
			
			Element elem = (Element)n;
			
			try {
				QueryBuilder builder = f.newInstance(elem, props, "module", "Term");
				builders.add(builder);
			}catch (Exception ex){
				LOG.error("Can not create query:" + XmlTools.node2String(elem));
			}
		}
		
		configure(props);
	}
}
