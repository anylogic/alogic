package com.alogic.blob.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import com.alogic.blob.BlobReader;
import com.anysoft.util.JsonTools;
import com.anysoft.util.XmlTools;

/**
 * 自动图标集
 * 
 * @author yyduan
 *
 */
public class AutoIconSet extends ResourceBlobManager{
	
	/**
	 * 信息集
	 */
	protected List<ResourceBlobInfo> infos = new ArrayList<ResourceBlobInfo>();

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
	public BlobReader getFile(String id) {
		if (infos.isEmpty()){
			return null;
		}
		int index = (id.hashCode() & Integer.MAX_VALUE) % infos.size();
		return new ResourceBlobReader(infos.get(index),getBootstrap());
	}

	@Override
	public boolean existFile(String id) {
		return true;
	}

	@Override
	protected void resourceFound(String id, ResourceBlobInfo info) {
		infos.add(info);
	}

	@Override
	public String list(List<String> ids, String cookies, int limit) {
		for (ResourceBlobInfo info:infos){
			ids.add(info.getId());
		}
		return cookies;
	}
}
