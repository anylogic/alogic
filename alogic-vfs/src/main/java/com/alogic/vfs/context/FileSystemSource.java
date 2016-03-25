package com.alogic.vfs.context;

import java.io.InputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.vfs.core.VirtualFileSystem;
import com.anysoft.context.Context;
import com.anysoft.context.Source;
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;

/**
 * vfs配置来源
 * @author duanyy
 *
 */
public class FileSystemSource extends Source<VirtualFileSystem>{
	/**
	 * 唯一实例
	 */
	private static FileSystemSource theInstance = null;
	
	/**
	 * 缺省配置文件
	 */
	private static final String DEFAULT = "java:///com/alogic/vfs/context/source.default.xml#" 
	 +  FileSystemSource.class.getName();
	
	protected FileSystemSource(){
		
	}	
	
	@Override
	public Context<VirtualFileSystem> newInstance(Element e, Properties p,
			String attrName) {
		Factory<Context<VirtualFileSystem>> f = new Factory<Context<VirtualFileSystem>>();
		return f.newInstance(e, p,attrName, InnerContext.class.getName());
	}
	
	@Override
	protected String getContextName(){
		return "context";
	}		

	/**
	 * 获取唯一的实例
	 * @return 唯一实例
	 */
	public static FileSystemSource get(){
		if (theInstance != null){
			return theInstance;
		}
		
		synchronized (FileSystemSource.class){
			if (theInstance == null){
				theInstance = (FileSystemSource)newInstance(Settings.get(), new FileSystemSource());
			}
		}
		return theInstance;		
	}
	
	/**
	 * 根据环境变量创建实例
	 * @param p 环境变量
	 * @param instance 预置的实例
	 * @return 创建并配置好的实例
	 */
	protected static FileSystemSource newInstance(Properties p,FileSystemSource instance) {
		String secondaryFile = p.GetValue("vfs.secondary", DEFAULT);
		String configFile = p.GetValue("vfs.master",secondaryFile);

		ResourceFactory rm = Settings.getResourceFactory();
		InputStream in = null;
		try {
			in = rm.load(configFile,secondaryFile, null);
			Document doc = XmlTools.loadFromInputStream(in);
			if (doc != null){
				if (instance == null){
					Factory<FileSystemSource> f = new Factory<FileSystemSource>();
					return f.newInstance(doc.getDocumentElement(), p,"module", FileSystemSource.class.getName());
				}else{
					instance.configure(doc.getDocumentElement(), p);
					return instance;
				}
			}
		} catch (Exception ex){
			logger.error("Error occurs when load xml file,source=" + configFile, ex);
		}finally {
			IOTools.closeStream(in);
		}
		return null;
	}
}
