package com.anysoft.cache;

import java.util.Hashtable;
import java.util.Map;

import org.w3c.dom.Element;

import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;

/**
 * 基于内存的DataStore
 * @author duanyy
 *
 * @param <data>
 * 
 * @version 1.5.2 [20141017 duanyy]
 * - 淘汰ChangeAware机制，采用更为通用的Watcher
 * 
 */
public class Memory<data extends Cacheable> extends AbstractDataStore<data> {

	
	public void save(String id, data _data) throws BaseException {
		datas.put(id, _data);
		if (watchers != null){
			watchers.changed(id, _data);
		}
	}

	
	public data load(String id) {
		return load(id,true);
	}

	
	public data load(String id, boolean cacheAllowed) {
		return datas.get(id);
	}

		
	
	public void close() throws Exception {
		datas.clear();
	}

	
	public void create(Properties props) throws BaseException {
		
	}

	
	public void refresh() throws BaseException {
		datas.clear();
	}
	
	/**
	 * 缓存数据
	 */
	protected Hashtable<String,data> datas = new Hashtable<String,data>();
	
	
	public void report(Element xml) {
		if (xml != null){
			xml.setAttribute("dataCount", String.valueOf(datas.size()));
		}
	}

	
	public void report(Map<String, Object> json) {
		if (json != null){
			json.put("dataCount", datas.size());
		}
	}

}
