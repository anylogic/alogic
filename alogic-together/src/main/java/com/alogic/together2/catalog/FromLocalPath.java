package com.alogic.together2.catalog;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.w3c.dom.Document;
import com.alogic.together2.TogetherServiceDescription;
import com.alogic.together2.service.TogetherServant;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;
import com.logicbus.models.catalog.CatalogNode;
import com.logicbus.models.catalog.Path;
import com.logicbus.models.servant.ServiceDescription;
import com.logicbus.models.servant.impl.FileSystemServantCatalog;
import com.logicbus.models.servant.impl.ServantCatalogNodeImpl;

/**
 * 从本地文件系统装入服务目录
 * 
 * @author yyduan
 * @since 1.6.11.3
 */
public class FromLocalPath extends FileSystemServantCatalog {
	/**
	 * 缺省的服务实现类
	 */
	private String servant = TogetherServant.class.getName();
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		servant = PropertiesConstants.getString(p,"servant",servant);
	}
	
	@Override
	protected CatalogNode createCatalogNode(Path _path){
		File rootFile = new File(rootPath + _path);
		if (!rootFile.isDirectory() || !rootFile.exists()){
			return null;
		}
		ServantCatalogNodeImpl root = new ServantCatalogNodeImpl(_path,null);
		
		File [] children = rootFile.listFiles();
		if (children != null){
			for (int i = 0 ; i < children.length ; i ++){
				File child = children[i];
				if (!child.isFile()){
					continue;
				}
				
				String name = child.getName();
				if (!name.endsWith(".xml"))
					continue;
				
				int end = name.lastIndexOf('.');
				int start = name.lastIndexOf('/');
				String id = name.substring(start + 1, end);
				Path childPath = _path.append(id);
				ServiceDescription sd = loadServiceDescription(id,childPath.getPath(),child,Settings.get());	
				if (sd != null){
					logger.info(String.format("Serivce %s is found.",sd.getPath()));
					root.addService(sd.getServiceID(), sd);
				}
			}
		}
		return root;
	}
	
	protected ServiceDescription loadServiceDescription(String id,String path,File file,Properties p){
		TogetherServiceDescription sd = null;
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			Document doc = XmlTools.loadFromInputStream(in);
			if (doc != null){
				sd = new TogetherServiceDescription(id,path);
				sd.configure(doc.getDocumentElement(), p);				
			}
		}catch (Exception ex){
			logger.error("Can not load service from " + file.getPath());
			logger.error(ExceptionUtils.getStackTrace(ex));
		}finally{
			IOTools.close(in);
		}
		return sd;
	}
}