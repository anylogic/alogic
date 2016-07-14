package com.alogic.together.catalog;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import com.alogic.together.service.TogetherServant;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.models.catalog.CatalogNode;
import com.logicbus.models.catalog.Path;
import com.logicbus.models.servant.DefaultServiceDescription;
import com.logicbus.models.servant.impl.FileSystemServantCatalog;
import com.logicbus.models.servant.impl.ServantCatalogNodeImpl;

/**
 * 基于本地文件系统的together服务目录
 * 
 * @author duanyy
 *
 */
public class LocalFileSystemServantCatalog extends FileSystemServantCatalog {
	/**
	 * 缺省的服务实现类
	 */
	private String servant = TogetherServant.class.getName();
	private String visible = "public";
	private String log = "brief";
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		servant = PropertiesConstants.getString(p,"servant",servant);
		visible = PropertiesConstants.getString(p,"visible",visible);
		log = PropertiesConstants.getString(p,"log",log);
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
				DefaultServiceDescription sd = new DefaultServiceDescription(childPath.getId());
				sd.setModule(servant);
				sd.setName(id);
				sd.setPath(childPath.getPath());
				sd.setNote(id);
				sd.setVisible(StringUtils.isNotEmpty(visible)?visible:"public");
				sd.setLogType(StringUtils.isNotEmpty(log)?log:"brief");
				sd.getProperties().SetValue("script", child.toURI().toString());		

				root.addService(sd.getServiceID(), sd);
			}
		}
		return root;
	}
}
