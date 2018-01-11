package com.alogic.cert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.anysoft.util.BaseException;
import com.anysoft.util.IOTools;

/**
 * 证书内容
 * 
 * @author yyduan
 *
 * @since 1.6.11.9
 */
public interface CertificateContent {
	/**
	 * 设置内容
	 * @param cert 证书内容
	 * @param key 私钥内容
	 */
	public void setContent(byte[] cert,byte[] key);
	
	/**
	 * 获取私钥内容
	 * @return 私钥内容
	 */
	public byte[] getKey();
	
	/**
	 * 获取证书内容
	 * @return 证书内容
	 */
	public byte[] getCert();
	
	/**
	 * 保存私钥内容到文件
	 * 
	 * @param filepath
	 */
	public void saveKey(String filepath);
	
	/**
	 * 保存证书内容到文件
	 * 
	 * @param filepath
	 */
	public void saveCert(String filepath);	
	
	/**
	 * 缺省实现
	 * @author yyduan
	 *
	 */
	public class Default implements CertificateContent{
		protected byte[] cert;
		protected byte[] key;
		
		@Override
		public void setContent(byte[] cert, byte[] key) {
			this.cert = cert;
			this.key = key;
		}

		@Override
		public byte[] getKey() {
			return this.key;
		}

		@Override
		public byte[] getCert() {
			return this.cert;
		}

		@Override
		public void saveKey(String filepath) {
			saveContent(filepath,this.key);
		}

		@Override
		public void saveCert(String filepath) {
			saveContent(filepath,this.cert);
		}

		protected void saveContent(String filepath,byte[] content){
			File file = new File(filepath);
			if (file.exists()){
				throw new BaseException("core.e1016",String.format("File %s exist.", filepath));
			}
			
			if (file.canWrite()){
				throw new BaseException("core.e1017",String.format("File %s can not be written.", filepath));
			}
			
			OutputStream out = null;
			
			try {
				out = new FileOutputStream(file);
				out.write(content);
				out.flush();
			} catch (Exception e) {
				throw new BaseException("core.e1004",String.format("Failed to write file %s", filepath));
			}finally{
				IOTools.close(out);
			}			
		}
		
	}
}
