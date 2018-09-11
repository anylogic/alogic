package com.alogic.auth.xscript;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.alogic.auth.Constants;
import com.alogic.auth.Principal;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.plugins.Segment;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 针对Principal对象的脚本操作
 * 
 * @author yyduan
 * @since 1.6.11.59 [20180911 duanyy]
 */
public abstract class PrincipalOperation extends AbstractLogiclet implements Constants{
	protected String pid = ID_PRINCIPAL;
			
	public PrincipalOperation(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	protected void onExecute(XsObject root,XsObject current,final LogicletContext ctx,final ExecuteWatcher watcher){
		Principal p = ctx.getObject(pid);		
		if (p != null){
			onExecute(p,root,current,ctx,watcher);
		}
	}

	protected abstract void onExecute(Principal p, XsObject root, XsObject current,
			LogicletContext ctx, ExecuteWatcher watcher);
	
	/**
	 * 获取基本信息
	 * @author yyduan
	 *
	 */
	public static class Info extends PrincipalOperation{
		public Info(String tag, Logiclet p) {
			super(tag, p);
		}
		
		@Override
		protected void onExecute(Principal p, XsObject root,
				XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
			ctx.SetValue(ID_PRINCIPAL_ID,p.getId());
			ctx.SetValue(ID_PRINCIPAL_LOGIN_ID, p.getLoginId());
			ctx.SetValue(ID_PRINCIPAL_LOGIN_IP, p.getLoginIp());
			ctx.SetValue(ID_PRINCIPAL_LOGIN_TIME, p.getLoginTime());
			ctx.SetValue(ID_PRINCIPAL_APP, p.getAppId());
		}		
	}
	
	/**
	 * 过期
	 * @author yyduan
	 *
	 */
	public static class Expire extends PrincipalOperation{
		public Expire(String tag, Logiclet p) {
			super(tag, p);
		}
		
		@Override
		protected void onExecute(Principal p, XsObject root,
				XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
			p.expire();
		}		
	}
	
	/**
	 * 设置属性
	 * @author yyduan
	 *
	 */
	public static class SetProperty extends PrincipalOperation{
		protected String $id;
		protected String $value;
		protected String $overwrite = "true";
		
		public SetProperty(String tag, Logiclet p) {
			super(tag, p);
		}
		
		@Override
		public void configure(Properties p){
			super.configure(p);
			
			$id = PropertiesConstants.getRaw(p,"id",$id);
			$value = PropertiesConstants.getRaw(p,"value",$value);
			$overwrite = PropertiesConstants.getRaw(p,"overwrite",$overwrite);
		}
		
		@Override
		protected void onExecute(Principal p, XsObject root,
				XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
			String id = PropertiesConstants.transform(ctx,$id,"");
			if (StringUtils.isNotEmpty(id)){
				p.setProperty(
					id, 
					PropertiesConstants.transform(ctx, $value, ""), 
					PropertiesConstants.transform(ctx, $overwrite, true)
				);
			}
		}		
	}	
	/**
	 * 获取属性
	 * @author yyduan
	 *
	 */
	public static class GetProperty extends PrincipalOperation{
		protected String $id;
		protected String $dft;
		protected String $key;
		
		public GetProperty(String tag, Logiclet p) {
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
		protected void onExecute(Principal p, XsObject root,
				XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
			String id = PropertiesConstants.transform(ctx,$id,"");
			String key = PropertiesConstants.transform(ctx, $key, id);
			if (StringUtils.isNotEmpty(id)){
				ctx.SetValue(id,p.getProperty(key, PropertiesConstants.transform(ctx, $dft, "")));
			}
		}		
	}		
	
	/**
	 * 清除属性及权限列表
	 * @author yyduan
	 *
	 */
	public static class Clear extends PrincipalOperation{
		
		public Clear(String tag, Logiclet p) {
			super(tag, p);
		}
		
		@Override
		protected void onExecute(Principal p, XsObject root,
				XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
			p.clearPrivileges();
			p.clearProperties();
		}		
	}
	
	/**
	 * 增加权限
	 * @author yyduan
	 *
	 */
	public static class AddPrivileges extends PrincipalOperation{
		protected String $privilege = "";
		protected String delimiter = ",";
		
		public AddPrivileges(String tag, Logiclet p) {
			super(tag, p);
		}
		
		@Override
		public void configure(Properties p){
			super.configure(p);
			
			delimiter = PropertiesConstants.getString(p,"delimiter",delimiter);
			$privilege = PropertiesConstants.getRaw(p,"plist",$privilege);
		}
		
		@Override
		protected void onExecute(Principal p, XsObject root,
				XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
			p.addPrivileges(PropertiesConstants.transform(ctx,$privilege,"").split(delimiter));
		}		
	}		
	
	/**
	 * 是否具备权限
	 * @author yyduan
	 *
	 */
	public static class HasPrivileges extends PrincipalOperation{
		protected String $privilege = "";
		protected String delimiter = ",";
		protected String id;
		
		public HasPrivileges(String tag, Logiclet p) {
			super(tag, p);
		}
		
		@Override
		public void configure(Properties p){
			super.configure(p);
			
			delimiter = PropertiesConstants.getString(p,"delimiter",delimiter);
			$privilege = PropertiesConstants.getRaw(p,"plist",$privilege);
			id = PropertiesConstants.getString(p,"id","$" + this.getXmlTag());
		}
		
		@Override
		protected void onExecute(Principal p, XsObject root,
				XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
			boolean hasPrivilege = false;
			String[] plist = PropertiesConstants.transform(ctx,$privilege,"").split(delimiter);
			for (String privilege:plist){
				if (p.hasPrivilege(privilege)){
					hasPrivilege = true;
					break;
				}
			}			
			ctx.SetValue(id, BooleanUtils.toStringTrueFalse(hasPrivilege));
		}		
	}	
	
	/**
	 * 获取权限列表
	 * @author yyduan
	 *
	 */
	public static class ListPrivileges extends Segment{
		protected String pid = ID_PRINCIPAL_ID;
		protected String $id = "$value";
		
		public ListPrivileges(String tag, Logiclet p) {
			super(tag, p);
		}
		
		@Override
		public void configure(Properties p){
			super.configure(p);			
			$id = PropertiesConstants.getRaw(p,"id",$id);
		}
		
		@Override
		protected void onExecute(XsObject root,
				XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
			Principal p = ctx.getObject(pid);		
			if (p != null){
				List<String> plist = p.getPrivileges();
				for (String privilege:plist){
					ctx.SetValue(PropertiesConstants.transform(ctx, $id, "$value"),privilege);
				}
			}
		}		
	}	
}

