package com.alogic.lucene.query;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import com.alogic.lucene.core.QueryBuilder;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 多个Term组合查询
 * @author yyduan
 * @since 1.6.11.46
 */
public class ByTerms extends QueryBuilder.Abstract {
	protected String $field = "";
	protected String $terms = "";
	protected String delimeter = ",";
	
	@Override
	public Query build(Properties ctx,Analyzer analyzer) {
		String field = PropertiesConstants.transform(ctx, $field, "");
		String terms = PropertiesConstants.transform(ctx, $terms, "");
		if (StringUtils.isNotEmpty(field) && StringUtils.isNotEmpty(terms)){
			BooleanQuery.Builder builder = new BooleanQuery.Builder();			
			String [] vals = terms.split(delimeter);
			int cnt = 0;
			for (String val:vals){
				if (StringUtils.isNotEmpty(val)){
					cnt ++;
					builder.add(new TermQuery(new Term(field, val)), BooleanClause.Occur.SHOULD);
				}
			}
			return cnt == 0 ? null:builder.build();
		}else{
			return null;
		}
	}

	@Override
	public void configure(Properties p) {
		super.configure(p);
		$field = PropertiesConstants.getRaw( p, "field", $field);
		$terms = PropertiesConstants.getRaw( p, "value", $terms);
		delimeter = PropertiesConstants.getString(p,"delimeter",delimeter);
	}

}
