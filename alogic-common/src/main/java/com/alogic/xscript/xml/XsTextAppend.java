package com.alogic.xscript.xml;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 在当前节点下面append一个Text节点
 * @author yyduan
 * @since 1.6.11.60 [20180912 duanyy]
 */
public class XsTextAppend extends XsElementOperation{
	protected String $value = "";
	
	public XsTextAppend(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		$value = PropertiesConstants.getRaw(p,"value",$value);
	}
	@Override
	protected void onExecute(Element elem, XsObject root, XsObject current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		String value = PropertiesConstants.transform(ctx, $value, "");
		
		if (StringUtils.isNotEmpty(value)){
			Document doc = elem.getOwnerDocument();
			elem.appendChild(doc.createTextNode(value));
		}
	}

}
