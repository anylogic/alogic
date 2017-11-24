package com.anysoft.util.code.util;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anysoft.util.Pair;

/**
 * RSA加解密工具
 * 
 * @author duanyy
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 */
public class RSAUtil {  
	/**
	 * a logger of log4j
	 */
	private static final Logger LOG = LoggerFactory.getLogger(RSAUtil.class);
	
	/**
	 * RSA算法代码
	 */
    public static final String KEY_ALGORITHM = "RSA";  
  
    /**
     * 秘钥长度
     */
    public static final int KEY_LENGTH = 1024;  
   
    /**
     * 签名算法代码
     */
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";  
    
	private RSAUtil(){
		
	}    
    
    /**
     * 生成公钥和私钥对
     */
    public static Pair<byte[],byte[]> generateKeyPair() {  
        try {  
            SecureRandom secureRandom = new SecureRandom();  
            secureRandom.setSeed(String.valueOf(System.currentTimeMillis()).getBytes());  
            
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);  
            keyPairGenerator.initialize(KEY_LENGTH, secureRandom);   
            
            KeyPair keyPair = keyPairGenerator.genKeyPair();
            PublicKey publicKey = keyPair.getPublic();  
            PrivateKey privateKey = keyPair.getPrivate();  
  
            return new Pair.Default<byte[], byte[]>(publicKey.getEncoded(),privateKey.getEncoded()); // NOSONAR
        } catch (Exception ex) {  
        	LOG.error(ExceptionUtils.getStackTrace(ex));
            return null;
        }  
    }
    
    /**
     * 生成公钥和私钥对
     */
    public static Pair<String,String> generateKeyPairInBase64() {  
        try {  
            SecureRandom secureRandom = new SecureRandom();  
            secureRandom.setSeed(String.valueOf(System.currentTimeMillis()).getBytes());  
            
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);  
            keyPairGenerator.initialize(KEY_LENGTH, secureRandom);   
            
            KeyPair keyPair = keyPairGenerator.genKeyPair();
            PublicKey publicKey = keyPair.getPublic();  
            PrivateKey privateKey = keyPair.getPrivate();  
  
            String publicKeyInBase64 = Base64.encodeBase64URLSafeString(publicKey.getEncoded());
            String privateKeyInBase64 = Base64.encodeBase64URLSafeString(privateKey.getEncoded());
            
            return new Pair.Default<String, String>(publicKeyInBase64,privateKeyInBase64); // NOSONAR
        } catch (Exception ex) {  
        	LOG.error(ExceptionUtils.getStackTrace(ex));
            return null;
        }  
    }    
  
    /**
     * 通过公钥进行加密
     * @param data 待加密数据
     * @param publicKey 公钥
     * @return 加密后 的数据
     */
    public static String encryptWithPublicKey(String data,String publicKey){
    	byte [] toEncrypt = data.getBytes();
    	byte [] result = encryptWithPublicKey(toEncrypt,0,toEncrypt.length,Base64.decodeBase64(publicKey));
    	return result != null ? Base64.encodeBase64URLSafeString(result) : data;
    }
    
    /**
     * 通过公钥进行加密
     * @param data 待加密的数据
     * @param offset 数据的起始位置
     * @param length 数据的长度
     * @param publicKeyBytes 公钥
     * @return 加密后的数据
     */
    public static byte[] encryptWithPublicKey(byte[] data, int offset, int length, byte[] publicKeyBytes) {  
        byte[] encryptedData = null;  
  
        try {  
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
            PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));  
  
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());  
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);  
            encryptedData = cipher.doFinal(data, offset, length);  
        } catch (Exception ex) {  
        	LOG.error(ExceptionUtils.getStackTrace(ex));
        }  
  
        return encryptedData;  
    }  
    
    /**
     * 通过私钥进行加密
     * @param data 待加密数据
     * @param privateKey 私钥
     * @return 加密后 的数据
     */
    public static String encryptWithPrivateKey(String data,String privateKey){
    	byte [] toEncrypt = data.getBytes();
    	byte [] result = encryptWithPrivateKey(toEncrypt,0,toEncrypt.length,Base64.decodeBase64(privateKey));
    	return result != null ? Base64.encodeBase64URLSafeString(result) : data;
    }    
  
    /**
     * 通过私钥进行加密
     * @param data 待加密的数据
     * @param offset 数据的起始位置
     * @param length 数据的长度
     * @param privateKeyBytes 
     * @return 加密后的数据
     */
    public static byte[] encryptWithPrivateKey(byte[] data, int offset, int length, byte[] privateKeyBytes) {  
        byte[] encryptedData = null;  
  
        try {  
        	KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
            PrivateKey privateKey = keyFactory.generatePrivate( new PKCS8EncodedKeySpec(privateKeyBytes));  
  
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());  
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);  
            encryptedData = cipher.doFinal(data, offset, length);  
        } catch (Exception ex) { 
        	LOG.error(ExceptionUtils.getStackTrace(ex));
        }  
  
        return encryptedData;  
    }  
  
    /**
     * 通过公钥进行解密
     * @param data 待解密数据
     * @param publicKey 公钥
     * @return 解密后 的数据
     */
    public static String decryptWithPublicKey(String data,String publicKey){
    	byte [] toDecrypt = Base64.decodeBase64(data.getBytes());
    	byte [] result = decryptWithPublicKey(toDecrypt,0,toDecrypt.length,Base64.decodeBase64(publicKey));
    	return result != null ? StringUtils.newStringUtf8(result) : data;
    }    
    
    /**
     * 通过公钥解密
     * @param data 待解密的数据
     * @param offset 数据的起始位置
     * @param length 数据的长度
     * @param publicKeyBytes 公钥
     * @return 解密后的数据
     */
    public static byte[] decryptWithPublicKey(byte[] data, int offset, int length, byte[] publicKeyBytes) {  
        byte[] encryptedData = null;  
  
        try {  
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
            PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));  
  
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());  
            cipher.init(Cipher.DECRYPT_MODE, publicKey);  
            encryptedData = cipher.doFinal(data, offset, length);  
        } catch (Exception ex) {  
        	LOG.error(ExceptionUtils.getStackTrace(ex));
        }  
  
        return encryptedData;  
    }  
  
    /**
     * 通过私钥进行解密
     * @param data 待解密数据
     * @param privateKey 私钥
     * @return 解密后 的数据
     */
    public static String decryptWithPrivateKey(String data,String privateKey){
    	byte [] toDecrypt = Base64.decodeBase64(data.getBytes());
    	byte [] result = decryptWithPrivateKey(toDecrypt,0,toDecrypt.length,Base64.decodeBase64(privateKey));
    	return result != null ? StringUtils.newStringUtf8(result) : data;
    }       
    
    /**
     * 通过私钥解密
     * @param data 待解密的数据
     * @param offset 数据的起始位置
     * @param length 数据的长度
     * @param privateKeyBytes 私钥
     * @return 解密后的数据
     */
    public static byte[] decryptWithPrivateKey(byte[] data, int offset, int length, byte[] privateKeyBytes) {  
        byte[] encryptedData = null;  
  
        try {    
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
            PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));  
  
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());  
            cipher.init(Cipher.DECRYPT_MODE, privateKey);  
            encryptedData = cipher.doFinal(data, offset, length);  
        } catch (Exception ex) {
        	LOG.error(ExceptionUtils.getStackTrace(ex));
        }  
  
        return encryptedData;  
    }  
 
    /**
     * 通过私钥签名
     * @param data 待签名的数据
     * @param privateKey 私钥
     * @return 签名数据
     */
    public static String sign(String data,String privateKey){
    	byte [] toSign = data.getBytes();
    	byte [] result = sign(toSign,0,toSign.length,Base64.decodeBase64(privateKey));
    	return result != null ? Base64.encodeBase64URLSafeString(result) : null;
    }
    
    /**
     * 通过私钥签名
     * @param data 待签名的数据
     * @param offset 数据的起始位置
     * @param length 数据的长度
     * @param privateKeyBytes 私钥
     * @return 签名数据
     */
    public static byte[] sign(byte[] data, int offset, int length, byte[] privateKeyBytes) {  
        byte[] signedData = null;  
        try {  
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
            PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));  
  
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);  
            signature.initSign(privateKey);  
            signature.update(data, offset, length);
            signedData = signature.sign();  
        } catch (Exception ex) {
        	LOG.error(ExceptionUtils.getStackTrace(ex));
        }  
  
        return signedData;  
    }  
   
    /**
     * 通过公钥验证签名
     * @param data 原始数据
     * @param publicKey 公钥
     * @param signData 签名数据
     * @return 是否通过
     */
    public static boolean verify(String data,String publicKey,String signData){
    	byte [] toVerify = data.getBytes();
    	return verify(toVerify,0,toVerify.length,Base64.decodeBase64(publicKey),Base64.decodeBase64(signData));
    }
    
    /**
     * 通过公钥验证签名
     * @param data 原始数据
     * @param offset 数据的起始位置
     * @param length 数据的长度
     * @param publicKeyBytes 公钥
     * @param dataSignature 签名数据
     * @return 是否通过
     */
    public static boolean verify(byte[] data, int offset, int length, byte[] publicKeyBytes, byte[] dataSignature) {  
        boolean result = false;  
        try {  
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
            PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));  
  
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);  
            signature.initVerify(publicKey);  
            signature.update(data, offset, length);  
            result = signature.verify(dataSignature);  
        } catch (Exception ex) {
        	LOG.error(ExceptionUtils.getStackTrace(ex));
        }  
  
        return result;  
    }  
    
    public static void main(String[] args){
    	Pair<String,String> keys = generateKeyPairInBase64();
    	String publicKey = keys.key();
    	String privateKey = keys.value();
    	
    	System.out.println("the public key is:");
    	System.out.println(publicKey);
    	
    	System.out.println("the private key is:");
    	System.out.println(privateKey);
    	
    	String data = "This is a text";
    	
    	String encryptData = encryptWithPublicKey(data,publicKey);
    	
    	System.out.println("The encrypt data with public key is");
    	System.out.println(encryptData);
    	
    	String decryptData = decryptWithPrivateKey(encryptData,privateKey);
    	System.out.println(decryptData);
    	
    	encryptData = encryptWithPrivateKey(data,privateKey);
    	System.out.println("The encrypt data with private key is");
    	System.out.println(encryptData);    	
    	
    	decryptData = decryptWithPublicKey(encryptData,publicKey);
    	System.out.println(decryptData);  	

    	String sign = sign(data,privateKey);
    	System.out.println("the signature data is ");
    	System.out.println(sign);
    	
    	boolean verify = verify(data,publicKey,sign);
    	System.out.println(verify);
    }
}  
