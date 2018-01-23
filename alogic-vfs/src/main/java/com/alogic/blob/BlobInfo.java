package com.alogic.blob;

import java.util.Map;

import org.w3c.dom.Element;

import com.anysoft.util.JsonTools;
import com.anysoft.util.Reportable;
import com.anysoft.util.XmlTools;

/**
 * Blob信息
 * @author yyduan
 *
 */
public interface BlobInfo extends Reportable{
	
	/**
	 * 获取id
	 * @return id
	 */
	public String getId();
	
	/**
	 * 获取ContentType
	 * @return ContentType
	 */
	public String getContentType();
	
	/**
	 * 虚基类
	 * @author yyduan
	 *
	 */
	public static class Abstract implements BlobInfo{
		protected String id;
		protected String contentType;
		public Abstract(final String id,final String contentType){
			this.id = id;
			this.contentType = contentType;
		}
		
		@Override
		public void report(Element xml) {
			if (xml != null){
				XmlTools.setString(xml, "id", getId());
			}
		}

		@Override
		public void report(Map<String, Object> json) {
			if (json != null){
				JsonTools.setString(json,"id",getId());
			}
		}

		@Override
		public String getId() {
			return id;
		}

		@Override
		public String getContentType() {
			return contentType;
		}
		
	}
}
