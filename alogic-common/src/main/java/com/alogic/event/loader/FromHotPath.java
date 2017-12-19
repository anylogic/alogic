package com.alogic.event.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.alogic.event.Process;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Watcher;

/**
 * 从热部署目录加载
 * 
 * @author yyduan
 *
 * @since 1.6.11.3
 */
public class FromHotPath extends FromLocalPath implements Runnable{
	
	/**
	 * 执行线程池
	 */
	protected ScheduledThreadPoolExecutor threadPool = new ScheduledThreadPoolExecutor(1);
	
	/**
	 * 扫描间隔
	 */
	protected long interval = 10;
	
	/**
	 * 开始扫描的延迟
	 */
	protected long delay = 10;
	
	/**
	 * 保存文件摘要
	 */
	protected Map<String,String> digests = new HashMap<String,String>();
	
	/**
	 * 监听器
	 */
	private List<Watcher<Process>> watchers = new ArrayList<Watcher<Process>>();
	
	@Override
	public void configure(Properties p){
		super.configure(p);		
		
		interval = PropertiesConstants.getLong(p,"interval",interval);
		delay = PropertiesConstants.getLong(p,"delay",delay);
		
		//开启扫描线程
		threadPool.scheduleAtFixedRate(this, delay, interval, TimeUnit.SECONDS);
	}

	@Override
	protected void scanFileSystem(String path, File file) {
        File[] files = file.listFiles();
        for (File item:files){
        	String name = item.getName();
        	if (item.isFile() && name.endsWith(".xml")){
        		String digest = digests.get(name);
        		
        		if (StringUtils.isEmpty(digest)){
        			//第一次发现该文件            			
            		Process p = loadFromFile(item);
        			if (p != null && !p.isNull()){
        				String md5 = getFileDigest(item);
        				if (StringUtils.isNotEmpty(md5)){
        					LOG.info(String.format("Processor %s is found.",name));        	
        					digests.put(name, md5);
        					processes.put(p.getId(), p);
        				}       				
        			}        			
        		}else{
        			String md5 = getFileDigest(item);
        			if (!md5.equals(digest)){
        				//文件有变更
                		Process p = loadFromFile(item);
            			if (p != null && !p.isNull()){
            				LOG.info(String.format("Processor %s has been changed.",name));      	
        					digests.put(name, md5);
        					processes.put(p.getId(), p); 
        					
        					for (Watcher<Process> w:watchers){
        						w.changed(p.getId(), p);
        					}        					
            			}
        			}
        		}
        	}
        }	
	}
	
	/**
	 * 获取指定文件的摘要
	 * @param f 文件
	 * @return 摘要字符串
	 */
	protected String getFileDigest(File f){
		InputStream in = null;
		try {
			in = new FileInputStream(f);
			return DigestUtils.md5Hex(in);
		}catch (Exception ex){
			LOG.error("Can not load file:" + f.getPath());
			LOG.error(ExceptionUtils.getStackTrace(ex));
			return null;
		}finally{
			IOTools.close(in);
		}
	}
	
	@Override
	public void addWatcher(Watcher<Process> watcher) {
		super.addWatcher(watcher);
		watchers.add(watcher);
	}
	
	@Override
	public void removeWatcher(Watcher<Process> watcher) {
		super.removeWatcher(watcher);
		watchers.remove(watcher);
	}
	
	@Override
	public void run() {
		if (StringUtils.isNotEmpty(home)){
			File root = new File(home);
			if (root.exists() && root.isDirectory()){
				scanFileSystem(home,root);
			}
		}
	}
}
