package com.alogic.cert.xscript;

import com.alogic.xscript.Logiclet;
import com.alogic.xscript.plugins.Segment;

/**
 * Namespace
 * @author yyduan
 * 
 * @since 1.6.11.9
 * @version 1.6.11.55 [20180822 duanyy] <br>
 * - 增加获取证书序列号功能; <br>
 */
public class NS extends Segment{

	public NS(String tag, Logiclet p) {
		super(tag, p);
		
		registerModule("cert-root",RootCert.class);
		registerModule("cert-new",ChildCert.class);
		registerModule("cert-get-key",GetKey.class);
		registerModule("cert-save-key",SaveKey.class);
		registerModule("cert-get-cert",GetCert.class);
		registerModule("cert-get-id",GetCertId.class);
		registerModule("cert-save-cert",SaveCert.class);
	}

}
