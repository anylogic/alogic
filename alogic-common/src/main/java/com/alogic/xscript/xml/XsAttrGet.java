package com.alogic.xscript.xml;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 获取Element的attr值
 * @author yyduan
 * @since 1.6.11.38
 */
public class XsAttrGet extends XsElementOperation{
	protected String $id = "";
	protected String $dft = "";

	public XsAttrGet(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		$id = PropertiesConstants.getRaw(p,"id","$" + this.getXmlTag());
		$dft = PropertiesConstants.getRaw(p,"dft",$dft);
	}
	@Override
	protected void onExecute(Element elem, XsObject root, XsObject current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		String id = PropertiesConstants.transform(ctx, $id, "$" + this.getXmlTag());
		
		if (StringUtils.isNotEmpty(id)){
			String value = elem.getAttribute(id);
			if (StringUtils.isEmpty(value)){
				value = PropertiesConstants.transform(ctx, $dft, "");
			}
			
			if (StringUtils.isNotEmpty(value)){
				ctx.SetValue(id, value);
			}
		}
	}

}
