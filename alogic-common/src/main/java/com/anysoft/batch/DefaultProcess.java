package com.anysoft.batch;

import com.anysoft.util.DefaultProperties;

/**
 * 缺省的处理过程
 * 
 * @author duanyy
 *
 */
public class DefaultProcess implements Process {

	public int init(DefaultProperties p) {
		p.list(System.out);
		return 0;
	}

	public int run() {
		return 0;
	}

}
