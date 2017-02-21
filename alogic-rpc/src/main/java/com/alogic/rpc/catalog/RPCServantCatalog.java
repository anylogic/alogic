package com.alogic.rpc.catalog;

import org.w3c.dom.Element;

import com.alogic.rpc.service.RPCServant;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.models.catalog.Path;
import com.logicbus.models.servant.DefaultServiceDescription;
import com.logicbus.models.servant.ServiceDescription;
import com.logicbus.models.servant.impl.XMLDocumentServantCatalog;;

/**
 * 基于RPC的服务目录
 * 
 * @author duanyy
 * @since 1.6.7.15
 */
public class RPCServantCatalog extends XMLDocumentServantCatalog{
	protected String servant = RPCServant.class.getName();
	
	@Override
	public void configure(Properties p) {
		servant = PropertiesConstants.getString(p,"servant",servant);
		loadDocument(p);
	}	
	
	protected ServiceDescription toServiceDescription(Path _path,Element root){
		String id = root.getAttribute("id");
		if (id == null){
			return null;
		}
		
		Path childPath = _path.append(id);
		
		DefaultServiceDescription sd = new DefaultServiceDescription(childPath.getId());
		sd.fromXML(root);
		
		//保存module
		String module = sd.getModule();
		Properties p = sd.getProperties();
		p.SetValue("servant.impl", module);
		sd.setModule(servant);
		sd.setPath(childPath.getPath());		
		return sd;
	}

}
