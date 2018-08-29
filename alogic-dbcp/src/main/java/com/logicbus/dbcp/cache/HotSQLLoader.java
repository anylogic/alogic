package com.logicbus.dbcp.cache;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.w3c.dom.Element;

import com.alogic.cache.CacheObject;
import com.alogic.load.Loader;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.Script;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.doc.json.JsonObject;
import com.anysoft.util.BaseException;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.logicbus.dbcp.context.DbcpSource;
import com.logicbus.dbcp.core.ConnectionPool;
import com.logicbus.dbcp.sql.DBTools;

/**
 * SQL热加载
 * 
 * @author yyduan
 * @since 1.6.11.45 
 * 
 * @version 1.6.11.58 [20180829 duanyy] <br>
 * - 修正获取onLoad节点信息的执行次序问题 <br>
 */
public class HotSQLLoader extends Loader.Hot<CacheObject>{
	/**
	 * 上一次扫描时间
	 */
	protected long lastScanTime = 0;

	/**
	 * 数据库连接池id
	 */
	protected String dbcpId = "default";
	
	/**
	 * SQL语句
	 */
	protected String sql;
	
	/**
	 * 加载事件脚本
	 */
	protected Logiclet onLoad = null;
	
	protected String cacheObjectId = "$cache-object";
	
	@Override
	public void configure(Properties p){		
		sql = PropertiesConstants.getString(p,"sql","");
		dbcpId = PropertiesConstants.getString(p,"dbcpId","default");		
		cacheObjectId = PropertiesConstants.getString(p,"cacheObjectId",cacheObjectId,true);
		super.configure(p);
	}
	
	@Override
	public void configure(Element e, Properties p) {
		Properties props = new XmlElementProperties(e,p);

		Element onLoadElem = XmlTools.getFirstElementByPath(e, "on-load");
		if (onLoadElem != null){
			onLoad = Script.create(onLoadElem, props);
		}	
		
		configure(props);
	}	
	
	@Override
	protected synchronized void doLoad(boolean first) {
		ConnectionPool pool = DbcpSource.getPool(dbcpId);
		if (pool == null){
			LOG.error("Can not find the dbcp pool:" + dbcpId);
			return ;
		}
		
		Connection conn = null;
		boolean error = false;
		try {
			conn = pool.getConnection();
			if (conn != null){			
				List<Map<String,Object>> result = DBTools.listAsObject(conn,sql,lastScanTime);
				
				for (Map<String,Object> map:result){
					String id = JsonTools.getString(map, "id", "");
					if (StringUtils.isNotEmpty(id)){
						CacheObject found = new CacheObject.Simple(id);
						found.fromJson(map);
						
						if (onLoad != null){
							LogicletContext logicletContext = new LogicletContext(Settings.get());					
							try {
								logicletContext.setObject(cacheObjectId, found);
								XsObject doc = new JsonObject("root",new HashMap<String,Object>());
								onLoad.execute(doc,doc, logicletContext, null);
							}catch (Exception ex){
								LOG.info("Failed to execute onload script" + ExceptionUtils.getStackTrace(ex));
							}finally{
								logicletContext.removeObject(cacheObjectId);
							}
						}						
						
						add(id, found);
					}
				}
				lastScanTime = System.currentTimeMillis();
			}else{
				LOG.error("Can not get a db connection from pool + " + dbcpId);
			}
		}catch (BaseException ex){
			error = true;
			LOG.error("Error when load from db:" + dbcpId);
			LOG.error(ExceptionUtils.getStackTrace(ex));
		}finally{
			pool.recycle(conn, error);
		}
	}

}
