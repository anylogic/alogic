package com.alogic.xscript.plugins;


import org.apache.commons.lang3.StringUtils;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;

/**
 * 从settings中提取相应的值，并设置为变量
 * 
 * @author duanyy
 *
 * @since 1.6.5.39
 * 
 * @version 1.6.8.14 [20170509 duanyy] <br>
 * - 增加xscript的中间文档模型,以便支持多种报文协议 <br>
 * 
 * @version 1.6.9.7 [20170801 duanyy] <br>
 * - 支持refer和raw的功能 <br>
 */
public class FromSettings extends AbstractLogiclet {
	protected String id;
	protected String value;
	protected boolean raw = false;
	protected boolean ref = false;
	protected String dft;
	
	public FromSettings(String tag, Logiclet p) {
		super(tag, p);
	}

	public void configure(Properties p){
		super.configure(p);
		
		id = PropertiesConstants.getString(p,"id","",true);
		value = PropertiesConstants.getRaw(p,"value","");
		raw = PropertiesConstants.getBoolean(p, "raw", raw);
		ref = PropertiesConstants.getBoolean(p, "ref", ref);
		dft = PropertiesConstants.getRaw(p,"dft","");
	}

	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		if (StringUtils.isNotEmpty(id)){
			Settings settings = Settings.get();
			String v = null;
			if (ref){
				if (raw){
					v = PropertiesConstants.getRaw(settings,value,dft);
				}else{
					v = PropertiesConstants.getString(settings,value,dft);
				}
			}else{
				v = settings.transform(value);
			}
			
			if (StringUtils.isNotEmpty(v)){
				ctx.SetValue(id,v);
			}
		}
	}

}
