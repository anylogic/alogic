package com.alogic.blob.resource;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.w3c.dom.Element;

import com.alogic.blob.BlobReader;
import com.anysoft.util.JsonTools;
import com.anysoft.util.XmlTools;

/**
 * 图标集
 * @author yyduan
 *
 */
public class IconSet extends ResourceBlobManager{
	
	/**
	 * 信息集
	 */
	protected Map<String,ResourceBlobInfo> infos = new ConcurrentHashMap<String,ResourceBlobInfo>();
	
	@Override
	public BlobReader getFile(String id) {
		ResourceBlobInfo info = infos.get(id);
		return info == null ? null : new ResourceBlobReader(info,getBootstrap());
	}

	@Override
	public void report(Element xml) {
		if (xml != null){
			super.report(xml);
			XmlTools.setInt(xml, "count",infos.size());
		}
	}

	@Override
	public void report(Map<String, Object> json) {
		if (json != null){
			super.report(json);
			JsonTools.setInt(json, "count", infos.size());
		}
	}	
	
	
	@Override
	public boolean existFile(String id) {
		return infos.containsKey(id);
	}

	@Override
	protected void resourceFound(String id, ResourceBlobInfo info) {
		infos.put(id, info);
	}

	@Override
	public String list(List<String> ids, String cookies, int limit) {
		Iterator<String> keys = infos.keySet().iterator();
		
		while (keys.hasNext()){
			ids.add(keys.next());
		}
		
		return cookies;
	}
}
