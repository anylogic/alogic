package com.logicbus.redis.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 参数
 * @author Administrator
 *
 */
public class Params {
	private List<byte[]> params = new ArrayList<byte[]>();

	public Collection<byte[]> getParams() {
		return Collections.unmodifiableCollection(params);
	}
	
	public void add(byte[] param){
		params.add(param);
	}
}
