package com.anysoft.batch;

import com.anysoft.util.DefaultProperties;

/**
 * 缺省的处理过程
 * 
 * @author duanyy
 *
 */
public class DefaultProcess implements Process {

	@Override
	public int init(DefaultProperties p) {
		p.list(System.out); // NOSONAR
		return 0;
	}
	@Override
	public int run() {
		return 0;
	}

}
