package com.alogic.xscript.crypto;

import org.apache.commons.lang3.StringUtils;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Pair;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.code.util.RSAUtil;

/**
 * 生成RSA公钥和私钥
 * 
 * @author yyduan
 *
 * @since 1.6.10.9
 */
public class GenRSAKey extends NS{

	protected String privateKeyId = "$crypt-private-key";
	protected String publicKeyId = "$crypt-public-key";
	
	public GenRSAKey(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p) {
		super.configure(p);
		
		privateKeyId = PropertiesConstants.getRaw(p,"privateId", privateKeyId);
		publicKeyId = PropertiesConstants.getRaw(p,"publicId", publicKeyId);
	}		
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		String privateKey = ctx.transform(privateKeyId);
		String publicKey = ctx.transform(publicKeyId);
		if (StringUtils.isNotEmpty(privateKey) && StringUtils.isNotEmpty(publicKey)){
			Pair<String,String> keyPair = RSAUtil.generateKeyPairInBase64();
			if (keyPair != null){
				ctx.SetValue(publicKey, keyPair.key());
				ctx.SetValue(privateKey, keyPair.value());
			}
		}
	}

}
