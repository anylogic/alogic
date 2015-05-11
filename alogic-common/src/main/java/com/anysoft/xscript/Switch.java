package com.anysoft.xscript;

import java.util.Hashtable;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anysoft.selector.Selector;
import com.anysoft.selector.impl.Constants;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.XmlElementProperties;

/**
 * switch语句
 * 
 * @author duanyy
 * @since 1.6.3.22
 */
public class Switch extends AbstractStatement{
	/**
	 * cases
	 */
	protected Hashtable<String,Statement> children = new Hashtable<String,Statement>();
	protected Selector selector = null;
	
	public Switch(String _tag, Statement _parent) {
		super(_tag, _parent);
	}

	@Override
	public void configure(Element _e, Properties _properties)
			throws BaseException {
		XmlElementProperties p = new XmlElementProperties(_e,_properties);
		
		selector = Selector.newInstanceWithDefault(_e, _properties, Constants.class.getName());
		if (selector == null){
			logger.error("Can not create selector.tag=" + getXmlTag());
		}
		
		NodeList _children = _e.getChildNodes();
		
		for (int i = 0 ; i < _children.getLength() ; i ++){
			Node n = _children.item(i);
			
			if (n.getNodeType() != Node.ELEMENT_NODE){
				//只处理Element节点
				continue;
			}
			
			Element e = (Element)n;
			String caseValue = e.getAttribute(STMT_CASE);
			if (isNotNull(caseValue)){
				String xmlTag = e.getNodeName();		
				Statement statement = createStatement(xmlTag, this);
				
				if (statement == null){
					logger.warn("Can not find plugin:" + xmlTag + ",Ignored.");
				}else{
					statement.configure(e, p);
					if (statement.isExecutable()){
						children.put(caseValue, statement);
					}
				}
			}else{
				logger.warn("The value is not valid,ignored");
			}
		}
	}

	protected int onExecute(Properties p, ExecuteWatcher watcher) throws BaseException {
		String caseValue = "default";
		if (selector != null){
			caseValue = selector.select(p);
		}
		
		Statement stmt = children.get(caseValue);
		if (stmt == null){
			if (!caseValue.equals(STMT_DEFAULT)){
				stmt = children.get(STMT_DEFAULT);
			}
		}
		
		if (stmt != null){
			return stmt.execute(p, watcher);
		}
		
		return -1;
	}

	protected boolean isNotNull(String value){
		return value != null && value.length() > 0;
	}
}
