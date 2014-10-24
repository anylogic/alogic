package com.logicbus.kvalue.common;

import org.w3c.dom.Element;

import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 简单Hash分区
 * 
 * @author duanyy
 *
 */
public class SimpleHash extends AbstractPartitioner {
	
	protected int nodesCnt = 5;
	
	
	protected String getPartitionCase(String key) {
		int idx = key.hashCode() & Integer.MAX_VALUE % nodesCnt;
		return String.valueOf(idx);
	}

	protected void onConfigure(Element _e,Properties _p){
		nodesCnt = PropertiesConstants.getInt(_p, "nodesCnt", nodesCnt,true);
		nodesCnt = nodesCnt <= 0 ? 5 : nodesCnt;
	}
}
