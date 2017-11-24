package com.alogic.xscript.crypto;

import org.apache.commons.lang3.StringUtils;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.code.Coder;
import com.anysoft.util.code.CoderFactory;

/**
 * 解密
 * @author yyduan
 *
 * @since 1.6.10.9
 */
public class Decrypt extends NS{
	protected String in = "in";
	protected String id;
	protected String coder = "DES3";
	protected String key;
	
	public Decrypt(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p) {
		super.configure(p);
		
		in = PropertiesConstants.getRaw(p,"in", "");
		id = PropertiesConstants.getRaw(p,"id", "$" + this.getXmlTag());
		key = PropertiesConstants.getRaw(p,"key", "");
		coder = PropertiesConstants.getRaw(p,"coder", coder);
	}		
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		
		String inData = ctx.transform(in);
		String keyData = ctx.transform(key);
		
		if (StringUtils.isNotEmpty(inData)&&StringUtils.isNotEmpty(keyData)){
			Coder c = CoderFactory.newCoder(PropertiesConstants.transform(ctx,coder,"DES3"));
			String outData = c.decode(inData, keyData);
			String outId = ctx.transform(id);
			if (StringUtils.isNotEmpty(outData) && StringUtils.isNotEmpty(outId)){
				ctx.SetValue(outId, outData);
			}
		}
		
	}

}