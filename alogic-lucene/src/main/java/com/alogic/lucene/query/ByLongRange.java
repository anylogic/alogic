package com.alogic.lucene.query;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.DocValuesRangeQuery;
import org.apache.lucene.search.Query;

import com.alogic.lucene.core.QueryBuilder;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * RangeQuery
 * @author yyduan
 * @since 1.6.11.34
 * 
 * @version 1.6.11.46 [20180726 duanyy] <br>
 * - build增加Analyzer上下文 <br>
 */
public class ByLongRange extends QueryBuilder.Abstract {
	protected String $field = "";
	protected String $max = "";
	protected String $min = "";
	protected String $enable = "true";
	protected boolean includeMax = true;
	protected boolean includeMin = true;
	
	@Override
	public Query build(Properties ctx,Analyzer analyzer) {
		String field = PropertiesConstants.transform(ctx, $field, "");
		long max = PropertiesConstants.transform(ctx, $max, Long.MAX_VALUE);
		long min = PropertiesConstants.transform(ctx, $min, Long.MIN_VALUE);
		boolean enable = PropertiesConstants.transform(ctx, $enable, true);
		
		if (StringUtils.isNotEmpty(field) && enable){
			return DocValuesRangeQuery.newLongRange(field, min,max,includeMin, includeMax);
		}else{
			return null;
		}
	}

	@Override
	public void configure(Properties p) {
		super.configure(p);
		$field = PropertiesConstants.getRaw( p, "field", $field);
		$max = PropertiesConstants.getRaw( p, "max", $max);
		$min = PropertiesConstants.getRaw( p, "min", $min);
		$enable = PropertiesConstants.getRaw( p, "enable", $enable);
	}

}