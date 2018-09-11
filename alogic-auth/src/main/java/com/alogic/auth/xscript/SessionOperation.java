package com.alogic.auth.xscript;

import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import com.alogic.auth.Constants;
import com.alogic.auth.Session;
import com.alogic.auth.SessionManagerFactory;
import com.alogic.cache.CacheObject;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.plugins.Segment;
import com.anysoft.util.Pair;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 针对Session对象的脚本操作
 * 
 * @author yyduan
 * @since 1.6.11.59 [20180911 duanyy]
 */
public abstract class SessionOperation extends AbstractLogiclet implements Constants{
	protected String pid = ID_SESSION;
			
	public SessionOperation(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	protected void onExecute(XsObject root,XsObject current,final LogicletContext ctx,final ExecuteWatcher watcher){
		Session session = ctx.getObject(pid);		
		if (session != null){
			onExecute(session,root,current,ctx,watcher);
		}
	}

	protected abstract void onExecute(Session session, XsObject root, XsObject current,
			LogicletContext ctx, ExecuteWatcher watcher);
	
	/**
	 * 获取基本信息
	 * @author yyduan
	 *
	 */
	public static class Info extends SessionOperation{
		public Info(String tag, Logiclet p) {
			super(tag, p);
		}
		
		@Override
		protected void onExecute(Session session, XsObject root,
				XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
			ctx.SetValue(ID_SESSION_ID, session.getId());
			ctx.SetValue(ID_SESSION_IS_LOGIN, session.getId());
			ctx.SetValue(ID_SESSION_ID, session.getId());
		}		
	}
	
	/**
	 * 设置会话状态为已登录
	 * @author yyduan
	 *
	 */
	public static class SetLoggedIn extends SessionOperation{
		public SetLoggedIn(String tag, Logiclet p) {
			super(tag, p);
		}
		
		@Override
		protected void onExecute(Session session, XsObject root,
				XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
			session.setLoggedIn(true);
		}		
	}	
	
	/**
	 * 设置会话状态为未登录
	 * @author yyduan
	 *
	 */
	public static class SetLoggedOut extends SessionOperation{
		public SetLoggedOut(String tag, Logiclet p) {
			super(tag, p);
		}
		
		@Override
		protected void onExecute(Session session, XsObject root,
				XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
			session.setLoggedIn(false);
		}		
	}	
	
	/**
	 * 将会话过期
	 * @author yyduan
	 *
	 */
	public static class Expire extends SessionOperation{
		public Expire(String tag, Logiclet p) {
			super(tag, p);
		}
		
		@Override
		protected void onExecute(Session session, XsObject root,
				XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
			session.expire();
		}		
	}
	
	/**
	 * 删除Hash的指定分组或者指定分组的指定值
	 * @author yyduan
	 *
	 */
	public static class HashDel extends SessionOperation{
		
		/**
		 * 分组
		 */
		protected String $group = CacheObject.DEFAULT_GROUP;
		
		/**
		 * 成员
		 */
		protected String $key;
		
		public HashDel(String tag, Logiclet p) {
			super(tag, p);
		}
		
		@Override
		public void configure(Properties p){
			super.configure(p);
			$group = PropertiesConstants.getRaw(p, "group", $group);
			$key = PropertiesConstants.getRaw(p, "key", $key);
		}

		@Override
		protected void onExecute(Session session, XsObject root,
				XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
			String key = PropertiesConstants.transform(ctx, $key, "");
			
			if (StringUtils.isNotEmpty(key)){
				session.hDel(
						PropertiesConstants.transform(ctx, $group, CacheObject.DEFAULT_GROUP),
						key
						);
			}else{
				session.hDel(
						PropertiesConstants.transform(ctx, $group, CacheObject.DEFAULT_GROUP)
						);			
			}
		}	
	}
	
	/**
	 * 查询Hash中指定的key是否存在
	 * @author yyduan
	 *
	 */	
	public static class HashExist extends SessionOperation{
		
		/**
		 * 分组
		 */
		protected String $group = CacheObject.DEFAULT_GROUP;
		
		/**
		 * 输出变量id
		 */
		protected String $id;
		
		/**
		 * 成员
		 */
		protected String $key;
		
		public HashExist(String tag, Logiclet p) {
			super(tag, p);
		}
		
		@Override
		public void configure(Properties p){
			super.configure(p);
			
			$id = PropertiesConstants.getRaw(p, "id", "$" + getXmlTag());
			$group = PropertiesConstants.getRaw(p, "group", $group);
			$key = PropertiesConstants.getRaw(p, "key", $key);
		}

		@Override
		protected void onExecute(Session session, XsObject root,
				XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
			String id = PropertiesConstants.transform(ctx, $id, "$" + getXmlTag());
			
			if (StringUtils.isNotEmpty(id)){
				ctx.SetValue(id, BooleanUtils.toStringTrueFalse(session.hExist(
						PropertiesConstants.transform(ctx, $group, CacheObject.DEFAULT_GROUP), 
						PropertiesConstants.transform(ctx, $key, ""))));
			}
		}
	}	
	
	/**
	 * 查询Hash中值
	 * @author yyduan
	 *
	 */		
	public static class HashGet extends SessionOperation{
		
		/**
		 * 分组
		 */
		protected String group = CacheObject.DEFAULT_GROUP;
		
		/**
		 * 输出变量id
		 */
		protected String id;
		
		/**
		 * hash key
		 */
		protected String key;
		
		/**
		 * 缺省值
		 */
		protected String dft = "";
		
		public HashGet(String tag, Logiclet p) {
			super(tag, p);
		}
		
		@Override
		public void configure(Properties p){
			super.configure(p);
			
			id = PropertiesConstants.getRaw(p, "id", "");
			group = PropertiesConstants.getRaw(p, "group", group);
			key =  PropertiesConstants.getRaw(p, "key", id);
			dft =  PropertiesConstants.getRaw(p, "dft", dft);
		}

		@Override
		protected void onExecute(Session session, XsObject root,
				XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
			String idValue = PropertiesConstants.transform(ctx, id, "");
			
			if (StringUtils.isNotEmpty(idValue)){
				ctx.SetValue(idValue, session.hGet(
						PropertiesConstants.transform(ctx, group, CacheObject.DEFAULT_GROUP), 
						PropertiesConstants.transform(ctx, key, ""), 
						PropertiesConstants.transform(ctx, dft, "")
						));
			}
		}
	}
	
	/**
	 * 查询Hash中所有值
	 * @author yyduan
	 *
	 */		
	public static class HashGetAll extends Segment{
		protected String pid = ID_SESSION;
		
		/**
		 * 分组
		 */
		protected String $group = CacheObject.DEFAULT_GROUP;
		
		/**
		 * 输出keyid
		 */
		protected String key = "$key";
		
		/**
		 * 输出值id
		 */
		protected String value = "$value";
		
		/**
		 * 缺省值
		 */
		protected String dft = "";
		
		/**
		 * 条件
		 */
		protected String condition = "*";	
		
		public HashGetAll(String tag, Logiclet p) {
			super(tag, p);
		}
		
		@Override
		public void configure(Properties p){
			super.configure(p);
			
			key = PropertiesConstants.getString(p, "key", key,true);
			value = PropertiesConstants.getString(p, "value", value,true);
			$group = PropertiesConstants.getRaw(p, "group", $group);
			condition = PropertiesConstants.getString(p,"condition", condition,true);
		}

		@Override
		protected void onExecute(XsObject root,XsObject current, LogicletContext ctx,
				ExecuteWatcher watcher) {
			Session session = ctx.getObject(pid);		
			if (session != null){
				String group = PropertiesConstants.transform(ctx,$group,CacheObject.DEFAULT_GROUP);				
				List<Pair<String,String>> result = session.hGetAll(group, condition);
				
				for (Pair<String,String> pair:result){
					ctx.SetValue(key, pair.key());
					ctx.SetValue(value, pair.value());
					super.onExecute(root, current, ctx, watcher);
				}
			}
		}	
	}	
	
	/**
	 * 设置Hash中的指定值
	 * @author yyduan
	 *
	 */
	public static class HashSet extends SessionOperation{
		
		/**
		 * 分组
		 */
		protected String $group = CacheObject.DEFAULT_GROUP;
		
		/**
		 * hash key
		 */
		protected String $key;
		
		/**
		 * 值
		 */
		protected String $value = "";
		
		protected boolean overwrite = true;
		
		/**
		 * 是否以原始值写入
		 */
		protected boolean raw = false;
		
		public HashSet(String tag, Logiclet p) {
			super(tag, p);
		}
		
		@Override
		public void configure(Properties p){
			super.configure(p);
			
			$group = PropertiesConstants.getRaw(p, "group", $group);
			$key =  PropertiesConstants.getRaw(p, "key", "");
			$value =  PropertiesConstants.getRaw(p, "value", $value);
			overwrite = PropertiesConstants.getBoolean(p,"overwrite",overwrite);
			raw = PropertiesConstants.getBoolean(p,"raw",raw);
		}

		@Override
		protected void onExecute(Session session, XsObject root,
				XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
			String key = PropertiesConstants.transform(ctx, $key, "");			
			if (StringUtils.isNotEmpty(key)){
				session.hSet(
					PropertiesConstants.transform(ctx, $group, CacheObject.DEFAULT_GROUP), 
					key, 
					raw?PropertiesConstants.getRaw(ctx,$value,""):PropertiesConstants.transform(ctx, $value, ""), 
					overwrite);
			}
		}
	}	
	
	/**
	 * 查询Hash中元素个数
	 * @author yyduan
	 *
	 */
	public static class HashSize extends SessionOperation{
		
		/**
		 * 分组
		 */
		protected String $group = CacheObject.DEFAULT_GROUP;
		
		/**
		 * 输出变量id
		 */
		protected String $id;
		
		public HashSize(String tag, Logiclet p) {
			super(tag, p);
		}
		
		@Override
		public void configure(Properties p){
			super.configure(p);
			
			$id = PropertiesConstants.getRaw(p, "id", "$" + getXmlTag());
			$group = PropertiesConstants.getRaw(p, "group", $group);
		}

		@Override
		protected void onExecute(Session session, XsObject root,
				XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
			String id = PropertiesConstants.transform(ctx, $id, "$" + getXmlTag());
			
			if (StringUtils.isNotEmpty(id)){
				ctx.SetValue(
					id, 
					String.valueOf(
						session.hLen(PropertiesConstants.transform(ctx, $group, CacheObject.DEFAULT_GROUP))
					)
				);
			}
		}
	}	
	
	/**
	 * 向Set中加入成员
	 * 
	 * @author yyduan
	 *
	 */
	public static class SetAdd extends SessionOperation{
		
		/**
		 * 分组
		 */
		protected String $group = CacheObject.DEFAULT_GROUP;
		
		/**
		 * 成员
		 */
		protected String $member;
		
		/**
		 * 是否以原始值写入
		 */
		protected boolean raw = false;
		
		public SetAdd(String tag, Logiclet p) {
			super(tag, p);
		}
		
		@Override
		public void configure(Properties p){
			super.configure(p);
			$group = PropertiesConstants.getRaw(p, "group", $group);
			$member = PropertiesConstants.getRaw(p, "member", $member);
			raw = PropertiesConstants.getBoolean(p,"raw",raw);
		}

		@Override
		protected void onExecute(Session session, XsObject root,
				XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
			session.sAdd(PropertiesConstants.transform(ctx, $group, CacheObject.DEFAULT_GROUP), 
					raw?PropertiesConstants.getRaw(ctx,$member,""):PropertiesConstants.transform(ctx, $member, ""));
		}
	}	
	
	/**
	 * 从Set中删除成员
	 * 
	 * @author yyduan
	 *
	 */
	public static class SetDel extends SessionOperation{
		
		/**
		 * 分组
		 */
		protected String $group = CacheObject.DEFAULT_GROUP;
		
		/**
		 * 成员
		 */
		protected String $member;
		
		public SetDel(String tag, Logiclet p) {
			super(tag, p);
		}
		
		@Override
		public void configure(Properties p){
			super.configure(p);
			$group = PropertiesConstants.getRaw(p, "group", $group);
			$member = PropertiesConstants.getRaw(p, "member", $member);
		}

		@Override
		protected void onExecute(Session session, XsObject root,
				XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {			
			String member = PropertiesConstants.transform(ctx, $member, "");
			
			if (StringUtils.isNotEmpty(member)){
				session.sDel(
						PropertiesConstants.transform(ctx, $group, CacheObject.DEFAULT_GROUP),
						member
						);
			}else{
				session.sDel(
						PropertiesConstants.transform(ctx, $group, CacheObject.DEFAULT_GROUP)
						);			
			}
		}
	}	
	
	/**
	 * 查询Set中成员是否存在
	 * @author yyduan
	 *
	 */
	public static class SetExist extends SessionOperation{
		
		/**
		 * 分组
		 */
		protected String $group = CacheObject.DEFAULT_GROUP;
		
		/**
		 * 输出变量id
		 */
		protected String $id;
		
		/**
		 * 成员
		 */
		protected String $member;
		
		public SetExist(String tag, Logiclet p) {
			super(tag, p);
		}
		
		@Override
		public void configure(Properties p){
			super.configure(p);
			
			$id = PropertiesConstants.getRaw(p, "id", "$" + getXmlTag());
			$group = PropertiesConstants.getRaw(p, "group", $group);
			$member = PropertiesConstants.getRaw(p, "member", $member);
		}

		@Override
		protected void onExecute(Session session, XsObject root,
				XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {		
			String id = PropertiesConstants.transform(ctx, $id, "$" + getXmlTag());
			
			if (StringUtils.isNotEmpty(id)){
				ctx.SetValue(id, BooleanUtils.toStringTrueFalse(session.sExist(
						PropertiesConstants.transform(ctx, $group, CacheObject.DEFAULT_GROUP), 
						PropertiesConstants.transform(ctx, $member, ""))));
			}
		}
	}	
	
	/**
	 * 遍历Set中的成员
	 * @author yyduan
	 *
	 */
	public static class SetMembers extends Segment{
		protected String pid = ID_SESSION;
		
		/**
		 * 分组
		 */
		protected String $group = CacheObject.DEFAULT_GROUP;
		
		/**
		 * 输出变量id
		 */
		protected String $id = "$value";
		
		/**
		 * 条件
		 */
		protected String condition = "*";
		
		public SetMembers(String tag, Logiclet p) {
			super(tag, p);
		}

		public void configure(Properties p){
			super.configure(p);
			pid = PropertiesConstants.getString(p,"pid", pid,true);
			$id = PropertiesConstants.getRaw(p, "id", $id);
			$group = PropertiesConstants.getRaw(p, "group", $group);	
			condition = PropertiesConstants.getString(p,"condition", condition,true);
		}

		@Override
		protected void onExecute(XsObject root,XsObject current, LogicletContext ctx,
				ExecuteWatcher watcher) {
			Session session = ctx.getObject(pid);
			if (session != null){
				String idValue = PropertiesConstants.transform(ctx,$id,"$value");
				String group = PropertiesConstants.transform(ctx,$group,CacheObject.DEFAULT_GROUP);				
				List<String> members = session.sMembers(group, condition);				
				for (String mem:members){
					ctx.SetValue(idValue, mem);
					super.onExecute(root, current, ctx, watcher);
				}
			}
		}	
	}
	
	/**
	 * 查询集合元素个数
	 * @author yyduan
	 *
	 */
	public static class SetSize extends SessionOperation{
		
		/**
		 * 分组
		 */
		protected String $group = CacheObject.DEFAULT_GROUP;
		
		/**
		 * 输出变量id
		 */
		protected String $id;
		
		public SetSize(String tag, Logiclet p) {
			super(tag, p);
		}
		
		@Override
		public void configure(Properties p){
			super.configure(p);
			
			$id = PropertiesConstants.getRaw(p, "id", "$" + getXmlTag());
			$group = PropertiesConstants.getRaw(p, "group", $group);
		}

		@Override
		protected void onExecute(Session session, XsObject root,
				XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
			String id = PropertiesConstants.transform(ctx, $id, "$" + getXmlTag());
			
			if (StringUtils.isNotEmpty(id)){
				ctx.SetValue(
					id, 
					String.valueOf(
						session.sSize(PropertiesConstants.transform(ctx, $group, CacheObject.DEFAULT_GROUP))
					)
				);
			}
		}

	}	
	
	/**
	 * 定位Session
	 * @author yyduan
	 *
	 */
	public static class Locate extends Segment {
		
		/**
		 * 当前节点的上下文id
		 */
		protected String cid = ID_SESSION;
		
		/**
		 * 待定位的会话id
		 */
		protected String $id;
		
		/**
		 * 结果代码
		 */
		protected String result = "$result";
		
		public Locate(String tag, Logiclet p) {
			super(tag, p);
		}

		@Override
		public void configure(Properties p){
			super.configure(p);
			$id = PropertiesConstants.getRaw(p, "id", "");		
			cid = PropertiesConstants.getString(p,"cid", cid,true);
			result = PropertiesConstants.getString(p, "result", "$" + this.getXmlTag(),true);
		}	
		
		@Override
		protected void onExecute(XsObject root,XsObject current, LogicletContext ctx,
				ExecuteWatcher watcher) {
			String id = PropertiesConstants.transform(ctx, $id, "");
			if (StringUtils.isNotEmpty(id)){
				Session found = SessionManagerFactory.getDefault().getSession(id, false);
				if (found != null){
					try {
						ctx.SetValue(result, "true");
						ctx.setObject(cid, found);
						super.onExecute(root, current, ctx, watcher);
					}finally{
						ctx.removeObject(cid);
					}
				}else{
					ctx.SetValue(result, "false");
				}
			}else{
				ctx.SetValue(result, "false");
			}
		}
	}	
}
