package com.alogic.together.idu;

import java.sql.Connection;
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
import com.logicbus.dbcp.sql.DBTools;

/**
 * 根据SQL语句扫描数据
 * 
 * @author yyduan
 * 
 * @since 1.6.10.1
 */
public class Scan extends Segment{
	protected String dbconn = "dbconn";
	protected String sql;
	protected String id;
	public Scan(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		dbconn = PropertiesConstants.getString(p,"dbconn", dbconn);
		sql = PropertiesConstants.getRaw(p,"sql",sql);
		id = PropertiesConstants.getString(p, "id", "$" + this.getXmlTag());
	}

	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		Connection conn = ctx.getObject(dbconn);
		if (conn == null){
			throw new BaseException("core.no_db_connection","It must be in a db context,check your together script.");
		}
		
		String listSql = ctx.transform(sql);
		
		List<Map<String,String>> result = DBTools.list(conn, listSql);
		
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