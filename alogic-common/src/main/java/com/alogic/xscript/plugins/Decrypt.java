package com.alogic.xscript.plugins;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alogic.xscript.util.MapProperties;
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
 * 
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
	protected void onExecute(Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		
		MapProperties p = new MapProperties(current,ctx);
		
		String inData = p.transform(in);
		String keyData = p.transform(key);
		
		if (StringUtils.isNotEmpty(inData)&&StringUtils.isNotEmpty(keyData)){
			String outData = coder.decode(inData, keyData);
			String outId = p.transform(out);
			if (StringUtils.isNotEmpty(outData) && StringUtils.isNotEmpty(outId)){
				ctx.SetValue(out, outData);
			}
		}
		
	}

}
