package com.alogic.xscript.doc;

import com.anysoft.util.Properties;

/**
 * 将XsObject封装为Properties接口
 * 
 * @author yyduan
 * @since 1.6.8.14
 */
public class XsObjectProperties extends Properties{

	protected XsObject xsObj = null;
	
	public XsObjectProperties(XsObject xsObj,Properties parent){
		super("default",parent);		
		this.xsObj = xsObj;
	}
	
	@Override
	protected void _SetValue(String _name, String _value) {
		if (xsObj != null){
			xsObj.addProperty(_name, _value);
		}
	}

	@Override
	protected String _GetValue(String _name) {
		if (xsObj != null){
			return xsObj.getProperty(_name, null);
		}
		return null;
	}

	@Override
	public void Clear() {
		// do not clear anything
	}

}
