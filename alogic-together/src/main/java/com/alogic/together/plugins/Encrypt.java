package com.alogic.together.plugins;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alogic.together.AbstractLogiclet;
import com.alogic.together.ExecuteWatcher;
import com.alogic.together.Logiclet;
import com.alogic.together.LogicletContext;
import com.alogic.together.util.MapProperties;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.code.Coder;
import com.anysoft.util.code.CoderFactory;

/**
 * 加密
 * 
 * @author duanyy
 * @since 1.6.5.13
 */
public class Encrypt extends AbstractLogiclet{
	protected String in = "in";
	protected String out = "out";
	protected Coder coder = null;
	protected String key;
	
	public Encrypt(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p) {
		super.configure(p);
		
		in = PropertiesConstants.getString(p,"in", in);
		out = PropertiesConstants.getString(p,"out", out);
		key = PropertiesConstants.getString(p,"key", "");
		
		coder = CoderFactory.newCoder(PropertiesConstants.getString(p,"coder","DES3"));
	}		
	
	@Override
	protected void onExecute(Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		
		MapProperties p = new MapProperties(current,ctx);
		
		String inData = PropertiesConstants.getString(p,in,"");
		String keyData = PropertiesConstants.getString(p,key,"");
		
		if (StringUtils.isNotEmpty(inData)&&StringUtils.isNotEmpty(keyData)){
			String outData = coder.encode(inData, keyData);
			if (StringUtils.isNotEmpty(outData)){
				ctx.SetValue(out, outData);
			}
		}
		
	}

}
