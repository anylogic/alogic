package com.anysoft.xscript;

import java.io.InputStream;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;

/**
 * include语句
 * 
 * @author duanyy
 * 
 * @since 1.6.3.29
 */
public class Include extends Block {
	public Include(String xmlTag,Statement _parent) {
		super(xmlTag,_parent);
	}

	protected int onExecute(Properties p, ExecuteWatcher watcher) {
		List<Statement> _children = children;
		Properties variables = getLocalVariables(p);
		
		for (int i = 0 ; i < _children.size(); i ++){
			Statement statement = _children.get(i);
			statement.execute(variables,watcher);
		}
		return 0;
	}

	protected int compiling(Element _e, Properties _properties,CompileWatcher watcher){
		XmlElementProperties p = new XmlElementProperties(_e, _properties);
		
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
						Statement statement = createStatement(xmlTag, this);
						
						if (statement == null){
							if (watcher != null){
								watcher.message(this, "warn", "Can not find plugin:" + xmlTag + ",Ignored.");
							}
						}else{
							statement.compile(e, p,watcher);
							if (statement.isExecutable()){
								children.add(statement);
							}
						}
					}
				}
			}
		}
		return 0;
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
	
	protected int onCompiling(Element _e, Properties p, CompileWatcher watcher) {
		return 0;
	}
}