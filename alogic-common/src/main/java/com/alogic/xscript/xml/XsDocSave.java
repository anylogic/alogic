package com.alogic.xscript.xml;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlTools;

/**
 * 将当前的文档输出到变量
 * 
 * @author yyduan
 * @since 1.6.11.38
 */
public class XsDocSave extends XsElementOperation{
	protected String $id = "";
	protected String encoding = "utf-8";
	
	public XsDocSave(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		encoding = PropertiesConstants.getString(p, "encoding" , encoding);
		$id = PropertiesConstants.getRaw(p,"id","$" + this.getXmlTag());
	}
	@Override
	protected void onExecute(Element elem, XsObject root, XsObject current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		String id = PropertiesConstants.transform(ctx, $id, "$" + this.getXmlTag());		
		if (StringUtils.isNotEmpty(id)){
			ctx.SetValue(id, XmlTools.node2String(elem));
		}
	}

}
