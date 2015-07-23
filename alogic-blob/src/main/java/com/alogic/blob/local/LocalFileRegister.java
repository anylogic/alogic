package com.alogic.blob.local;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.alogic.blob.core.BlobInfo;
import com.alogic.blob.core.BlobRegister;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;

/**
 * 本地实现的Register
 * 
 * @author duanyy
 * @since 1.6.3.32
 * 
 * @version 1.6.3.33 [duanyy 20150723] <br>
 * - 变更home的参数名为home.data
 */
public class LocalFileRegister implements BlobRegister{
	/**
	 * a logger of log4j
	 */
	protected static final Logger logger = LogManager.getLogger(BlobRegister.class);
	
	protected static String readString(DataInputStream in) throws IOException{
		int length = in.readInt();
		byte [] bytes = new byte[length];
		in.read(bytes);
		
		return new String(bytes);
	}
	
	protected static long readLong(DataInputStream in) throws IOException{
		return in.readLong();
	}	
	
	protected static void writeString(DataOutputStream out,String value) throws IOException{
		byte [] bytes = value.getBytes();
		out.writeInt(bytes.length);
		out.write(bytes);
	}
	
	protected static void writeLong(DataOutputStream out,long value) throws IOException{
		out.writeLong(value);
	}		
	
	public BlobInfo find(String id) {
		File file = new File(getRealPath(id));
		if (file.exists() && file.canRead() && file.isFile()){
			try {
				DataInputStream in = new DataInputStream(new FileInputStream(file));
				
				String contentType = readString(in);
				String md5 = readString(in);
				
				return new BlobInfo.Default(id, contentType, md5, file.length());
			} catch (FileNotFoundException e) {
				logger.error("Can not find file:" + file.getPath(),e);
			} catch (IOException e) {
				logger.error("Can not read file:" + file.getPath(),e);
			}
		}
		return null;
	}

	public void add(BlobInfo info) {
		File file = new File(getRealPath(info.id()));
		if (!file.exists()){
			try {
				File parentFile = file.getParentFile();
				if (!parentFile.exists()){
					parentFile.mkdirs();
				}
				file.createNewFile();
				DataOutputStream out = new DataOutputStream(new FileOutputStream(file));
				
				writeString(out,info.contentType());
				writeString(out,info.md5());
				
			} catch (IOException e) {
				logger.error("Can not create new file:" + file.getPath(),e);
			}
		}
	}

	public void delete(String id) {
		File file = new File(getRealPath(id));
		if (file.exists() && file.canWrite()){
			file.delete();
		}	
	}	
	
	public void configure(Element _e, Properties _properties)
			throws BaseException {
		XmlElementProperties p = new XmlElementProperties(_e,_properties);
		
		home = PropertiesConstants.getString(p,"home.metadata",home);
		
		{
			//确保目录存在
			File homeFile = new File(home);
			if (!homeFile.exists()){
				homeFile.mkdirs();
			}
		}		
	}

	protected String home = "${ketty.home}/blob/metadata/${id}";
	
	public void report(Element xml) {
		if (xml != null){
			xml.setAttribute("module", getClass().getName());
			File file = new File(home);
			if (file.exists()){
				xml.setAttribute("totalSpace", String.valueOf(file.getTotalSpace()));
				xml.setAttribute("usableSpace", String.valueOf(file.getUsableSpace()));
				xml.setAttribute("home", home);
			}
		}
	}

	public void report(Map<String, Object> json) {
		if (json != null){
			json.put("module", getClass().getName());
			File file = new File(home);
			if (file.exists()){
				json.put("totalSpace", file.getTotalSpace());
				json.put("usableSpace", file.getUsableSpace());
				json.put("home", home);
			}
		}
	}

	
	/**
	 * 通过文件id映射实际路径
	 * @param id 文件id
	 * @return 实际路径
	 */
	protected String getRealPath(String id){
		long hash = id.hashCode() & Long.MAX_VALUE;
		
		return home + File.separator 
				+ (hash/100 % 10) + File.separator 
				+ (hash/10 % 10) + File.separator
				+ (hash % 10) + File.separator
				+ id + ".data";
	}
	
	/**
	 * 生成一个全局的文件ID
	 * @return 文件id
	 */
	protected String newFileId(){
		return System.currentTimeMillis() + randomString(6);
	}
	
	/**
	 * 字符表
	 */
	protected static final char[] Chars = {
	      'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
	      'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
	      'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',
	      'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
	      'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
	      'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
	      '8', '9'
	 };
	
	/**
	 * 按照指定宽度生成随机字符串
	 * @param _width 字符串的宽度
	 * @return 随机字符串
	 */
	static protected String randomString(int _width){
		int width = _width <= 0 ? 6 : _width;
		char [] ret = new char[width];
		Random ran = new Random();
		for (int i = 0 ; i < width ; i ++){
			int intValue = ran.nextInt(62) % 62;
			ret[i] = Chars[intValue];
		}
		
		return new String(ret);
	}
	
}
