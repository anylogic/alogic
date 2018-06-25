package com.alogic.xscript.xml;

import org.apache.commons.lang3.StringUtils;
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
 * 获取Text节点的值
 * 
 * @author yyduan
 * @since 1.6.11.38
 */
public class XsTextGet extends XsElementOperation{
	protected String $id = "";
	protected String $tag = "";
	protected String $dft = "";

	public XsTextGet(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		$id = PropertiesConstants.getRaw(p,"id","$" + this.getXmlTag());
		$tag = PropertiesConstants.getRaw(p,"tag",$tag);
		$dft = PropertiesConstants.getRaw(p,"dft",$dft);
	}
	@Override
	protected void onExecute(Element elem, XsObject root, XsObject current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		String id = PropertiesConstants.transform(ctx, $id, "$" + this.getXmlTag());
		String tag = PropertiesConstants.transform(ctx, $tag, "");
		if (StringUtils.isNotEmpty(id) && StringUtils.isNotEmpty(tag)){
			String value = "";
			Element e = XmlTools.getFirstElementByPath(elem, tag);
			if (e != null){
				Node node = e.getFirstChild();
				if (node != null){
					value = node.getNodeValue();
				}
			}
			
			if (StringUtils.isEmpty(value)){
				value = PropertiesConstants.transform(ctx, $dft, "");
			}
			
			if (StringUtils.isNotBlank(value)){
				ctx.SetValue(id, value);
			}
		}
	}

}