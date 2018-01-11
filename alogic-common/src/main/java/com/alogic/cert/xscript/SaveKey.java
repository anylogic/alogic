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
 * 从证书内容中获取key，并存储到本地文件
 * 
 * @author yyduan
 * 
 * @since 1.6.11.9
 *
 */
public class SaveKey extends CertificateOperation{

	protected String $path = "";

	public SaveKey(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		$path = PropertiesConstants.getRaw(p,"path","");
	}
	
	@Override
	protected void onExecute(CertificateContent content, XsObject root,
			XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		String path = PropertiesConstants.transform(ctx, $path, "$cert-key");
		if (StringUtils.isNotEmpty(path)){
			content.saveKey(path);
		}
	}

}
