package com.alogic.blob.core;

import java.util.Map;

import org.w3c.dom.Element;

import com.anysoft.util.Reportable;

/**
 * BlobInfo
 * @author duanyy
 * @since 1.6.3.28
 * 
 * @version 1.6.3.32 [duanyy 20150720] <br>
 * - 增加md5,content-type等信息 <br>
 */
public interface BlobInfo extends Reportable{
	
	/**
	 * 获取文件id
	 * @return 文件id
	 */
	public String id();
	
	/**
	 * 获取文件的MD5码
	 * @return MD5码
	 */
	public String md5();
	
	/**
	 * 获取文件的ContentType
	 * @return ContentType
	 */
	public String contentType();
	
	/**
	 * 获取文件长度
	 * @return 文件长度
	 */
	public long length();	
	
	/**
	 * 缺省实现
	 * @author duanyy
	 * @version 1.6.4.2 <br>
	 * - BlobInfo增加length <br>
	 */
	public static class Default implements BlobInfo{
		protected String id;
		protected String contentType = "";
		protected String md5 = "";
		protected long length = 0;
		
		public Default(String _id,String _contentType){
			id = _id;
			contentType = _contentType == null?"application/octet-stream":_contentType;
		}
		
		public Default(String _id,String _contentType,String _md5,long _length){
			id = _id;
			contentType = _contentType;
			md5 = _md5;
			length = _length;
		}
		
		public void report(Element xml) {
			if (xml != null){
				xml.setAttribute("id", id);
				xml.setAttribute("contentType", contentType);
				xml.setAttribute("md5", md5);
				xml.setAttribute("length", String.valueOf(length));
			}
		}

		public void report(Map<String, Object> json) {
			if (json != null){
				json.put("id", id);
				json.put("contentType", contentType);
				json.put("md5", md5);
				json.put("length", length);
			}
		}

		public String id() {
			return id;
		}

		public String md5() {
			return md5;
		}

		public String contentType() {
			return contentType;
		}

		public long length() {
			return length;
		}
		
		public void id(String _id){
			id = _id;
		}
		
		public void md5(String _md5){
			md5 = _md5;
		}
		
		public void contentType(String _contentType){
			contentType = _contentType;
		}
		
		public void length(long _length){
			length = _length;
		}
	}
}
