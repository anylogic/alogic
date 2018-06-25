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
 * 设置当前element的属性
 * 
 * @author yyduan
 * @since 1.6.11.38
 */
public class XsAttrSet extends XsElementOperation{
	protected String $id = "";
	protected String $value = "";
	protected boolean overwrite = true;
	
	public XsAttrSet(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		$id = PropertiesConstants.getRaw(p,"id",$id);
		$value = PropertiesConstants.getRaw(p,"value",$value);
		overwrite = PropertiesConstants.getBoolean(p, "overwrite", overwrite);
	}
	@Override
	protected void onExecute(Element elem, XsObject root, XsObject current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		String id = PropertiesConstants.transform(ctx, $id, "");
		String value = PropertiesConstants.transform(ctx, $value, "");
		
		if (StringUtils.isNotEmpty(id)){
			if (StringUtils.isEmpty(value)){
				value = PropertiesConstants.getString(ctx, id, "");
			}
			
			if (StringUtils.isNotEmpty(value)){
				if (overwrite){
					elem.setAttribute(id, value);
				}else{
					boolean exist = elem.hasAttribute(id);
					if (!exist){
						elem.setAttribute(id, value);
					}
				}
			}
		}
	}

}
