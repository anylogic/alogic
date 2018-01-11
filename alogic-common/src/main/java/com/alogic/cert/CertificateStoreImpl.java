package com.alogic.cert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;
import sun.security.x509.CertAndKeyGen;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertInfo;
import sun.security.x509.CertificateVersion;
import sun.security.x509.CertificateSerialNumber;
import sun.security.x509.AlgorithmId;
import sun.security.x509.CertificateAlgorithmId;
import sun.security.x509.CertificateSubjectName;
import sun.security.x509.CertificateX509Key;
import sun.security.x509.CertificateValidity;
import sun.security.x509.CertificateIssuerName;
import sun.security.x509.X509CertImpl;

/**
 * CertificateStore实现
 * 
 * @author yyduan
 *
 * @since 1.6.11.9
 */
@SuppressWarnings("restriction")
public class CertificateStoreImpl implements CertificateStore{
	/**
	 * a logger of slf4j
	 */
	protected static final Logger LOG = LoggerFactory.getLogger(CertificateStore.class);
	
	/**
	 * KeyStore的类型
	 */
	protected String jksType = "jks";
	
	/**
	 * keystore文件路径
	 */
	protected String jksPath;
	
	/**
	 * keystore文件密码
	 */
	protected String jksPwd = "alogic";
	
	/**
	 * 根证书的别名
	 */
	protected String jksRootAlias = "RootCA";
	
	/**
	 * 通用名,一般填写服务器域名，用于绑定
	 */
	protected String rootCN = "ALOGIC CA";
	
	/**
	 * 组织单位名称
	 */
	protected String rootOU = "ALOGIC.COM";
	
	/**
	 * 单位名称
	 */
	protected String rootO = "ALOGIC";
	
	/**
	 * 所在地,所在城市和区域
	 */
	protected String rootL = "GUANGZHOU";
	
	/**
	 * State，省/市/自治区/州
	 */
	protected String rootST = "GUANDDONG";
	
	/**
	 * Country,国家
	 */
	protected String rootC = "CN";
	
	/**
	 * 证书信息模板
	 */
	protected static String X500NamePattern = "CN=%s,OU=%s,O=%s,L=%s,ST=%s,C=%s";
	
	/**
	 * 随机发生器
	 */
	protected SecureRandom secureRandom = null;
	
	/**
	 * 根证书
	 */
	protected Certificate rootCert = null;
	
	/**
	 * 根证书私钥
	 */
	protected PrivateKey rootKey = null;
	
	/**
	 * KeyStore
	 */
	protected KeyStore keyStore = null;
	
	/**
	 * 证书有效期：缺省10年
	 */
	protected long rootTTL = 10;
	
	/**
	 * 证书算法
	 */
	protected String algorithm = "SHA512WithRSA";
	
	/**
	 * 获取根证书的X500Name
	 * @return X500Name
	 */
	protected String getRootX500Name(){
		return String.format(X500NamePattern, rootCN,rootOU,rootO,rootL,rootST,rootC);
	}
	
	/**
	 * 获取子证书的X500Name
	 * @param cn 子证书的通用名
	 * @return X500Name
	 */
	protected String getX500Name(String cn){
		return String.format(X500NamePattern, cn,rootOU,rootO,rootL,rootST,rootC);
	}
	
	protected String getX500Name(Properties p){
		String cn = PropertiesConstants.getString(p,"CN",rootCN);
		String ou = PropertiesConstants.getString(p,"OU",rootOU);
		String o = PropertiesConstants.getString(p,"O",rootO);
		String l = PropertiesConstants.getString(p,"L",rootL);
		String st = PropertiesConstants.getString(p,"ST",rootST);
		String c = PropertiesConstants.getString(p,"C",rootC);		
		return String.format(X500NamePattern, cn,ou,o,l,st,c);
	}
	
	@Override
	public void configure(Element e, Properties p) {
		Properties props = new XmlElementProperties(e,p);
		configure(props);
	}

	@Override
	public void configure(Properties p) {
		
		jksType = PropertiesConstants.getString(p, "jks.type", jksType);	
		jksPath = PropertiesConstants.getString(p, "jks.path", "");		
		jksPwd = PropertiesConstants.getString(p, "jks.pwd", jksPwd);	
		jksRootAlias = PropertiesConstants.getString(p, "jks.root", jksRootAlias);
		algorithm = PropertiesConstants.getString(p, "jks.algorithm", algorithm);
		rootTTL = PropertiesConstants.getLong(p,"ttl",rootTTL);
		rootCN = PropertiesConstants.getString(p,"CN",rootCN);
		rootOU = PropertiesConstants.getString(p,"OU",rootOU);
		rootO = PropertiesConstants.getString(p,"O",rootO);
		rootL = PropertiesConstants.getString(p,"L",rootL);
		rootST = PropertiesConstants.getString(p,"ST",rootST);
		rootC = PropertiesConstants.getString(p,"C",rootC);		
		
		try {
			secureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");
		} catch (NoSuchAlgorithmException e) {
			LOG.error(ExceptionUtils.getStackTrace(e));
		} catch (NoSuchProviderException e) {
			LOG.error(ExceptionUtils.getStackTrace(e));
		}
		init();
	}

	protected void init() {
		try {
			keyStore = KeyStore.getInstance(jksType);
			
			File file = new File(jksPath);
			if (file.exists() && file.isFile()){				
				InputStream in = new FileInputStream(file);
				try {
					keyStore.load(new FileInputStream(file), jksPwd.toCharArray());
				}finally{
					IOTools.close(in);
				}
				rootCert = keyStore.getCertificate(jksRootAlias);
				rootKey = (PrivateKey)keyStore.getKey(jksRootAlias, jksPwd.toCharArray());
			}else{
				keyStore.load(null, jksPwd.toCharArray());
			}
			
			if (rootCert == null || rootKey == null){
				//创建root
				CertAndKeyGen rootCertAndKeyGen = new CertAndKeyGen("RSA",algorithm, null);  
				rootCertAndKeyGen.setRandom(secureRandom); 
				rootCertAndKeyGen.generate(1024); 
				
				X509Certificate rootCertificate = rootCertAndKeyGen.getSelfCertificate(  
						new X500Name(getRootX500Name()), rootTTL * 365 * 24L * 60L * 60L); 
				
				X509Certificate[] X509Certificates = new X509Certificate[] { rootCertificate };
				
				keyStore.setKeyEntry(jksRootAlias, rootCertAndKeyGen.getPrivateKey(), jksPwd.toCharArray(), X509Certificates);
				
				OutputStream out = new FileOutputStream(file);				
				try {
					keyStore.store(out, jksPwd.toCharArray());
				}finally{
					IOTools.close(out);
				}
				
				rootCert = keyStore.getCertificate(jksRootAlias);
				rootKey = (PrivateKey)keyStore.getKey(jksRootAlias, jksPwd.toCharArray());
			}
		}catch (Exception ex){
			LOG.error(ExceptionUtils.getStackTrace(ex));
		}
	}

	@Override
	public CertificateContent getRoot(CertificateContent content) {
		try {
			content.setContent(rootCert.getEncoded(), rootKey.getEncoded());			
		}catch (Exception ex){
			LOG.error(ExceptionUtils.getStackTrace(ex));
		}
		return content;
	}
	
	@Override
	public CertificateContent newCertificate(CertificateContent content,String cn) {
		return newCertificate(content,getX500Name(cn),null);
	}
	
	@Override
	public CertificateContent newCertificate(CertificateContent content,Properties p) {
		return newCertificate(content,getX500Name(p),p);
	}
	
	/**
	 * 签发新的证书
	 * 
	 * @param content 证书内容
	 * @param subject x500name
	 * @param p 参数
	 */
	protected CertificateContent newCertificate(CertificateContent content,String subject,Properties p){
		try {
			long ttl = p == null ? rootTTL : PropertiesConstants.getLong(p, "ttl", rootTTL);
			
			CertAndKeyGen certAndKeyGen = new CertAndKeyGen("RSA", algorithm,null); 
			
			certAndKeyGen.setRandom(secureRandom);
			certAndKeyGen.generate(1024);  
			
			X509CertInfo info = new X509CertInfo();
			info.set(X509CertInfo.VERSION, 
					new CertificateVersion(CertificateVersion.V3));
			info.set(X509CertInfo.SERIAL_NUMBER, 
					new CertificateSerialNumber(new java.util.Random().nextInt() & 0x7fffffff));
			info.set(X509CertInfo.ALGORITHM_ID, 
					new CertificateAlgorithmId(AlgorithmId.get(algorithm)));
			info.set(X509CertInfo.SUBJECT, 
					new CertificateSubjectName(new X500Name(subject)));
			info.set(X509CertInfo.KEY, 
					new CertificateX509Key(certAndKeyGen.getPublicKey()));
			info.set(X509CertInfo.VALIDITY, 
					new CertificateValidity(new Date(),new Date(System.currentTimeMillis() + ttl * 365 * 24L * 60L * 60L * 1000)));			
			info.set(X509CertInfo.ISSUER, 
					new CertificateIssuerName(new X500Name(getRootX500Name())));
			
			X509CertImpl cert = new X509CertImpl(info);
			cert.sign(rootKey, algorithm);
			
			content.setContent(cert.getEncoded(), certAndKeyGen.getPrivateKey().getEncoded());
		}catch (Exception ex){
			LOG.error(ExceptionUtils.getStackTrace(ex));
		}
		return content;
	}
}
