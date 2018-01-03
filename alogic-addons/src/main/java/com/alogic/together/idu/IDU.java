package com.alogic.together.idu;


import com.alogic.xscript.Logiclet;
import com.alogic.xscript.plugins.Segment;


/**
 * 数据库操作命名空间
 * 
 * @author duanyy
 * @version 1.6.5.30 [duanyy 20160720] <br>
 * - 增加事务操作语句commit和rollback <br>
 * 
 * @version 1.6.10.1 [duanyy 20170911] <br>
 * - DB和Cache操作增加相应的前缀 <br>
 * 
 * @deprecated
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
		registerModule("scan",Scan.class);
		
		registerModule("db-del", Delete.class);
		registerModule("db-id-exist",IdExist.class);
		registerModule("db-list",ListAll.class);
		registerModule("db-insert",New.class);
		registerModule("db-sequence",NewId.class);
		registerModule("db-select",Query.class);
		registerModule("db-update",Update.class);
		registerModule("cache-expire",CacheClear.class);
		registerModule("cache-load",CacheQuery.class);
		registerModule("db-commit",Commit.class);
		registerModule("db-rollback",Rollback.class);
		registerModule("db-scan",Scan.class);		
	}
}