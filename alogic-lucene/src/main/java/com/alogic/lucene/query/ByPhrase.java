package com.alogic.lucene.query;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;

import com.alogic.lucene.core.QueryBuilder;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * PhraseQuery
 * @author yyduan
 * @since 1.6.11.31
 */
public class ByPhrase extends QueryBuilder.Abstract {
	protected String $field = "";
	protected String $value = "";
	protected String delimeter = " ";
	protected String $slop = "100";
	
	@Override
	public Query build(Properties ctx) {
		String field = PropertiesConstants.transform(ctx, $field, "");
		String values = PropertiesConstants.transform(ctx, $value, "");
		
		if (StringUtils.isNotEmpty(field) && StringUtils.isNotEmpty(values)){
			int slop = PropertiesConstants.getInt(ctx, $slop, 100);
			String [] vals = values.split(delimeter);
			return new PhraseQuery(slop,field,vals);
		}else{
			return null;
		}
	}

	@Override
	public void configure(Properties p) {
		super.configure(p);
		$field = PropertiesConstants.getRaw( p, "field", $field);
		$value = PropertiesConstants.getRaw( p, "value", $value);
		$slop = PropertiesConstants.getRaw( p, "slop", $slop);
		delimeter = PropertiesConstants.getString(p, "delimeter", delimeter);
	}

}
