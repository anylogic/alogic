package com.alogic.lucene.query;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.Query;

import com.alogic.lucene.core.QueryBuilder;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * FuzzyQuery
 * @author yyduan
 * @since 1.6.11.31
 * 
 * @version 1.6.11.46 [20180726 duanyy] <br>
 * - build增加Analyzer上下文 <br>
 */
public class ByFuzzy extends QueryBuilder.Abstract {
	protected String $field = "";
	protected String $value = "";
	
	@Override
	public Query build(Properties ctx,Analyzer analyzer) {
		String field = PropertiesConstants.transform(ctx, $field, "");
		String value = PropertiesConstants.transform(ctx, $value, "");
		
		if (StringUtils.isNotEmpty(field) && StringUtils.isNotEmpty(value)){
			return new FuzzyQuery(new Term(field,value));
		}else{
			return null;
		}
	}

	@Override
	public void configure(Properties p) {
		super.configure(p);
		$field = PropertiesConstants.getRaw( p, "field", $field);
		$value = PropertiesConstants.getRaw( p, "value", $value);
	}

}
