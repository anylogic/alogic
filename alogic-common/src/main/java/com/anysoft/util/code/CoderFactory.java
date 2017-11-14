package com.anysoft.util.code;

import com.anysoft.util.BaseException;
import com.anysoft.util.Factory;
import com.anysoft.util.Pair;
import com.anysoft.util.Settings;
import com.anysoft.util.code.util.RSAUtil;

/**
 * SDACoder的工厂类
 * @author duanyy
 *
 */
public class CoderFactory extends Factory<Coder> {	
	protected CoderFactory(ClassLoader cl){
		super(cl);
	}
	
	protected static CoderFactory factory = null;
	
	synchronized public static Coder newCoder(String module){
		if (factory == null){
			Settings settings = Settings.get();
			ClassLoader cl = (ClassLoader)settings.get("classLoader");		
			if (cl == null){
				cl = Thread.currentThread().getContextClassLoader();
			}
			factory = new CoderFactory(cl);
		}
		
		return factory.newInstance(module);
	}
	
	
	public String getClassName(String _module) throws BaseException{
		if (_module.indexOf('.') < 0){
			return "com.anysoft.util.code.coder." + _module;
		}
		return _module;
	}
	
	public static void main(String [] args){		
		String value = "root";
		String key = "helloworldsdsddddddddddddddddddd";
		{
			Coder coder = CoderFactory.newCoder("DES");		
			
			String encodedValue = coder.encode(value,key);
			System.out.println("The encoded value is " + encodedValue);
			
			String decodedValue = coder.decode(encodedValue,key);
			System.out.println("The decoded value is " + decodedValue);						
		}
		{
			Coder coder = CoderFactory.newCoder("HmacSHA256");		
			//key = coder.createKey();
			String encodedValue = coder.encode(value,key);
			System.out.println("The key is " + key);
			System.out.println("The encoded value is " + encodedValue);
			
			String decodedValue = coder.decode(encodedValue,key);
			System.out.println("The decoded value is " + decodedValue);						
		}		
		{
			Coder coder = CoderFactory.newCoder("DES3");		
			String encodedValue = coder.encode(value,key);
			System.out.println("The encoded value is " + encodedValue);			
			String decodedValue = coder.decode(encodedValue,key);
			System.out.println("The decoded value is " + decodedValue);
			
			Coder coder2 = CoderFactory.newCoder("DES3");
			System.out.println("The decoded value is " + coder2.decode(encodedValue,key));
			
		}		
		{
			Coder coder = CoderFactory.newCoder("AES");		
			String encodedValue = coder.encode(value,key);
			System.out.println("The encoded value is " + encodedValue);
			String decodedValue = coder.decode(encodedValue,key);
			System.out.println("The decoded value is " + decodedValue);
		}	
		{
			Coder coder = CoderFactory.newCoder("MD5");		
			String encodedValue = coder.encode("shit1234_","Password");
			System.out.println("The encoded value is " + encodedValue);
			String decodedValue = coder.decode(encodedValue,key);
			System.out.println("The decoded value is " + decodedValue);
		}		
		{
			Pair<String,String> keyPair = RSAUtil.generateKeyPairInBase64();
			System.out.println("The public key is " + keyPair.key());
			System.out.println("The secret key is " + keyPair.value());
		}
	}
}
