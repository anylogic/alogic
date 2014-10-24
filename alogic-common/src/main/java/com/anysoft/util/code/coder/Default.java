package com.anysoft.util.code.coder;

import com.anysoft.util.code.Coder;


/**
 * 缺省的编码/解码器
 * 
 * <br>
 * 
 * 缺省状态下
 * 
 * @author duanyy
 *
 */
public class Default implements Coder {

	
	public String encode(String data,String key) {
		return data;
	}

	
	public String decode(String data,String key) {
		return data;
	}

	
	public String createKey(){
		return "";
	}	
}
