package com.alogic.blob.local;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.alogic.blob.core.BlobInfo;
import com.alogic.blob.core.BlobRegister;
import com.alogic.blob.core.BlobReader;
import com.alogic.blob.core.BlobManager;
import com.alogic.blob.core.BlobWriter;
import com.anysoft.util.BaseException;
import com.anysoft.util.Factory;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;

/**
 * 本地文件系统实现
 * 
 * @author duanyy
 * @since 1.6.3.28
 * 
 * @version 1.6.3.32 [duanyy 20150720] <br>
 * - 增加md5,content-type等信息 <br>
 */
public class LocalBlobManager implements BlobManager {
	/**
	 * a logger of log4j
	 */
	protected static final Logger logger = LogManager.getLogger(BlobManager.class);
	
	public void report(Element xml) {
		if (xml != null){
			xml.setAttribute("module", getClass().getName());
			xml.setAttribute("id", id);
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
			json.put("id", id);
			File file = new File(home);
			if (file.exists()){
				json.put("totalSpace", file.getTotalSpace());
				json.put("usableSpace", file.getUsableSpace());
				json.put("home", home);
			}
		}
	}

	public BlobWriter newFile(String contentType) {
		String id = newFileId();
		File file = new File(getRealPath(id));
		if (!file.exists()){
			try {
				File parentFile = file.getParentFile();
				if (!parentFile.exists()){
					parentFile.mkdirs();
				}
				file.createNewFile();
				return new LocalBlobWriter(id,file,contentType);
			} catch (IOException e) {
				logger.error("Can not create new file:" + file.getPath(),e);
			}
		}
		return null;
	}

	public BlobReader getFile(String id) {
		BlobInfo info = fileRegister != null ? fileRegister.find(id): null;
		
		if (info != null){
			File file = new File(getRealPath(id));
			if (file.exists() && file.canRead() && file.isFile()){
				return new LocalBlobReader(id,file,info);
			}
		}
		return null;
	}

	public boolean existFile(String id) {
		BlobInfo info = fileRegister != null ? fileRegister.find(id): null;
		if (info != null){
			File file = new File(getRealPath(id));
			return file.exists();
		}else{
			return false;
		}
	}

	public boolean deleteFile(String id) {
		fileRegister.delete(id);
		
		File file = new File(getRealPath(id));
		if (!file.exists() || !file.canWrite()){
			return false;
		}
		
		return file.delete();
	}

	public void configure(Element _e, Properties _properties)
			throws BaseException {
		XmlElementProperties p = new XmlElementProperties(_e,_properties);
		
		id = PropertiesConstants.getString(p,"id",id);
		
		home = PropertiesConstants.getString(p,"home",home);
		
		{
			//确保目录存在
			File homeFile = new File(home);
			if (!homeFile.exists()){
				homeFile.mkdirs();
			}
		}
		
		Factory<BlobRegister> factory = new Factory<BlobRegister>();
		fileRegister = factory.newInstance(_e, _properties, "register", LocalFileRegister.class.getName());
	}

	protected String home = "${ketty.home}/blob/data/${id}";
	
	protected String id = "default";
	
	protected BlobRegister fileRegister = null;
	
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

	public void commit(BlobWriter writer) {
		if (fileRegister != null){
			fileRegister.add(writer.getBlobInfo());
		}
	}

	public void cancel(BlobWriter writer) {
		String id = writer.getBlobInfo().id();
		File file = new File(getRealPath(id));
		if (file.exists() && file.canWrite()){
			file.delete();
		}
	}
}
