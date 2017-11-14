package com.alogic.ac.verifier;

import com.alogic.ac.AccessAppKey;
import com.alogic.ac.AccessVerifier;
import com.logicbus.backend.Context;

/**
 * 无验证
 * @author yyduan
 * @since 1.6.10.6
 */
public class None extends AccessVerifier.Abstract{

	@Override
	public boolean verify(AccessAppKey key, Context ctx) {
		return true;
	}

}
