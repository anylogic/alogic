package com.alogic.cert.xscript;

import com.alogic.cert.CertificateContent;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 证书操作
 * @author yyduan
 * 
 * @since 1.6.11.9
 *
 */
public abstract class CertificateOperation extends AbstractLogiclet{
	protected String pid = "$cert";
	
	public CertificateOperation(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		pid = PropertiesConstants.getString(p,"pid",pid,true);
	}
	
	@Override
	protected void onExecute(XsObject root,XsObject current,final LogicletContext ctx,final ExecuteWatcher watcher){
		CertificateContent content = ctx.getObject(pid);
		if (content == null){
			throw new BaseException("core.e1001","It must be in a cert-child or cert-root context,check your together script.");
		}
		
		onExecute(content,root,current,ctx,watcher);
	}

	protected abstract void onExecute(CertificateContent content, XsObject root,
			XsObject current, LogicletContext ctx, ExecuteWatcher watcher);
}
