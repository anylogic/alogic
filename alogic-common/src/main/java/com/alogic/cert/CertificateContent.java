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
 * 
 * @version 1.6.11.55 [20180822 duanyy] <br>
 * - 增加获取证书序列号功能; <br>
 * 
 * @version 1.6.11.56 [20180823 duanyy] <br>
 * - 证书的序列号可定制; <br>
 */
public interface CertificateContent {
	
	/**
	 * 获取证书序列号
	 * @return 序列号
	 */
	public String getCertId();
	
	/**
	 * 设置内容
	 * @param cert 证书内容
	 * @param key 私钥内容
	 */
	public void setContent(String id,byte[] cert,byte[] key);
	
	/**
	 * 获取私钥内容
	 * 
	 * @param raw 是否已原始格式输出
	 * @return 私钥内容
	 */
	public byte[] getKey(boolean raw);
	
	/**
	 * 获取证书内容
	 * 
	 * @param raw 是否已原始格式输出
	 * @return 证书内容
	 */
	public byte[] getCert(boolean raw);
	
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
		protected String id;
		
		@Override
		public void setContent(String id,byte[] cert, byte[] key) {
			this.cert = cert;
			this.key = key;
			this.id = id;
		}

		@Override
		public String getCertId(){
			return this.id;
		}
		
		@Override
		public byte[] getKey(boolean raw) {
			return this.key;
		}

		@Override
		public byte[] getCert(boolean raw) {
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
