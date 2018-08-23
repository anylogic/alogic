package com.alogic.cert;

import java.io.StringWriter;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;

import com.anysoft.util.IOTools;

/**
 * 基于PEM密钥格式的Content
 * @author yyduan
 *
 * @since 1.6.11.9
 * 
 * @version 1.6.11.56 [20180823 duanyy] <br>
 * - 证书的序列号可定制; <br>
 */
public class PemCertificateContent extends CertificateContent.Default{

	@Override
	public byte[] getKey(boolean raw) {
		if (raw){
			return super.getKey(true);
		}else{
			PemObject pemObj = new PemObject("RSA PRIVATE KEY",super.getKey(true));
			StringWriter writer = new StringWriter();
			PemWriter pemWriter = new PemWriter(writer);
			try {
				pemWriter.writeObject(pemObj);
			}catch (Exception ex){
				return null;
			}finally{
				IOTools.close(pemWriter);
			}
			return writer.getBuffer().toString().getBytes();
		}
	}

	@Override
	public byte[] getCert(boolean raw) {
		if (raw){
			return super.getCert(true);
		}else{
			PemObject pemObj = new PemObject("CERTIFICATE",super.getCert(true));
			StringWriter writer = new StringWriter();
			PemWriter pemWriter = new PemWriter(writer);
			try {
				pemWriter.writeObject(pemObj);
			}catch (Exception ex){
				return null;
			}finally{
				IOTools.close(pemWriter);
			}
			return writer.getBuffer().toString().getBytes();
		}
	}

	@Override
	public void saveKey(String filepath) {
		saveContent(filepath,getKey(false));
	}

	@Override
	public void saveCert(String filepath) {
		saveContent(filepath,getCert(false));
	}
	
}