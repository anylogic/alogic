package com.logicbus.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;
import com.logicbus.backend.AbstractServant;
import com.logicbus.backend.AccessController;
import com.logicbus.backend.Context;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.backend.message.XMLMessage;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 获取混合模式下，访问控制组的列表
 * 
 * @author duanyy
 * 
 * @since 1.6.10.12
 */
public class AclGroupList extends AbstractServant {
	
	protected void onDestroy() {

	}

	
	protected void onCreate(ServiceDescription sd) {

	}

	
	protected int onXml(Context ctx) {
		XMLMessage msg = (XMLMessage)ctx.asMessage(XMLMessage.class);
		
		Document doc = msg.getDocument();
		Element root = msg.getRoot();
		
		Settings settings = Settings.get();
		AccessController ac = (AccessController) settings.get("accessController");
		if (ac != null){
			String [] groups = ac.getGroupList();
			
			for (String g:groups){
				Element acls = doc.createElement("acls");				
				XmlTools.setString(acls,"id",g);			
				root.appendChild(acls);
			}
		}
		
		return 0;
	}

	
	protected int onJson(Context ctx) {
		JsonMessage msg = (JsonMessage)ctx.asMessage(JsonMessage.class);
		
		Map<String,Object> root = msg.getRoot();
		
		Settings settings = Settings.get();
		AccessController ac = (AccessController) settings.get("accessController");
		if (ac != null){
			String [] groups = ac.getGroupList();
			
			List<String> array = new ArrayList<String>(groups.length);
			
			for (String g:groups){
				array.add(g);
			}
			
			root.put("acls", array);
		}
		
		return 0;
	}

}