package com.alogic.cert;

import java.math.BigInteger;

import com.anysoft.util.Configurable;
import com.anysoft.util.Properties;
import com.anysoft.util.XMLConfigurable;

/**
 * 证书中心，封装keystore
 * 
 * @author yyduan
 *
 * @since 1.6.11.9
 * 
 * @version 1.6.11.56 [20180823 duanyy] <br>
 * - 证书的序列号可定制; <br>
 */
public interface CertificateStore extends XMLConfigurable,Configurable{
	
	/**
	 * 新创建一个由根证书签发的证书
	 * 
	 * @param sn 证书序列号
	 * @param content 用于保存证书内容
	 * @param cn 通用名称
	 * @return 证书内容
	 */
	public CertificateContent newCertificate(BigInteger sn,CertificateContent content,String cn);

	/**
	 * 新创建一个由根证书签发的证书
	 * 
	 * @param sn 证书序列号
	 * @param content 用于保存证书内容
	 * @param p 参数集
	 * @return 证书内容
	 */
	public CertificateContent newCertificate(BigInteger sn,CertificateContent content,Properties p);
	
	/**
	 * 获取当前的根证书内容
	 * 
	 * @param content 用于保存证书内容
	 * @return 根证书内容
	 */
	public CertificateContent getRoot(CertificateContent content);
}
