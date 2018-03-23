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
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.dbcp.processor.Preprocessor;
import com.logicbus.dbcp.sql.DBTools;

/**
 * 查询单条记录
 * 
 * @author yyduan
 * @since 1.6.11.24
 */
public class Select extends DBOperation{
	protected String sqlQuery = "";
	protected Preprocessor processor = null;
	protected String id;
	public Select(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		sqlQuery = PropertiesConstants.getString(p, "sql", sqlQuery);
		id = PropertiesConstants.getString(p, "id", "$" + this.getXmlTag(),true);
		processor = new Preprocessor(sqlQuery);
	}

	@Override
	protected void onExecute(Connection conn, final XsObject root,final XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		List<Object> data = new ArrayList<Object>();
		String sql = processor.process(ctx, data);
		Map<String,String> result = DBTools.select(conn, sql,data.toArray());
		if (result != null){
			Iterator<Entry<String,String>> iter = result.entrySet().iterator();
			while (iter.hasNext()){
				Entry<String,String> entry = iter.next();				
				ctx.SetValue(entry.getKey(), entry.getValue());
			}
			ctx.SetValue(id, "1");
		}else{
			ctx.SetValue(id, "0");
		}
	}
}