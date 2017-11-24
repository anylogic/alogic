package com.anysoft.util.code.coder;

import com.anysoft.util.KeyGen;
import com.anysoft.util.code.Coder;
import com.anysoft.util.code.util.RSAUtil;

/**
 * 基于RSA采用私钥加密/解密
 * 
 * @author duanyy
 *
 */
public class PrivateRSA implements Coder {	
	@Override
	public String encode(String data,String key) {
		return RSAUtil.encryptWithPrivateKey(data, key);
	}
	
	@Override
	public String decode(String data,String key) {
		return RSAUtil.decryptWithPrivateKey(data, key);
	}
	
	public String sign(String data,String key){
		return RSAUtil.sign(data, key);
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