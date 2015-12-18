package com.alogic.blob.local;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.w3c.dom.Element;

import com.alogic.blob.core.BlobInfo;
import com.alogic.blob.core.BlobRegister;
import com.alogic.blob.core.BlobReader;
import com.alogic.blob.core.BlobManager;
import com.alogic.blob.core.BlobWriter;
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
 * 
 * @version 1.6.3.33 [duanyy 20150723] <br>
 * - 变更home的参数名为home.data <br>
 * @version 1.6.4.7 [duanyy 20150916] <br>
 * - 从BlobManager.Abstract继承 <br>
 * 
 * @version 1.6.4.18 [duanyy 20151218] <br>
 * - 增加自动图标集 <br>
 * 
 * @version 1.6.4.19 [duanyy 20151218] <br>
 * - 按照SONAR建议修改代码 <br>
 */
public class LocalBlobManager extends BlobManager.Abstract {

	protected String home = "${ketty.home}/blob/data/${id}";
	
	protected BlobRegister fileRegister = null;
	
	
	/**
	 * 字符表
	 */
	private static final char[] CHARS = {
	      'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
	      'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
	      'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',
	      'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
	      'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
	      'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
	      '8', '9'
	 };	
	
	@Override
	public void report(Element xml) {
		if (xml != null){
			super.report(xml);
			
			File file = new File(home);
			if (file.exists()){
				xml.setAttribute("totalSpace", String.valueOf(file.getTotalSpace()));
				xml.setAttribute("usableSpace", String.valueOf(file.getUsableSpace()));
				xml.setAttribute("home", home);
			}
		}
	}

	@Override
	public void report(Map<String, Object> json) {
		if (json != null){
			super.report(json);
			
			File file = new File(home);
			if (file.exists()){
				json.put("totalSpace", file.getTotalSpace());
				json.put("usableSpace", file.getUsableSpace());
				json.put("home", home);
			}
		}
	}

	@Override
	public BlobWriter newFile(String contentType) {
		String fileId = newFileId();
		File file = new File(getRealPath(fileId));
		if (!file.exists()){
			try {
				File parentFile = file.getParentFile();
				if (!parentFile.exists()){
					parentFile.mkdirs();
				}
				file.createNewFile();
				return new LocalBlobWriter(fileId,file,contentType);
			} catch (IOException e) {
				logger.error("Can not create new file:" + file.getPath(),e);
			}
		}
		return null;
	}

	@Override
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

	@Override
	public boolean existFile(String id) {
		BlobInfo info = fileRegister != null ? fileRegister.find(id): null;
		if (info != null){
			File file = new File(getRealPath(id));
			return file.exists();
		}else{
			return false;
		}
	}

	@Override
	public boolean deleteFile(String id) {
		fileRegister.delete(id);
		
		File file = new File(getRealPath(id));
		if (!file.exists() || !file.canWrite()){
			return false;
		}
		
		return file.delete();
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);

		id = PropertiesConstants.getString(p, "id", id);
		home = PropertiesConstants.getString(p, "home.data", home);

		// 确保目录存在
		File homeFile = new File(home);
		if (!homeFile.exists()) {
			homeFile.mkdirs();
		}

	}

	@Override
	public void configure(Element e, Properties props) {
		XmlElementProperties p = new XmlElementProperties(e,props);
		configure(p);
		
		Factory<BlobRegister> factory = new Factory<BlobRegister>();// NOSONAR
		fileRegister = factory.newInstance(e, props, "register", LocalFileRegister.class.getName());
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
	 * 按照指定宽度生成随机字符串
	 * @param theWidth 字符串的宽度
	 * @return 随机字符串
	 */
	protected static String randomString(int theWidth){
		int width = theWidth <= 0 ? 6 : theWidth;
		char [] ret = new char[width];
		Random ran = new Random();
		for (int i = 0 ; i < width ; i ++){
			int intValue = ran.nextInt(62) % 62;
			ret[i] = CHARS[intValue];
		}
		
		return new String(ret);
	}

	@Override
	public void commit(BlobWriter writer) {
		if (fileRegister != null){
			fileRegister.add(writer.getBlobInfo());
		}
	}

	@Override
	public void cancel(BlobWriter writer) {
		String fileId = writer.getBlobInfo().id();
		File file = new File(getRealPath(fileId));
		if (file.exists() && file.canWrite()){
			file.delete();
		}
	}
	
	@Override
	public String list(List<String> ids, String cookies,int limit) {
		return fileRegister == null ? null : fileRegister.list(ids, cookies,limit);
	}
}
