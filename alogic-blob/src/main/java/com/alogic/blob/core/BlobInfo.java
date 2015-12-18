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
 * 
 * @version 1.6.4.18 [duanyy 20151218] <br>
 * - 增加自动图标集 <br>
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
		
		public Default(String theId,String cType){
			id = theId;
			contentType = cType == null?"application/octet-stream":cType;
		}
		
		public Default(String theId,String cType,String theMd5,long theLength){
			id = theId;
			contentType = cType;
			md5 = theMd5;
			length = theLength;
		}
		
		@Override
		public void report(Element xml) {
			if (xml != null){
				xml.setAttribute("id", id);
				xml.setAttribute("contentType", contentType);
				xml.setAttribute("md5", md5);
				xml.setAttribute("length", String.valueOf(length));
			}
		}

		@Override
		public void report(Map<String, Object> json) {
			if (json != null){
				json.put("id", id);
				json.put("contentType", contentType);
				json.put("md5", md5);
				json.put("length", length);
			}
		}
		@Override
		public String id() {
			return id;
		}
		@Override
		public String md5() {
			return md5;
		}
		@Override
		public String contentType() {
			return contentType;
		}
		@Override
		public long length() {
			return length;
		}
		
		public void id(String theId){
			id = theId;
		}
		
		public void md5(String theMd5){
			md5 = theMd5;
		}
		
		public void contentType(String cType){
			contentType = cType;
		}
		
		public void length(long theLength){
			length = theLength;
		}
	}
}
