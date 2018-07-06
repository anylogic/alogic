package com.alogic.xscript.plugins;

import org.apache.commons.lang3.StringUtils;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.doc.XsObjectProperties;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 设置变量值到上下文
 * 
 * @author duanyy
 * 
 * @version 1.6.8.14 [20170509 duanyy] <br>
 * - 增加xscript的中间文档模型,以便支持多种报文协议 <br>
 * 
 * @version 1.6.11.42 [20170509 duanyy] <br>
 * - 支持动态id输出<br>
 */
public class Set extends AbstractLogiclet {
	protected String $id;
	protected String $value;
	protected String $dftValue = "";
	protected boolean ref = false;
	
	public Set(String tag, Logiclet p) {
		super(tag, p);
	}

	public void configure(Properties p){
		super.configure(p);
		
		$id = PropertiesConstants.getRaw(p,"id","");
		$value = PropertiesConstants.getRaw(p,"value","");
		ref = PropertiesConstants.getBoolean(p,"ref",ref,true);
		$dftValue = PropertiesConstants.getRaw(p,"dft",$dftValue);
	}

	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		String id = PropertiesConstants.transform(ctx, $id, "");
		if (StringUtils.isNotEmpty(id)){
			XsObjectProperties p = new XsObjectProperties(current,ctx);
			String v = PropertiesConstants.transform(p,$value,"");
			String dft = PropertiesConstants.transform(p,$dftValue,"");
			if (StringUtils.isEmpty(v)){
				v = dft;
			}
			if (ref){
				v = PropertiesConstants.getString(p,v,dft,false);
			}
			
			ctx.SetValue(id, v);
		}
	}

}
