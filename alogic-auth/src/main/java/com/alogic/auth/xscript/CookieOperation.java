package com.alogic.auth.xscript;

import org.apache.commons.lang3.StringUtils;

import com.alogic.auth.Constants;
import com.alogic.auth.CookieManager;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * Cookie操作
 * @author yyduan
 * @since 1.6.11.59 [20180911 duanyy]
 */
public abstract class CookieOperation extends AbstractLogiclet implements Constants{
	protected String pid = ID_COOKIES;
			
	public CookieOperation(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	protected void onExecute(XsObject root,XsObject current,final LogicletContext ctx,final ExecuteWatcher watcher){
		CookieManager p = ctx.getObject(pid);		
		if (p != null){
			onExecute(p,root,current,ctx,watcher);
		}
	}

	protected abstract void onExecute(CookieManager cm, XsObject root, XsObject current,
			LogicletContext ctx, ExecuteWatcher watcher);
	
	/**
	 * 设置属性
	 * @author yyduan
	 *
	 */
	public static class SetCookie extends CookieOperation{
		protected String $id;
		protected String $value;
		protected String $path = "/";
		protected String $ttl = "1800";
		
		public SetCookie(String tag, Logiclet p) {
			super(tag, p);
		}
		
		@Override
		public void configure(Properties p){
			super.configure(p);
			
			$id = PropertiesConstants.getRaw(p,"id",$id);
			$value = PropertiesConstants.getRaw(p,"value",$value);
			$path = PropertiesConstants.getRaw(p,"path",$path);
			$ttl = PropertiesConstants.getRaw(p,"ttl",$ttl);
		}
		
		@Override
		protected void onExecute(CookieManager cm, XsObject root,
				XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
			String id = PropertiesConstants.transform(ctx,$id,"");
			if (StringUtils.isNotEmpty(id)){
				cm.setCookie(id, 
					PropertiesConstants.transform(ctx, $value, ""), 
					PropertiesConstants.transform(ctx, $path, "/"), 
					PropertiesConstants.transform(ctx, $ttl, 18000)
				);
			}
		}		
	}	
	/**
	 * 获取属性
	 * @author yyduan
	 *
	 */
	public static class GetCookie extends CookieOperation{
		protected String $id;
		protected String $dft;
		protected String $key;
		
		public GetCookie(String tag, Logiclet p) {
			super(tag, p);
		}
		
		@Override
		public void configure(Properties p){
			super.configure(p);
			
			$id = PropertiesConstants.getRaw(p,"id",$id);
			$key = PropertiesConstants.getRaw(p,"key",$key);
			$dft = PropertiesConstants.getRaw(p,"dft",$dft);
		}
		
		@Override
		protected void onExecute(CookieManager cm, XsObject root,
				XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
			String id = PropertiesConstants.transform(ctx,$id,"");
			String key = PropertiesConstants.transform(ctx, $key, id);
			if (StringUtils.isNotEmpty(id)){
				ctx.SetValue(id,cm.getCookie(key, PropertiesConstants.transform(ctx, $dft, "")));
			}
		}		
	}		
}
