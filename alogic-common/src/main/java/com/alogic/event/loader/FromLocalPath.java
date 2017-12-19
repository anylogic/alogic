package com.alogic.event.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.w3c.dom.Document;

import com.alogic.event.Process;
import com.alogic.load.Loader;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;

/**
 * 从本地文件目录装入
 * 
 * @author yyduan
 * @since 1.6.11.3
 */
public class FromLocalPath extends Loader.Abstract<Process>{
	/**
	 * 文件路径
	 */
	protected String home;

	/**
	 * 预加载的对象
	 */
	protected Map<String,Process> processes = new ConcurrentHashMap<String,Process>();
	
	@Override
	public Process load(String id, boolean cacheAllowed) {
		return processes.get(id);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);		
		
		home = PropertiesConstants.getString(p, "path", "");
		if (StringUtils.isNotEmpty(home)){
			LOG.info(String.format("Scan event processor in %s",home));
			File root = new File(home);
			if (root.exists() && root.isDirectory()){
				scanFileSystem(home,root);
			}
		}
	}
	
	/**
	 * 扫描文件系统
	 * @param path
	 * @param file
	 */
	protected void scanFileSystem(String path, File file) {
        File[] files = file.listFiles();
        for (File item:files){
        	if (item.getName().endsWith(".xml")){
        		LOG.info(String.format("Found event processor : %s",item.getName()));        		
        		Process p = loadFromFile(item);
    			if (p != null && !p.isNull()){
    				processes.put(p.getId(), p);
    			}
        	}
        }	
	}
	
	/**
	 * 从指定的文件装入Process 
	 * @param file Process配置文件
	 * @return Process实例
	 */
	protected Process loadFromFile(File file){
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			Document doc = XmlTools.loadFromInputStream(in);
			Process p = new Process.Default();
			p.configure(doc.getDocumentElement(), Settings.get());
			return p;
		}catch (Exception ex){
			LOG.error("Can not load file:" + file.getPath());
			LOG.error(ExceptionUtils.getStackTrace(ex));
			return null;
		}finally{
			IOTools.close(in);
		}		
	}
}