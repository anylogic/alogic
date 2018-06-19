package com.alogic.xscript.plugins;

import org.apache.commons.lang3.StringUtils;

import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.code.Coder;
import com.anysoft.util.code.CoderFactory;

/**
 * 生成可共享的URL地址
 * 
 * @author yyduan
 *
 */
public class Share extends AbstractLogiclet {
	protected String $in = "";
	protected String id;
	protected Coder coder = null;
	protected String key = "alogic";
	protected String urlBase = "/share/";
	
	public Share(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p) {
		super.configure(p);
		
		$in = 
		id = PropertiesConstants.getRaw(p,"id", "$" + this.getXmlTag());
		
		key = PropertiesConstants.getString(p,"share.key", key,false);		
		coder = CoderFactory.newCoder(PropertiesConstants.getString(p,"share.coder","DES3"));
		urlBase = PropertiesConstants.getString(p,"share.base", urlBase,false);	
	}		
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		String in = ctx.transform($in);
		if (StringUtils.isNotEmpty(in) && StringUtils.isNotEmpty(id)){
			String out = coder.encode(in, key);
			ctx.SetValue(id, urlBase + out);
		}
	}
	
	protected String getDataPattern(Properties p){
		return PropertiesConstants.getRaw(p,"in", $in);
	}
	
	public static class VFS extends Share{

		public VFS(String tag, Logiclet p) {
			super(tag, p);
		}
		
		protected String getDataPattern(Properties p){
			return "/services/component/vfs/Download?path=${path}&domain=${domain}";
		}		
	}
	
	public static class Blob extends Share{

		public Blob(String tag, Logiclet p) {
			super(tag, p);
		}
		
		protected String getDataPattern(Properties p){
			return "/services/component/blob/Download?fileId=${fileId}&domain=${domain}";
		}		
	}	
}