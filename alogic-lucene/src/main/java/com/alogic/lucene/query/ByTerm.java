package com.alogic.lucene.query;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import com.alogic.lucene.core.QueryBuilder;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * TermQuery
 * @author yyduan
 * @since 1.6.11.31
 */
public class ByTerm extends QueryBuilder.Abstract {
	protected String $field = "";
	protected String $value = "";
	
	@Override
	public Query build(Properties ctx) {
		String field = PropertiesConstants.transform(ctx, $field, "");
		String value = PropertiesConstants.transform(ctx, $value, "");
		if (StringUtils.isNotEmpty(field) && StringUtils.isNotEmpty(value)){
			return new TermQuery(new Term(field,value));
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