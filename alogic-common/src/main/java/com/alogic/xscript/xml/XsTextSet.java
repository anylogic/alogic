package com.alogic.xscript.xml;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlTools;

/**
 * 增加一个Text节点
 * @author yyduan
 * @since 1.6.11.38
 */
public class XsTextSet extends XsElementOperation{
	protected String $tag = "";
	protected String $value = "";
	protected boolean overwrite = true;
	
	public XsTextSet(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		$tag = PropertiesConstants.getRaw(p,"tag",$tag);
		$value = PropertiesConstants.getRaw(p,"value",$value);
		overwrite = PropertiesConstants.getBoolean(p, "overwrite", overwrite);
	}
	@Override
	protected void onExecute(Element elem, XsObject root, XsObject current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		String tag = PropertiesConstants.transform(ctx, $tag, "");
		String value = PropertiesConstants.transform(ctx, $value, "");
		
		if (StringUtils.isNotEmpty(tag)){
			if (StringUtils.isEmpty(value)){
				value = PropertiesConstants.getString(ctx,tag,"");
			}
			Document doc = elem.getOwnerDocument();
			Element child = XmlTools.getFirstElementByPath(elem, tag);
			if (child == null){				
				child = doc.createElement(tag);
				child.appendChild(doc.createTextNode(value));
				elem.appendChild(child);						
			}else{
				Node node = child.getFirstChild();
				if (node == null){
					child.appendChild(doc.createTextNode(value));
				}else{
					child.replaceChild(doc.createTextNode(value), node);
				}
			}
		}
	}

}
 