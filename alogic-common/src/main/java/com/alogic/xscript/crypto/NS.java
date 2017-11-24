package com.alogic.xscript.crypto;

import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.Logiclet;

/**
 * 加解密相关的插件
 * 
 * @author yyduan
 * 
 * @since 1.6.10.9
 */
public class NS extends AbstractLogiclet{

	public NS(String tag, Logiclet p) {
		super(tag, p);
		
		registerModule("crypt-de",Decrypt.class);
		registerModule("crypt-en",Encrypt.class);
		registerModule("crypt-key",GenKey.class);
		registerModule("crypt-rsa-key",GenRSAKey.class);
	}

}
