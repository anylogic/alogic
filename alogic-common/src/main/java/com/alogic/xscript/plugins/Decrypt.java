package com.alogic.xscript.plugins;

import org.apache.commons.lang3.StringUtils;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.code.Coder;
import com.anysoft.util.code.CoderFactory;

/**
 * 解密
 * 
 * @author duanyy
 * @since 1.6.5.13
 * @version 1.6.8.14 [20170509 duanyy] <br>
 * - 增加xscript的中间文档模型,以便支持多种报文协议 <br>
 */
public class Decrypt extends AbstractLogiclet{
	protected String in = "in";
	protected String out = "out";
	protected Coder coder = null;
	protected String key;
	
	public Decrypt(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p) {
		super.configure(p);
		
		in = PropertiesConstants.getRaw(p,"in", "");
		out = PropertiesConstants.getRaw(p,"out", "");
		key = PropertiesConstants.getRaw(p,"key", "");
		
		coder = CoderFactory.newCoder(PropertiesConstants.getString(p,"coder","DES3"));
	}		
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		
		String inData = ctx.transform(in);
		String keyData = ctx.transform(key);
		
		if (StringUtils.isNotEmpty(inData)&&StringUtils.isNotEmpty(keyData)){
			String outData = coder.decode(inData, keyData);
			String outId = ctx.transform(out);
			if (StringUtils.isNotEmpty(outData) && StringUtils.isNotEmpty(outId)){
				ctx.SetValue(outId, outData);
			}
		}
		
	}

}
