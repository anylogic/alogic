package com.anysoft.util.code.coder;
import com.anysoft.util.KeyGen;
import com.anysoft.util.code.Coder;
import com.anysoft.util.code.util.RSAUtil;

/**
 * 基于RSA采用公钥加密/解密
 * 
 * @author duanyy
 *
 */
public class PublicRSA implements Coder {	
	public String encode(String data,String key) {
		return RSAUtil.encryptWithPublicKey(data, key);
	}

	public String decode(String data,String key) {
		return RSAUtil.decryptWithPublicKey(data, key);
	}
	
	public boolean verify(String data,String key,String signData){
		return RSAUtil.verify(data, key, signData);
	}
	
	public String createKey(){
		return KeyGen.getKey(8);
	}
}