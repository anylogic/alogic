package com.alogic.together.idu;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.backend.ServantException;
import com.logicbus.dbcp.processor.Preprocessor;
import com.logicbus.dbcp.sql.DBTools;

/**
 * 校验ID是否存在
 * 
 * @author duanyy
 *
 */
public class IdExist extends DBOperation {
	protected String sqlIdExist = "select 1 from dual";
	protected Preprocessor processor = null;
	
	public IdExist(String tag, Logiclet p) {
		super(tag, p);
	}

	public void configure(Properties p){
		super.configure(p);
		sqlIdExist = PropertiesConstants.getString(p, "sql.IdExist", sqlIdExist);
		processor = new Preprocessor(sqlIdExist);
	}
	
	@Override
	protected void onExecute(Connection conn, Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		List<Object> binded = new ArrayList<Object>();
		String sql = processor.process(ctx, binded);
		int exist = DBTools.selectAsInt(conn, sql, 1, binded.toArray());
		if (exist > 0){
			throw new ServantException("core.id_used","The given id has been used.");
		}
	}

}
