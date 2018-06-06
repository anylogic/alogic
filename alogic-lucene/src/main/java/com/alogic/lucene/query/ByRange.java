package com.alogic.lucene.query;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.DocValuesRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.BytesRef;
import com.alogic.lucene.core.QueryBuilder;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * RangeQuery
 * @author yyduan
 * @since 1.6.11.34
 */
public class ByRange extends QueryBuilder.Abstract {
	protected String $field = "";
	protected String $max = "";
	protected String $min = "";
	protected boolean includeMax = true;
	protected boolean includeMin = true;
	
	@Override
	public Query build(Properties ctx) {
		String field = PropertiesConstants.transform(ctx, $field, "");
		String max = PropertiesConstants.transform(ctx, $max, "");
		String min = PropertiesConstants.transform(ctx, $min, "");
		
		if (StringUtils.isNotEmpty(field) && StringUtils.isNotEmpty($max)
				&& StringUtils.isNotEmpty($min)) {
			return DocValuesRangeQuery.newBytesRefRange(field, new BytesRef(
					max), new BytesRef(min), includeMin, includeMax);
		} else {
			return null;
		}
	}

	@Override
	public void configure(Properties p) {
		super.configure(p);
		$field = PropertiesConstants.getRaw( p, "field", $field);
		$max = PropertiesConstants.getRaw( p, "max", $max);
		$min = PropertiesConstants.getRaw( p, "min", $min);
		includeMax = PropertiesConstants.getBoolean(p, "includeMax", true);
		includeMin = PropertiesConstants.getBoolean(p, "includeMin", true);
	}

}
