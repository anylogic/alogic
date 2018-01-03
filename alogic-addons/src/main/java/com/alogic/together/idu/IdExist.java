package com.alogic.together.idu;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
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
 * @version 1.6.8.14 [20170509 duanyy] <br>
 * - 增加xscript的中间文档模型,以便支持多种报文协议 <br>
 * @deprecated
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
	protected void onExecute(Connection conn, XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		List<Object> binded = new ArrayList<Object>();
		String sql = processor.process(ctx, binded);
		int exist = DBTools.selectAsInt(conn, sql, 1, binded.toArray());
		if (exist > 0){
			throw new ServantException("clnt.e2008","The given id has been used.");
		}
	}

}
