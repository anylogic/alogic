package com.alogic.remote.xscript.util;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
 * MD5签名
 * @author yyduan
 * @since 1.6.10.3
 */
public class MD5 extends AbstractLogiclet{
	protected String id;
	protected String value = "";
	public MD5(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		id = PropertiesConstants.getString(p,"id","$" + this.getXmlTag(),true);
		value = PropertiesConstants.getRaw(p,"value",value);
	}
	
	@Override
	protected void onExecute(XsObject root,XsObject current,final LogicletContext ctx,final ExecuteWatcher watcher){
		String val = ctx.transform(value);
		if (StringUtils.isNotEmpty(val)){
			try {
				MessageDigest m = MessageDigest.getInstance("md5");
				m.update(val.getBytes());
				byte b[] = m.digest();
				
				StringBuffer buf = new StringBuffer();
				for (int i, offset = 0; offset < b.length; offset++) {
					i = b[offset];
					if (i < 0)
						i += 256;
					if (i < 16)
						buf.append("0");
					buf.append(Integer.toHexString(i));
				}
				ctx.SetValue(id, buf.toString());
			} catch (NoSuchAlgorithmException e) {
				throw new BaseException("core.not_supported","Md5 algorithm is not supported");
			}
		}
	}	
}