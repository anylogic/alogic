package com.alogic.together2;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alogic.together2.service.TogetherServant;
import com.alogic.xscript.Script;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.logicbus.models.servant.DefaultServiceDescription;


/**
 * 自定义的ServiceDescription
 * 
 * @author yyduan
 * @since 1.6.11.3
 */
public class TogetherServiceDescription  extends DefaultServiceDescription implements XMLConfigurable {
	/**
	 * 脚本
	 */
	protected Script script = null;
	
	public TogetherServiceDescription(String id,String path) {
		super(id);
		setPath(path);
	}
	
	/**
	 * 获取脚本
	 * @return 脚本
	 */
	public Script getScript(){
		return script;
	}

	@Override
	public void configure(Element root, Properties p) {
		Properties props = new XmlElementProperties(root,p);
		setName(PropertiesConstants.getString(props,"name",getName(),true));		
		setModule(PropertiesConstants.getString(props, "module", TogetherServant.class.getName(),true));
		setNote(PropertiesConstants.getString(props,"note",getNote(),true));

		setVisible(PropertiesConstants.getString(props,"visible",getVisible(),false));
		setLogType(PropertiesConstants.getString(props,"log",getLogType().toString(),false));
		setGuard(PropertiesConstants.getBoolean(props,"guard",guard(),false));
		setAcGroup(PropertiesConstants.getString(props,"acGroupId",getAcGroup(),false));
		setPrivilege(PropertiesConstants.getString(props,"privilege",getPrivilege(),false));
		
		NodeList eProperties = XmlTools.getNodeListByPath(root, "properties/parameter");
		if (eProperties != null){
			for (int i = 0 ; i < eProperties.getLength() ; i ++){
				Node n = eProperties.item(i);
				if (n.getNodeType() != Node.ELEMENT_NODE){
					continue;
				}
				Element e = (Element)n;
				String _id = e.getAttribute("id");
				String _value = e.getAttribute("value");
				if (StringUtils.isNotEmpty(_id) && StringUtils.isNotEmpty(_value)){
					PropertySpec spec = new PropertySpec();
					spec.fromXML(e);
					propertySpecs.put(_id, spec);					
					getProperties().SetValue(_id,_value);
				}
			}
		}
		
		Element elem = XmlTools.getFirstElementByPath(root, "script");
		if (elem != null){
			script = Script.create(elem, props);
		}
	}

}
