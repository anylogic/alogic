package com.alogic.ac.loader.aak;

import java.sql.Connection;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.alogic.ac.AccessAppKey;
import com.alogic.load.Loader;
import com.anysoft.util.BaseException;
import com.anysoft.util.Factory;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.dbcp.context.DbcpSource;
import com.logicbus.dbcp.core.ConnectionPool;
import com.logicbus.dbcp.sql.DBTools;

/**
 * AAK数据库装载器
 * @author yyduan
 * @since 1.6.10.6
 */
public class AAKFromMysql extends Loader.Abstract<AccessAppKey>{
	
	/**
	 * 数据库连接池id
	 */
	protected String dbcpId = "default";
	
	/**
	 * 应用域
	 */
	protected String domain = "frontend";
	
	protected String aakModule = AccessAppKey.Default.class.getName();
	
	@Override
	public void configure(Properties p) {
		super.configure(p);
		
		dbcpId = PropertiesConstants.getString(p, "dbcpId", dbcpId);
		domain = PropertiesConstants.getString(p, "domain", domain);
		aakModule = PropertiesConstants.getString(p, "aak", aakModule,true);
	}
	
	@Override
	public AccessAppKey load(String id, boolean cacheAllowed) {
		ConnectionPool pool = DbcpSource.getPool(dbcpId);
		if (pool == null){
			LOG.error("Can not find the connection pool named:" + dbcpId);
			return null;
		}
		
		Connection conn = null;
		boolean error = false;
		try {
			conn = pool.getConnection();
			if (conn != null){
				return loadAAK(conn,id);
			}else{
				LOG.error("Can not get db connection from pool:" + dbcpId);
				return null;
			}		
		}catch (BaseException ex){
			LOG.error(ExceptionUtils.getStackTrace(ex));
			return null;
		}finally{
			pool.recycle(conn, error);
		}

	}

	protected AccessAppKey loadAAK(Connection conn, String id) {
		AccessAppKey aak = null;
		
		Map<String,Object> result = DBTools.selectAsObjects(
				conn,
				"select app_key id,app_id appId,verifier,app_key_content keyContent from acl_app_key where app_key = ? and domain=?",
				id,
				domain);
		
		if (result != null){
			Factory<AccessAppKey> f = new Factory<AccessAppKey>();
			try {
				aak = f.newInstance(aakModule);
				aak.fromJson(result);
			}catch (Exception ex){
				LOG.error("Can not create aak instance:" + id);
				LOG.error(ExceptionUtils.getStackTrace(ex));
			}
		}
		
		return aak;
	}

}