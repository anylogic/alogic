package com.alogic.together.idu;

import java.sql.Connection;
import java.sql.SQLException;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;

/**
 * 提交事务
 * 
 * @author duanyy
 * @version 1.6.8.14 [20170509 duanyy] <br>
 * - 增加xscript的中间文档模型,以便支持多种报文协议 <br>
 */
public class Commit extends DBOperation {

	public Commit(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	protected void onExecute(Connection conn, XsObject root,XsObject current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		try {
			conn.commit();
		} catch (SQLException e) {
			logger.error("Failed to commit transaction",e);
		}
	}

}
