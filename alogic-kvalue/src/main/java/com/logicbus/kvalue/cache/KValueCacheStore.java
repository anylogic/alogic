package com.logicbus.kvalue.cache;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alogic.cache.CacheObject;
import com.alogic.load.Loader;
import com.alogic.load.Store;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.Script;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.doc.json.JsonObject;
import com.anysoft.util.BaseException;
import com.anysoft.util.Factory;
import com.anysoft.util.Pager;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.logicbus.kvalue.context.KValueSource;
import com.logicbus.kvalue.core.HashRow;
import com.logicbus.kvalue.core.Schema;
import com.logicbus.kvalue.core.SetRow;
import com.logicbus.kvalue.core.SortedSetRow;
import com.logicbus.kvalue.core.Table;

/**
 * 基于KValue的缓存Store实现
 * 
 * @author yyduan
 * @since 1.6.11.13
 * 
 * @version 1.6.11.20 [20180223 duanyy] <br>
 * - 修正缓存对象id的问题 <br>
 * - 缓存的idtable功能可选,默认管理<br>
 * 
 * @version 1.6.11.29 [20180510 duanyy]
 * - 增加on-load事件处理;
 * 
 * @version 1.6.11.45 [duanyy 20180722] <br>
 * - Sinkable实现增加nocache模式;
 * 
 * @version 1.6.11.58 [20180829 duanyy] <br>
 * - 修正on-load之后，所注销对象的变量id <br>
 */
public class KValueCacheStore extends Loader.Sinkable<CacheObject> implements Store<CacheObject>{

	/**
	 * 用于保存Hash类数据的表
	 */
	protected Table hashTable;
	
	/**
	 * 用于保存Set类数据的表
	 */
	protected Table setTable;
	
	/**
	 * 用于保存id的表
	 */
	protected Table idTable;
	
	/**
	 * 本缓存的id
	 */
	protected String id;
	
	/**
	 * 是否在idTable中保存id列表
	 */
	protected boolean enableIdTable = false;
	
	/**
	 * 加载事件脚本
	 */
	protected Logiclet onLoad = null;
	
	protected String cacheObjectId = "$cache-object";
	
	/**
	 * 获取id
	 * @return id
	 */
	public String getId(){
		return id;
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		cacheObjectId = PropertiesConstants.getString(p,"cacheObjectId",cacheObjectId,true);
		
		id = PropertiesConstants.getString(p, "id", "",true);
		enableIdTable = PropertiesConstants.getBoolean(p, "table.id.enable", enableIdTable);
		String schema = PropertiesConstants.getString(p,"schema","redis");
		String hashTableName = PropertiesConstants.getString(p,"table.hash","m");
		String setTableName = PropertiesConstants.getString(p,"table.set","s");
		String idtableName = PropertiesConstants.getString(p,"table.id","i");
		Schema instance = KValueSource.getSchema(schema);
		if (instance == null){
			throw new BaseException("core.e1003","Can not find a kvalue schema named " + schema);
		}
		
		hashTable = instance.getTable(hashTableName);
		if (hashTable == null){
			throw new BaseException("core.e1003","Can not find a kvalue table named " + hashTable);
		}
		
		setTable = instance.getTable(setTableName);
		if (setTable == null){
			throw new BaseException("core.e1003","Can not find a kvalue table named " + setTableName);
		}			
		
		idTable = instance.getTable(idtableName);
		if (idTable == null){
			throw new BaseException("core.e1003","Can not find a kavalue table named " + idtableName);
		}
	}
	
	@Override
	public void configure(Element e, Properties p) {
		Properties props = new XmlElementProperties(e,p);
		configure(props);
		
		NodeList nodeList = XmlTools.getNodeListByPath(e, getSinkTag());
		Factory<Loader<CacheObject>> factory = new Factory<Loader<CacheObject>>();
		String scope = PropertiesConstants.getString(p, "ketty.scope", "runtime");
		
		for (int i = 0 ;i < nodeList.getLength() ; i ++){
			Node n = nodeList.item(i);
			
			if (Node.ELEMENT_NODE != n.getNodeType()){
				continue;
			}
			
			Element elem = (Element)n;
			
			String itemScope = XmlTools.getString(elem, "scope", "");
			if (StringUtils.isNotEmpty(itemScope) && !itemScope.equals(scope)){
				continue;
			}
			
			try {
				Loader<CacheObject> loader = factory.newInstance(elem, props, "module");
				if (loader != null){
					loaders.add(loader);
				}
			}catch (Exception ex){
				LOG.error("Can not create loader from element:" + XmlTools.node2String(elem));
				LOG.error(ExceptionUtils.getStackTrace(ex));
			}
		}
		
		Element onLoadElem = XmlTools.getFirstElementByPath(e, "on-load");
		if (onLoadElem != null){
			onLoad = Script.create(onLoadElem, props);
		}
	}	
	
	/**
	 * 根据对象id生成在缓存中的id
	 * @param id 对象id
	 * @return 缓存中的id
	 */
	protected String getRowId(String id){
		return this.getId() + '$' + id;
	}
	
	/**
	 * 根据id生成一个缓存对象(该对象不一定存在数据)
	 * @param id 对象id
	 * @return 缓存对象
	 */
	protected CacheObject getCacheObject(String id){
		String rowId = getRowId(id);
		HashRow hash = (HashRow) hashTable.select(rowId, true);
		SetRow set = (SetRow) setTable.select(rowId, true);
		
		long ttl = this.getTTL();
		if (ttl <= 0){
			ttl = 30 * 60 * 1000L;
		}
		return new KValueCacheObject(id,hash,set,ttl);
	}
	
	@Override
	public CacheObject load(String id, boolean cacheAllowed) {
		if (noCache()){
			return loadFromSink(id,cacheAllowed);
		}else{
			CacheObject found = loadFromSelf(id,cacheAllowed);
			if (found == null){
				found = loadFromSink(id,cacheAllowed);
				if (found != null){
					onLoad(id,found);
					save(id,found,true);
				}
			}		
			return found;
		}
	}
	

	protected void onLoad(String id, CacheObject cache) {
		if (onLoad != null){
			LogicletContext logicletContext = new LogicletContext(Settings.get());
	
			try {
				logicletContext.setObject(cacheObjectId, cache);
				XsObject doc = new JsonObject("root",new HashMap<String,Object>());
				onLoad.execute(doc,doc, logicletContext, null);
			}catch (Exception ex){
				LOG.info("Failed to execute onload script" + ExceptionUtils.getStackTrace(ex));
			}finally{
				logicletContext.removeObject(cacheObjectId);
			}
		}
	}

	@Override
	public CacheObject newObject(String id) {
		return getCacheObject(id);
	}
	
	@Override
	public void save(String id, CacheObject o, boolean overwrite) {
		if (o != null){
			CacheObject kvObject = getCacheObject(id);
			o.copyTo(kvObject);
			
			if (enableIdTable){
				SortedSetRow idRow = (SortedSetRow)idTable.select(getId(),true);
				idRow.add(id, System.currentTimeMillis());
			}
		}
	}

	@Override
	public void del(String id) {
		CacheObject kvObject = getCacheObject(id);
		if (kvObject.isValid()){
			kvObject.expire();
		}
	}

	@Override
	public void scan(List<String> result, Pager pager) {
		SortedSetRow idRow = (SortedSetRow)idTable.select(getId(),true);
		long max = System.currentTimeMillis();
		long min = max - getTTL();
		List<String> ids = idRow.rangeByScore(min, max, true, pager.getOffset(), pager.getLimit());
		long all = idRow.count(min, max);
		
		String keyword = pager.getKeyword();
		int offset = pager.getOffset();
		int limit = pager.getLimit();
	
		int current = 0;
		for (String id:ids){
			boolean match = StringUtils.isEmpty(pager.getKeyword()) || id.contains(keyword);
			if (match){
				if (current >= offset && current < offset + limit){
					result.add(id);
				}
				current ++;
			}
		}
		
		pager.setAll(all).setTotal(current);
	}

	@Override
	protected CacheObject loadFromSelf(String id, boolean cacheAllowed) {
		CacheObject kvObject = getCacheObject(id);
		if (kvObject.isValid()){
			if (enableIdTable){
				SortedSetRow idRow = (SortedSetRow)idTable.select(getId(),true);
				idRow.add(id, System.currentTimeMillis());
			}
			return kvObject;
		}else{
			return null;
		}
	}


}
