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
 */
public class PemCertificateContent extends CertificateContent.Default{

	@Override
	public byte[] getKey() {
		PemObject pemObj = new PemObject("RSA PRIVATE KEY",super.getKey());
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

	@Override
	public byte[] getCert() {
		PemObject pemObj = new PemObject("CERTIFICATE",super.getCert());
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

	@Override
	public void saveKey(String filepath) {
		saveContent(filepath,getKey());
	}

	@Override
	public void saveCert(String filepath) {
		saveContent(filepath,getCert());
	}
	
}