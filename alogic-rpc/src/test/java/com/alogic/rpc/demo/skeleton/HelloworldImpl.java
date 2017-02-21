package com.alogic.rpc.demo.skeleton;

import com.alogic.rpc.demo.api.IHelloworld;

/**
 * Helloworld的骨架代码
 * 
 * @author duanyy
 *
 */
public class HelloworldImpl implements IHelloworld {

	@Override
	public String sayHello(String words) {
		return words;
	}

}
