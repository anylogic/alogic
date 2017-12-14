package com.alogic.remote.xscript.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.apache.commons.lang3.StringUtils;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 对指定的值进行URLDecode解码
 * @author yyduan
 * @since 1.6.10.3
 */
public class Encoder extends AbstractLogiclet{
	protected String id;
	protected String value = "";
	protected String encoding = "utf-8";
	public Encoder(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		id = PropertiesConstants.getString(p,"id","$" + this.getXmlTag(),true);
		value = PropertiesConstants.getRaw(p,"value",value);
		encoding = PropertiesConstants.getRaw(p,"encoding",encoding);
	}
	
	@Override
	protected void onExecute(XsObject root,XsObject current,final LogicletContext ctx,final ExecuteWatcher watcher){
		String val = ctx.transform(value);
		if (StringUtils.isNotEmpty(val)){
			String enc = ctx.transform(encoding);			
			enc = StringUtils.isEmpty(enc) ? "utf-8":enc;
			try {
				ctx.SetValue(id, URLEncoder.encode(val, enc));
			} catch (UnsupportedEncodingException e) {
				throw new BaseException("core.e1005",String.format("Encoding %s is not supported", enc));
			}
		}
	}	
}
