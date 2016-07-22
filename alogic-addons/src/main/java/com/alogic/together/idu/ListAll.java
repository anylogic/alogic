package com.alogic.together.idu;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.dbcp.processor.Preprocessor;
import com.logicbus.dbcp.sql.DBTools;

/**
 * 执行查询语句
 * 
 * @author duanyy
 *
 */
public class ListAll extends DBOperation{
	protected String tag = "data";
	protected String sqlQuery = "";	
	protected Preprocessor processor = null;
	
	public ListAll(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		tag = PropertiesConstants.getRaw(p, "tag", tag);
		sqlQuery = PropertiesConstants.getString(p, "sql.Query", sqlQuery);
		processor = new Preprocessor(sqlQuery);
	}

	@Override
	protected void onExecute(Connection conn, Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		String tagValue = ctx.transform(tag);
		if (StringUtils.isNotEmpty(tagValue)){
			List<Object> data = new ArrayList<Object>();
			String sql = processor.process(ctx, data);
			List<Map<String,Object>> result = DBTools.listAsObject(conn, sql,data.toArray());
			current.put(tagValue, result);
		}
	}
}
