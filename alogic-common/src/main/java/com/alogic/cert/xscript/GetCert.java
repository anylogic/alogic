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
 * 从证书内容中获取证书
 * @author yyduan
 * 
 * @since 1.6.11.9
 * @version 1.6.11.56 [20180823 duanyy] <br>
 * - 证书的序列号可定制; <br>
 */
public class GetCert extends CertificateOperation{

	protected String $id = "$cert-cert";
	protected String $raw = "false";
	
	public GetCert(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		$id = PropertiesConstants.getRaw(p,"id",$id);
		$raw = PropertiesConstants.getRaw(p,"raw",$raw);
	}
	
	@Override
	protected void onExecute(CertificateContent content, XsObject root,
			XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		String id = PropertiesConstants.transform(ctx, $id, "$cert-key");
		if (StringUtils.isNotEmpty(id)){
			String key = new String(content.getCert(PropertiesConstants.transform(ctx, $raw, false)));
			ctx.SetValue(id, key);
		}
	}

}
