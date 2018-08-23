package com.alogic.cert.bc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Date;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.alogic.cert.CertificateContent;
import com.alogic.cert.CertificateStore;
import com.anysoft.util.IOTools;
import com.anysoft.util.KeyGen;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;  
import org.bouncycastle.cert.X509v3CertificateBuilder;  
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;  
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;  

/**
 * 基于 Bouncy Castle的证书api实现
 * 
 * 
 * @author yyduan
 *
 * @since 1.6.11.10
 * 
 * @version 1.6.11.54 [20180822 duanyy] <br>
 * - 增加所生成证书的keyUsage和extKeyUsage的设置; <br>
 * 
 * @version 1.6.11.55 [20180822 duanyy] <br>
 * - 增加获取证书序列号功能; <br>
 * 
 * @version 1.6.11.56 [20180823 duanyy] <br>
 * - 证书的序列号可定制; <br>
 */
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
	protected X509Certificate rootCert = null;
	
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
			Security.addProvider(new BouncyCastleProvider());
			
			keyStore = KeyStore.getInstance(jksType);
			
			File file = new File(jksPath);
			if (file.exists() && file.isFile()){				
				InputStream in = new FileInputStream(file);
				try {
					keyStore.load(new FileInputStream(file), jksPwd.toCharArray());
				}finally{
					IOTools.close(in);
				}
				rootCert = (X509Certificate) keyStore.getCertificate(jksRootAlias);
				rootKey = (PrivateKey)keyStore.getKey(jksRootAlias, jksPwd.toCharArray());
			}else{
				keyStore.load(null, jksPwd.toCharArray());
			}
			
			if (rootCert == null || rootKey == null){
				//创建root
				KeyPairGenerator kpg=KeyPairGenerator.getInstance("RSA");  
		        KeyPair  kp = kpg.generateKeyPair();  
		        PublicKey pubk = kp.getPublic();  
		        PrivateKey prik = kp.getPrivate(); 
		        
		        long now = System.currentTimeMillis();
		        
				X509v3CertificateBuilder builder = new X509v3CertificateBuilder(
					new X500Name(getRootX500Name()),
					BigInteger.valueOf(now * 10000 + Integer.parseInt(KeyGen.uuid(5, 0, 9))),
					new Date(now), 
					new Date(now + rootTTL * 365 * 24L * 60L * 60L * 1000L), 
					new X500Name(getRootX500Name()), 
					SubjectPublicKeyInfo.getInstance(pubk.getEncoded())
				);
				
				addExtension(builder);
				
	            X509CertificateHolder holder = builder.build(
	            	new JcaContentSignerBuilder(algorithm).setSecureRandom(secureRandom).setProvider("BC").build(prik)
	            );  
	  
	            X509Certificate rootCertificate = new JcaX509CertificateConverter().getCertificate(holder);
				
				X509Certificate[] X509Certificates = new X509Certificate[] { rootCertificate };
				
				keyStore.setKeyEntry(jksRootAlias, prik, jksPwd.toCharArray(), X509Certificates);
				
				OutputStream out = new FileOutputStream(file);				
				try {
					keyStore.store(out, jksPwd.toCharArray());
				}finally{
					IOTools.close(out);
				}
				
				rootCert = (X509Certificate) keyStore.getCertificate(jksRootAlias);
				rootKey = (PrivateKey)keyStore.getKey(jksRootAlias, jksPwd.toCharArray());
			}
		}catch (Exception ex){
			LOG.error(ExceptionUtils.getStackTrace(ex));
		}
	}

	@Override
	public CertificateContent getRoot(CertificateContent content) {
		try {
			content.setContent(rootCert.getSerialNumber().toString(),rootCert.getEncoded(), rootKey.getEncoded());			
		}catch (Exception ex){
			LOG.error(ExceptionUtils.getStackTrace(ex));
		}
		return content;
	}
	
	@Override
	public CertificateContent newCertificate(BigInteger sn,CertificateContent content,String cn) {
		return newCertificate(sn,content,getX500Name(cn),null);
	}
	
	@Override
	public CertificateContent newCertificate(BigInteger sn,CertificateContent content,Properties p) {
		return newCertificate(sn,content,getX500Name(p),p);
	}
	
	/**
	 * 签发新的证书
	 * 
	 * @param content 证书内容
	 * @param subject x500name
	 * @param p 参数
	 */
	protected CertificateContent newCertificate(BigInteger sn,CertificateContent content,String subject,Properties p){
		try {
			long ttl = p == null ? rootTTL : PropertiesConstants.getLong(p, "ttl", rootTTL);
			long now = System.currentTimeMillis();
			
			//生成RSA公钥和私钥
			KeyPairGenerator kpg=KeyPairGenerator.getInstance("RSA");  
	        KeyPair kp = kpg.generateKeyPair();  
	        PublicKey pubk = kp.getPublic();  
	        PrivateKey prik = kp.getPrivate(); 
	        
	        //构造证书
			X509v3CertificateBuilder builder = new X509v3CertificateBuilder(
				new X500Name(getRootX500Name()),
				sn, 
				new Date(now), 
				new Date(now + ttl * 365 * 24L * 60L * 60L * 1000L), 
				new X500Name(subject), 
				SubjectPublicKeyInfo.getInstance(pubk.getEncoded())
			);
			
			addExtension(builder);
			
			//用根证书的Key进行签名
			X509CertificateHolder holder = builder.build(
            		new JcaContentSignerBuilder(algorithm).setSecureRandom(secureRandom).setProvider("BC").build(rootKey)
            		);  
			
            X509Certificate certifacate = new JcaX509CertificateConverter().getCertificate(holder);
            
			content.setContent(certifacate.getSerialNumber().toString(),certifacate.getEncoded(), prik.getEncoded());
		}catch (Exception ex){
			LOG.error(ExceptionUtils.getStackTrace(ex));
		}
		return content;
	}
	
	public void addExtension(X509v3CertificateBuilder builder) throws CertIOException{		
		
		int usage = KeyUsage.digitalSignature;  
        usage += KeyUsage.nonRepudiation;  
        usage += KeyUsage.keyEncipherment;  
        usage += KeyUsage.dataEncipherment;  
        usage += KeyUsage.keyAgreement;  
        usage += KeyUsage.keyCertSign;  
        usage += KeyUsage.cRLSign;  
        usage += KeyUsage.encipherOnly;  
        usage += KeyUsage.decipherOnly;  
  
        builder.addExtension(Extension.keyUsage, true, new KeyUsage(usage));  		
		
		ASN1EncodableVector extKeyUsage = new ASN1EncodableVector();
		extKeyUsage.add(KeyPurposeId.anyExtendedKeyUsage);
		extKeyUsage.add(KeyPurposeId.id_kp_clientAuth);
		extKeyUsage.add(KeyPurposeId.id_kp_serverAuth);
		extKeyUsage.add(KeyPurposeId.id_kp_scvpClient);
		extKeyUsage.add(KeyPurposeId.id_kp_scvpServer);
		extKeyUsage.add(KeyPurposeId.id_kp_codeSigning);
		extKeyUsage.add(KeyPurposeId.id_kp_emailProtection);
		extKeyUsage.add(KeyPurposeId.id_kp_timeStamping);
		
		builder.addExtension(Extension.extendedKeyUsage, false, new DERSequence(extKeyUsage));
	}
}


