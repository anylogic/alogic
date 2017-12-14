package com.logicbus.kvalue.xscript;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.doc.json.JsonObject;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.kvalue.core.KeyValueRow;

/**
 * 对KeyValueRow的操作
 * 
 * @author duanyy
 *
 */
public abstract class KVRowOperation extends AbstractLogiclet{
	/**
	 * row的cid
	 */
	private String pid = "$kv-row";
	
	/**
	 * 返回结果的id
	 */
	protected String id;
	
	public KVRowOperation(String tag, Logiclet p) {
		super(tag, p);
	}
	
	public void configure(Properties p){
		super.configure(p);
		pid = PropertiesConstants.getString(p,"pid", pid,true);
		id = PropertiesConstants.getString(p,"id", "$" + getXmlTag());
	}

	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		KeyValueRow r = ctx.getObject(pid);
		if (r == null){
			throw new BaseException("core.e1001","It must be in a kvRow context,check your script.");
		}
		
		if (StringUtils.isNotEmpty(id)){
			onExecute(r,root,current,ctx,watcher);
		}
	}

	protected void onExecute(KeyValueRow row, Map<String, Object> root, Map<String, Object> current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		throw new BaseException("core.e1000",
				String.format("Tag %s does not support protocol %s",this.getXmlTag(),root.getClass().getName()));		
	}
	
	@SuppressWarnings("unchecked")
	protected void onExecute(KeyValueRow row, XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher){
		if (current instanceof JsonObject){
			onExecute(row,(Map<String,Object>)root.getContent(),(Map<String,Object>)current.getContent(),ctx,watcher);
		}		
	}
	
	protected boolean getBoolean(String value,boolean dftValue){
		try{
			return Boolean.parseBoolean(value);
		}catch (NumberFormatException ex){
			return dftValue;
		}
	}
	
	protected long getLong(String value,long dftValue){
		try{
			return Long.parseLong(value);
		}catch (NumberFormatException ex){
			return dftValue;
		}
	}	
	
	protected int getInt(String value,int dftValue){
		try{
			return Integer.parseInt(value);
		}catch (NumberFormatException ex){
			return dftValue;
		}
	}	
	
	protected double getDouble(String value,double dftValue){
		try{
			return Double.parseDouble(value);
		}catch (NumberFormatException ex){
			return dftValue;
		}
	}	
	
	protected float getFloat(String value,float dftValue){
		try{
			return Float.parseFloat(value);
		}catch (NumberFormatException ex){
			return dftValue;
		}
	}	
}