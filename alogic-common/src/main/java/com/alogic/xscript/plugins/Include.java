package com.alogic.xscript.plugins;

import java.io.InputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alogic.xscript.Logiclet;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;

/**
 * Include
 * 
 * <p>
 * <code>Include</code> is used to import another script file.
 *  
 * @author duanyy
 *
 */

public class Include extends Segment{

	public Include(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Element element,Properties props) {
		XmlElementProperties p = new XmlElementProperties(element, props);
		configure(p);
		
		String src = PropertiesConstants.getString(p,"src","");
		String secondary = PropertiesConstants.getString(p,"secondary","");
		
		if (src != null && src.length() > 0){
			Document doc = loadDocument(src,secondary);
			if (doc != null){
				Element root = doc.getDocumentElement();
				if (root != null){
					NodeList _children = root.getChildNodes();
					for (int i = 0 ; i < _children.getLength() ; i ++){
						Node n = _children.item(i);
						
						if (n.getNodeType() != Node.ELEMENT_NODE){
							//只处理Element节点
							continue;
						}
						
						Element e = (Element)n;
						String xmlTag = e.getNodeName();		
						Logiclet logiclet = createLogiclet(xmlTag, this);
						
						if (logiclet != null){
							logiclet.configure(e, p);
							if (logiclet.isExecutable()){
								children.add(logiclet);
							}
						}
					}
				}
			}
		}
	}

	protected Document loadDocument(String url,String secondary){
		ResourceFactory resourceFactory = Settings.getResourceFactory();
		InputStream in = null;
		Document doc = null;
		try {
			in = resourceFactory.load(url, secondary);
			doc = XmlTools.loadFromInputStream(in);
		}catch (Exception ex){
			logger.error("The config file is not a valid file,url = " + url);
		}finally{
			IOTools.close(in);
		}
		return doc;
	}	
}