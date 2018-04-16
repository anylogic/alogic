package com.alogic.xscript.plugins;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;

/**
 * 函数声明插件
 * 
 * @author yyduan
 * @since 1.6.11.27
 */
public class FunctionDeclare extends AbstractLogiclet{
	protected String id;
	
	public FunctionDeclare(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		id = PropertiesConstants.getString(p, "id","$" + this.getXmlTag(),true);
	}
	
	@Override
	public void configure(Element root, Properties p) {
		Properties props = new XmlElementProperties(root,p);
		configure(props);
		
		if (StringUtils.isNotEmpty(id)){
			Segment segment = new Segment(this.getXmlTag(),this);		
			segment.configure(root, props);
			registerFunction(id, segment);
		}
	}	

	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		// it's not an executable logiclet
	}
	
	public boolean isExecutable(){
		return false;
	}	

}