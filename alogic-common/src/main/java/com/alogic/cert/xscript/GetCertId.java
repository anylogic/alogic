package com.alogic.cert.xscript;

import org.apache.commons.lang3.StringUtils;

import com.alogic.cert.CertificateContent;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 获取证书id
 * 
 * @author yyduan
 * @since 1.6.11.55 [20180822 duanyy]
 * 
 * 
 */
public class GetCertId extends CertificateOperation{

	protected String $id = "$cert-cert-id";

	public GetCertId(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		$id = PropertiesConstants.getRaw(p,"id",$id);
	}
	
	@Override
	protected void onExecute(CertificateContent content, XsObject root,
			XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		String id = PropertiesConstants.transform(ctx, $id, "$cert-key");
		if (StringUtils.isNotEmpty(id)){
			ctx.SetValue(id, content.getCertId());
		}
	}

}
