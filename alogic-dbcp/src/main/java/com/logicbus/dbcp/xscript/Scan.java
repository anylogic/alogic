package com.logicbus.dbcp.xscript;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.plugins.Segment;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.dbcp.processor.Preprocessor;
import com.logicbus.dbcp.sql.DBTools;

/**
 * 根据SQL语句扫描数据
 * @since 1.6.10.5
 * 
 * @version 1.6.11.17 [20180209 duanyy] <br>
 * - 支持SQL预处理; <br>
 * 
 * @version 1.6.11.24 [20180323 duanyy]
 * - 修正id取值问题 <br>
 * 
 * @version 1.6.11.27 [20180417 duanyy] <br>
 * - 增加debug参数 <br>
 */
public class Scan extends Segment{
	protected String dbconn = "dbconn";
	protected String sql;
	protected String id;
	protected Preprocessor processor = null;
	protected boolean debug = false;
	public Scan(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		dbconn = PropertiesConstants.getString(p,"dbconn", dbconn);
		sql = PropertiesConstants.getRaw(p,"sql",sql);
		id = PropertiesConstants.getString(p, "id", "$" + this.getXmlTag(),true);
		debug = PropertiesConstants.getBoolean(p,"debug", debug,true);
		processor = new Preprocessor(sql);
	}

	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		Connection conn = ctx.getObject(dbconn);
		if (conn == null){
			throw new BaseException("core.e1001","It must be in a db context,check your together script.");
		}
		
		List<Object> data = new ArrayList<Object>();
		String sql = processor.process(ctx, data);
		
		if (debug){
			log("sql=" + sql,"debug");
			log("binded=" + data.toString(),"debug");
		}
		
		List<Map<String,String>> result = DBTools.list(conn, sql,data.toArray());
		
		if (result != null && result.size() > 0){
			ctx.SetValue(id, String.valueOf(result.size()));
			for (Map<String,String> item:result){
				Iterator<Entry<String,String>> iter = item.entrySet().iterator();
				while (iter.hasNext()){
					Entry<String,String> entry = iter.next();				
					ctx.SetValue(entry.getKey(), entry.getValue());
				}
				super.onExecute(root, current, ctx, watcher);
			}
		}else{
			ctx.SetValue(id, "0");
		}
	}
}