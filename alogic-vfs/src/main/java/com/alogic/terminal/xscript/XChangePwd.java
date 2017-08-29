package com.alogic.terminal.xscript;

import com.alogic.terminal.Resolver;
import com.alogic.terminal.Terminal;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 执行修改密码指令
 * @author yyduan
 * @since 1.6.9.9
 * @version 1.6.9.9 [20170829 duanyy] <br>
 * - 增加ssh改密码功能 <br>
 */
public class XChangePwd extends AbstractLogiclet implements Resolver{
	/**
	 * 父节点id
	 */
	protected String pid = "$xshell";
	
	protected String pwd = "";
	
	public XChangePwd(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public Object resolveBegin(String cmd) {
		return this;
	}

	@Override
	public void resolveLine(Object cookies, String content) {
		log(content, "info");
	}

	@Override
	public void resolveEnd(Object cookies) {
		// nothing to do
	}
	
	@Override 
	public void configure(Properties p){
		super.configure(p);
		
		pid = PropertiesConstants.getString(p,"pid", pid , true);
		pwd =  PropertiesConstants.getRaw(p,"value", "");
	}

	@Override
	protected void onExecute(XsObject root,XsObject current,final LogicletContext ctx,final ExecuteWatcher watcher){
		Terminal t = ctx.getObject(pid);
		if (t == null){
			throw new BaseException("core.no_terminal","It must be in a xshell context,check your together script.");
		}
		
		String password = ctx.transform(pwd);
		if (!t.changePassword(password, this)){
			throw new BaseException("core.failed","Failed to modify password");
		}
	}
}