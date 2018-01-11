package com.alogic.cert.xscript;

import com.alogic.cert.CertificateContent;
import com.alogic.cert.CertificateStore;
import com.alogic.cert.CertificateStoreFactory;
import com.alogic.cert.PemCertificateContent;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 采用根证书签发一个子证书
 * 
 * @author yyduan
 * 
 * @since 1.6.11.9
 *
 */
public class ChildCert extends NS{
	protected String cid = "$cert";
	protected String $cn;
	
	public ChildCert(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		$cn = PropertiesConstants.getRaw(p,"cn","");
		cid = PropertiesConstants.getString(p, "cid", cid,true);
	}
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		CertificateStore store = CertificateStoreFactory.getDefault();
		CertificateContent pem = store.newCertificate(
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
