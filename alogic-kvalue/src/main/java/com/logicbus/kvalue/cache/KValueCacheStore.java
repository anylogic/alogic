package com.logicbus.kvalue.cache;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alogic.cache.CacheObject;
import com.alogic.load.Loader;
import com.alogic.load.Store;
import com.anysoft.util.BaseException;
import com.anysoft.util.Pager;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
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
	 * 获取id
	 * @return id
	 */
	public String getId(){
		return id;
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		id = PropertiesConstants.getString(p, "id", "",true);
		
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
		return new KValueCacheObject(rowId,hash,set,ttl);
	}
	
	@Override
	public CacheObject load(String id, boolean cacheAllowed) {
		CacheObject found = loadFromSelf(id,cacheAllowed);
		if (found == null){
			found = loadFromSink(id,cacheAllowed);
			if (found != null){
				save(id,found,true);
			}
		}		
		return found;
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
			
			SortedSetRow idRow = (SortedSetRow)idTable.select(getId(),true);
			idRow.add(id, System.currentTimeMillis());
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
			SortedSetRow idRow = (SortedSetRow)idTable.select(getId(),true);
			idRow.add(id, System.currentTimeMillis());
			return kvObject;
		}else{
			return null;
		}
	}


}
