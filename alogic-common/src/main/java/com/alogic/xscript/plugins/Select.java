package com.alogic.xscript.plugins;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.selector.Selector;
import com.anysoft.selector.impl.Constants;
import com.anysoft.util.Properties;
import com.anysoft.util.XmlElementProperties;

/**
 * 设置变量到上下文，变量取值通过selector框架计算
 * 
 * @author duanyy
 * 
 * @version 1.6.8.14 [20170509 duanyy] <br>
 * - 增加xscript的中间文档模型,以便支持多种报文协议 <br>
 * 
 */
public class Select extends AbstractLogiclet {
	protected String id;
	protected Selector selector = null;
	
	public Select(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Element e, Properties p) {
		Properties props = new XmlElementProperties(e,p);
		configure(props);
		
		id = e.getAttribute("id");
		selector = Selector.newInstanceWithDefault(e, p, Constants.class.getName());
		
		if (StringUtils.isEmpty(id) && selector != null){
			id = selector.getId();
		}
	}	

	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		if (selector != null){
			ctx.SetValue(id, selector.onSelect(ctx));
		}
	}

}
