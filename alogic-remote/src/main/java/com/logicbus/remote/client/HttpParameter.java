package com.logicbus.remote.client;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Enumeration;

import com.anysoft.util.DefaultProperties;


/**
 * Http Query参数信息
 * 
 * @author duanyy
 * @since 1.2.3
 * 
 * @version 1.2.4.2 [20140709 duanyy]
 * - encoding成为{@link com.logicbus.remote.client.Parameter Parameter}的方法.
 */
public class HttpParameter extends DefaultProperties implements Parameter{
	/**
	 * 编码
	 */
	protected String encoding = "utf-8";
	protected StringBuffer outputBuffer = new StringBuffer();
	
	public HttpParameter(String _encoding){
		encoding = _encoding;
	}
	
	public HttpParameter(){
	}	

	public HttpParameter(String _encoding,String ... _params){
		encoding = _encoding;
		params(_params);
	}
	
	/**
	 * 设置编码
	 * @param _encoding 编码
	 * @return
	 */
	public Parameter encoding(String _encoding){
		encoding = _encoding;
		return this;
	}
		
	/**
	 * 设置参数
	 * @param key 参数key
	 * @param value 参数值
	 * @return
	 */
	public Parameter param(String key,String value){
		_SetValue(key,value);
		return this;
	}
	
	public Parameter params(String ... _params){
		for (int i = 0 ; i < _params.length ; i = i + 2){
			if (i + 1 < _params.length){
				param(_params[i],_params[i + 1]);
			}else{
				param(_params[i],"");
			}
		}
		
		return this;
	}
	
	
	public String toString(){
		outputBuffer.setLength(0);				
		Enumeration<?> __keys = keys();
		while (__keys.hasMoreElements()){
			String __name = (String)__keys.nextElement();
			String __value = _GetValue(__name);
			outputBuffer.append(__name);
			if (__value.length() > 0){
				outputBuffer.append("=");
				try {
					outputBuffer.append(URLEncoder.encode(__value,encoding));
				} catch (UnsupportedEncodingException e) {
				}
			}
			if (__keys.hasMoreElements()){
				outputBuffer.append("&");
			}
		}
		
		return outputBuffer.toString();
	}
	
	public static void main(String [] args){
		HttpParameter query = new HttpParameter("utf-8","a","asdsad","b","http://localhost:8090/services");
		
		System.out.println(query.toString());
	
	}
}
