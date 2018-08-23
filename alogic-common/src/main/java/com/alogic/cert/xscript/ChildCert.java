package com.alogic.cert.xscript;

import java.math.BigInteger;

import com.alogic.cert.CertificateContent;
import com.alogic.cert.CertificateStore;
import com.alogic.cert.CertificateStoreFactory;
import com.alogic.cert.PemCertificateContent;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.KeyGen;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 采用根证书签发一个子证书
 * 
 * @author yyduan
 * 
 * @since 1.6.11.9
 * @version 1.6.11.56 [20180823 duanyy] <br>
 * - 证书的序列号可定制; <br>
 */
public class ChildCert extends NS{
	protected String cid = "$cert";
	protected String $cn;
	protected String $sn;
	
	public ChildCert(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		$cn = PropertiesConstants.getRaw(p,"cn","");
		$sn = PropertiesConstants.getRaw(p,"sn","");
		cid = PropertiesConstants.getString(p, "cid", cid,true);
	}
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		
		long sn = PropertiesConstants.transform(ctx, $sn, 0);
		if (sn <= 0){
			sn = System.currentTimeMillis() * 10000 + Integer.parseInt(KeyGen.uuid(5, 0, 9));
		}

		CertificateStore store = CertificateStoreFactory.getDefault();
		CertificateContent pem = store.newCertificate(
				BigInteger.valueOf(sn),
				new PemCertificateContent(),
				PropertiesConstants.transform(ctx, $cn, "ctg-aep-iam")
				);	
		try {
			ctx.setObject(cid, pem);
			super.onExecute(root, current, ctx, watcher);
		}finally{
			ctx.removeObject(cid);
		}
	}
}
