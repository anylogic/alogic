package com.anysoft.xscript;

import org.w3c.dom.Element;

import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlElementProperties;

/**
 * Using语句
 * 
 * @author duanyy
 * @since 1.6.3.22
 */
public class Using extends AbstractStatement {

	public Using(String xmlTag,Statement _parent) {
		super(xmlTag,_parent);
	}
	
	public void configure(Element _e, Properties _properties)
			throws BaseException {
		XmlElementProperties p = new XmlElementProperties(_e,_properties);
		
		String xmlTag = PropertiesConstants.getString(p,"xmlTag","",true);
		String module = PropertiesConstants.getString(p,"module","",true);
		
		if (isNotNull(xmlTag) && isNotNull(module)){
			Class<? extends Statement> clazz = getClass(module);
			if (clazz == null){
				logger.warn("Can not find class:" + module + ",ignored");
			}else{
				Statement _parent = parent();
				if (_parent == null){
					logger.warn("Parent statement is null,ignored");
				}else{
					_parent.registerModule(xmlTag, clazz);
				}
			}
		}else{
			logger.warn("The xmltag or module is not valid,ignored");
		}
	}
	
	public boolean isExecutable(){
		return false;
	}

	public int onExecute(Properties p,ExecuteWatcher watcher) {
		return 0;
	}
	
	protected boolean isNotNull(String value){
		return value != null && value.length() > 0;
	}
	
	@SuppressWarnings("unchecked")
	protected Class<? extends Statement> getClass(String module){
		ClassLoader cl = Settings.getClassLoader();
		try {
			return (Class<? extends Statement>)cl.loadClass(module);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

}
