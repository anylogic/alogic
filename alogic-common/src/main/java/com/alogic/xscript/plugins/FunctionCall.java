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

/**
 * 函数调用插件
 * @author yyduan
 * @since 1.6.11.27
 */
public class FunctionCall extends AbstractLogiclet{

	/**
	 * 函数id
	 */
	protected String id;
	
	/**
	 * 回调
	 */
	protected Logiclet callback = null;
	
	protected String callbackId = "$callback";

	public FunctionCall(String tag, Logiclet p) {
		super(tag, p);
	}	
	
	@Override
	public void configure(Element e, Properties p) {
		super.configure(e, p);
		
		callback = new Segment(this.getXmlTag(),this);		
		callback.configure(e, p);		
	}	

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		id = PropertiesConstants.getRaw(p,"id","");
		callbackId = PropertiesConstants.getRaw(p,"callback",callbackId);
	}
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		String funcId = PropertiesConstants.transform(ctx, id, "");
		if (StringUtils.isNotEmpty(funcId)){
			Logiclet func = this.getFunction(funcId);
			if (func != null){
				try {
					if (callback != null){
						ctx.setObject(callbackId, callback);
					}
					func.execute(root, current, ctx, watcher);
				}finally{
					if (callback != null){
						ctx.removeObject(callbackId);
					}
				}
			}
		}
	}
}
