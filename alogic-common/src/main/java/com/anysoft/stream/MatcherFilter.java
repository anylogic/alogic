package com.anysoft.stream;

import org.w3c.dom.Element;

import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.StringMatcher;

/**
 * 基于id匹配的过滤器
 * 
 * @author duanyy
 *
 * @param <data>
 */
public class MatcherFilter <data extends Flowable> extends FilterHandler<data> {
	protected String pattern = "*";
	protected StringMatcher matcher = null;
	@Override
	protected boolean accept(data d) {
		String id = d.id();
		return matcher == null? true:matcher.match(id);
	}

	@Override
	protected void onConfigure(Element e, Properties p) {
		super.onConfigure(e, p);
		pattern = PropertiesConstants.getString(p,"pattern",pattern);
		
		matcher = new StringMatcher(pattern);
	}		
}