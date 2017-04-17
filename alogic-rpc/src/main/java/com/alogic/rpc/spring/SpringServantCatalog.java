package com.alogic.rpc.spring;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.backend.ServantException;
import com.logicbus.models.catalog.Path;
import com.logicbus.models.servant.DefaultServiceDescription;
import com.logicbus.models.servant.ServiceDescription;
import com.logicbus.models.servant.impl.XMLDocumentServantCatalog;;

/**
 * 基于spring bean的Servant目录
 * 
 * @author xiongkw
 *
 */
public class SpringServantCatalog extends XMLDocumentServantCatalog {
	protected String servant = SpringServant.class.getName();

	@Override
	public void configure(Properties p) {
		servant = PropertiesConstants.getString(p, "servant", servant);
		loadDocument(p);
	}

	protected ServiceDescription toServiceDescription(Path _path, Element root) {
		String id = root.getAttribute("id");
		if (id == null) {
			return null;
		}

		Path childPath = _path.append(id);

		DefaultServiceDescription sd = new DefaultServiceDescription(childPath.getId());
		sd.fromXML(root);

		// 保存module
		String module = sd.getModule();
		if (StringUtils.isEmpty(module)) {
			throw new ServantException("core.servant_definition_error", "Servant definition error!");
		}
		Properties p = sd.getProperties();
		p.SetValue("identifier", module);
		sd.setModule(servant);
		sd.setPath(childPath.getPath());
		return sd;
	}

}
