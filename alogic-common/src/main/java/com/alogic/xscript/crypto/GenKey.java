package com.alogic.xscript.crypto;

import org.apache.commons.lang3.StringUtils;

import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.code.Coder;
import com.anysoft.util.code.CoderFactory;

/**
 * 生成密钥
 * 
 * @author yyduan
 *
 * @since 1.6.10.9
 */
public class GenKey extends AbstractLogiclet{

	protected String id = "$crypt-key";
	protected String initKey;
	protected String coder = "DES3";
	
	public GenKey(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p) {
		super.configure(p);
		
		id = PropertiesConstants.getRaw(p,"id", id);
		initKey = PropertiesConstants.getRaw(p,"init", "");
		coder = PropertiesConstants.getRaw(p,"coder", coder);
	}		
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		String cid = ctx.transform(id);
		if (StringUtils.isNotEmpty(cid)){
			Coder c = CoderFactory.newCoder(PropertiesConstants.transform(ctx,coder,"DES3"));
			ctx.SetValue(cid, c.createKey(ctx.transform(initKey)));
		}
	}

}