package com.anysoft.stream;

import org.w3c.dom.Element;

import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 基于一定比率的过滤器
 * 
 * @author duanyy
 *
 * @param <data>
 */
public class RateFilter<data extends Flowable> extends FilterHandler<data> {
	protected int rate = 100;
	@Override
	protected boolean accept(data d) {
		String id = d.id();
		return ((id.hashCode() & Integer.MAX_VALUE) % rate) <= 0;
	}

	@Override
	protected void onConfigure(Element e, Properties p) {
		super.onConfigure(e, p);
		rate = PropertiesConstants.getInt(p,"rate",rate);
		if (rate <= 0){
			rate = 100;
		}
	}		
}