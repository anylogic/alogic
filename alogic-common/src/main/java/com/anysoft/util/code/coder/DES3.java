package com.anysoft.util.code.coder;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anysoft.util.KeyGen;
import com.anysoft.util.code.Coder;


/**
 * 基于3DES算法的编码/解码器
 * 
 * @author duanyy
 *
 */
public class DES3 implements Coder {
	
	protected static final Logger LOG = LoggerFactory.getLogger(DES3.class);
	
	public String getAlgorithm() {
		return "DESede";
	}
	
	public String encode(String data,String key) {
		try {
			String _key = padding(key);
			String algorithm = getAlgorithm();

            SecureRandom random = new SecureRandom();  
            DESedeKeySpec desKey = new DESedeKeySpec(_key.getBytes());  
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm);  
            SecretKey securekey = keyFactory.generateSecret(desKey);
            
            Cipher c = Cipher.getInstance(algorithm);  
            c.init(Cipher.ENCRYPT_MODE, securekey, random);  	        
			
            byte [] result = c.doFinal(data.getBytes());
            return new String(Base64.encodeBase64(result));
		}catch (Exception ex){
			LOG.error(ExceptionUtils.getStackTrace(ex));
			return data;
		}
	}

	
	public String decode(String data,String key) {
		try {
			String _key = padding(key);
			String algorithm = getAlgorithm();

            SecureRandom random = new SecureRandom();  
            DESedeKeySpec desKey = new DESedeKeySpec(_key.getBytes());  
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm);  
            SecretKey securekey = keyFactory.generateSecret(desKey);
            
            Cipher c = Cipher.getInstance(algorithm);	
			c.init(Cipher.DECRYPT_MODE, securekey,random);
			
			byte [] result = Base64.decodeBase64(data.getBytes());
			return  new String(c.doFinal(result));
		}catch (Exception ex){
			LOG.error(ExceptionUtils.getStackTrace(ex));
			return data;
		}
	}	
	
	
	public String createKey(){
		return KeyGen.getKey(24);
	}	
	
	@Override
	public String createKey(String init){
		return init;
	}
	
	protected String padding(String key){
		if (key.length() > 24){
			return key.substring(0,24);
		}else{
			for (int i = key.length() ; i < 24 ; i ++){
				key += "0";
			}
			return key;
		}
	}
}
