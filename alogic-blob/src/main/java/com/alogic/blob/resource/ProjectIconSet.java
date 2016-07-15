package com.alogic.blob.resource;

import org.w3c.dom.Element;

import com.anysoft.util.Properties;

/**
 * 项目图标集 
 * 
 * @author duanyy
 *
 */
public class ProjectIconSet extends ResourceBlobManager {
	
	@Override
	public void configure(Element _e, Properties _properties){
		_e.setAttribute("home", "/com/alogic/blob/icon/project");
		_e.setAttribute("bootstrap", getClass().getName());
		
		super.configure(_e, _properties);
		
		resourceFound(getHome(),"1442218333965zoJgma");
		resourceFound(getHome(),"1442218333970n3h6gt");
		resourceFound(getHome(),"1442218333972WjJAl7");
		resourceFound(getHome(),"1442218333973C7O4ix");
		resourceFound(getHome(),"1442218333975pUJnMM");
		resourceFound(getHome(),"1442218333979rBK93N");
		resourceFound(getHome(),"1442218333981tIAUqW");
		resourceFound(getHome(),"1442218333983Tqvgiz");
		resourceFound(getHome(),"14422183339857AN3qP");
		resourceFound(getHome(),"1442218333987kFKifK");
		resourceFound(getHome(),"1442218333993XRSLAs");
		resourceFound(getHome(),"1442218333995wF8wFO");
		resourceFound(getHome(),"1442218333997XSEkIf");
		resourceFound(getHome(),"1442218333999c2ppM5");
		resourceFound(getHome(),"1442218334004fWAAGi");
		resourceFound(getHome(),"144221833400602m0rz");
	}
}
