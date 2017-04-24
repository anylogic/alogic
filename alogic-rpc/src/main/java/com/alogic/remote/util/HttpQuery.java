package com.alogic.remote.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Set;
import com.anysoft.util.DefaultProperties;

/**
 * 工具类，封装了http调用路径和参数
 * 
 * @author yyduan
 * @since 1.6.8.12
 */
public class HttpQuery extends DefaultProperties{
	protected String path;
	protected String encoding = "utf-8";
	protected StringBuffer outputBuffer = new StringBuffer();
	
	public HttpQuery(String path,String encoding){
		this.path = path;
		this.encoding = encoding;
	}
	
	public HttpQuery(String path){
		this(path,"utf-8");
	}
	
	public HttpQuery param(String name,String value){
		this.SetValue(name, value);
		return this;
	}
	
	public HttpQuery params(String ... _params){
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
		outputBuffer.append(path);
		
		Set<String> keys = this.keys();
		if (!keys.isEmpty()){
			
			outputBuffer.append("?");			
			Iterator<String> __keys = keys().iterator();
			while (__keys.hasNext()){
				String __name = (String)__keys.next();
				String __value = _GetValue(__name);
				outputBuffer.append(__name);
				if (__value.length() > 0){
					outputBuffer.append("=");
					try {
						outputBuffer.append(URLEncoder.encode(__value,encoding));
					} catch (UnsupportedEncodingException e) {
					}
				}
				if (__keys.hasNext()){
					outputBuffer.append("&");
				}
			}
		}
		return outputBuffer.toString();
	}
}
