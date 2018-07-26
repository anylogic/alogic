package com.alogic.lucene.query;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.BytesRef;

import com.alogic.lucene.core.QueryBuilder;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

public class ByMatch extends QueryBuilder.Abstract {
	protected String $field = "";
	protected String $value = "";
	
	@Override
	public Query build(Properties ctx,Analyzer analyzer) {
		String field = PropertiesConstants.transform(ctx, $field, "");
		String value = PropertiesConstants.transform(ctx, $value, "");
		if (StringUtils.isNotEmpty(field) && StringUtils.isNotEmpty(value)){
			TokenStream source = null;
			try{
				source = analyzer.tokenStream(field, value);
				BooleanQuery.Builder builder = new BooleanQuery.Builder();
				TermToBytesRefAttribute termAtt = source.getAttribute(TermToBytesRefAttribute.class);
				source.reset();				
				int cnt = 0;
				while (source.incrementToken()){
					cnt ++;
					BytesRef bytes = termAtt.getBytesRef();		
					builder.add(new TermQuery(new Term(field, BytesRef.deepCopyOf(bytes))), BooleanClause.Occur.SHOULD);
				}
				return cnt == 0 ? null:builder.build();
			} catch (IOException e) {
				LOG.error(ExceptionUtils.getStackTrace(e));
				return null;
			}finally{
				IOTools.close(source);
			}
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
