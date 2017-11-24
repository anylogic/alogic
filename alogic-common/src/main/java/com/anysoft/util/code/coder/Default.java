package com.anysoft.util.code.coder;

import com.anysoft.util.KeyGen;
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

	@Override
	public String encode(String data,String key) {
		return data;
	}

	@Override
	public String decode(String data,String key) {
		return data;
	}

	@Override
	public String createKey(){
		return KeyGen.getKey(8);
	}	
	
	@Override
	public String createKey(String init){
		return init;
	}
}
