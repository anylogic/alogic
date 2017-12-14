package com.alogic.terminal.xscript;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

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
 * 执行普通指令
 * 
 * @author yyduan
 * 
 * @since 1.6.9.9
 * @version 1.6.9.9 [20170829 duanyy] <br>
 * - 增加ssh改密码功能 <br>
 */
public class XCmd extends AbstractLogiclet implements Resolver{
	protected String pid = "$xshell";
	
	/**
	 * 指令
	 */
	protected List<String> cmds = new ArrayList<String>();	
	
	public XCmd(String tag, Logiclet p) {
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
		
		String value = PropertiesConstants.getRaw(p,"value","");
		if (StringUtils.isNotEmpty(value)){
			String [] list = value.split(";");
			for (String one:list){
				if (StringUtils.isNotEmpty(one)){
					cmds.add(one);
				}
			}
		}
	}

	@Override
	protected void onExecute(XsObject root,XsObject current,final LogicletContext ctx,final ExecuteWatcher watcher){
		Terminal t = ctx.getObject(pid);
		if (t == null){
			throw new BaseException("core.e1001","It must be in a xshell context,check your together script.");
		}
		
		for (String cmd:cmds){
			String transformed = ctx.transform(cmd);
			if (StringUtils.isNotEmpty(transformed)){
				t.exec(this,transformed);
			}
		}
	}
}
