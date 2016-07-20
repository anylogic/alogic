package com.alogic.together.idu;


import com.alogic.xscript.Logiclet;
import com.alogic.xscript.plugins.Segment;


/**
 * 数据库操作命名空间
 * 
 * @author duanyy
 * @version 1.6.5.30 [duanyy 20160720] <br>
 * - 增加事务操作语句commit和rollback <br>
 */
public class IDU extends Segment{
	public IDU(String tag, Logiclet p) {
		super(tag, p);
		
		registerModule("db",DBConnection.class);
		registerModule("delete", Delete.class);
		registerModule("idExist",IdExist.class);
		registerModule("list",ListAll.class);
		registerModule("new",New.class);
		registerModule("newId",NewId.class);
		registerModule("query",Query.class);
		registerModule("update",Update.class);
		registerModule("cache",Cache.class);
		registerModule("expire",CacheClear.class);
		registerModule("load",CacheQuery.class);
		registerModule("commit",Commit.class);
		registerModule("rollback",Rollback.class);
	}
}