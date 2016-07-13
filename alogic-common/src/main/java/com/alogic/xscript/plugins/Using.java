package com.alogic.xscript.plugins;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlElementProperties;


/**
 * Using
 * @author duanyy
 *
 */
public class Using extends AbstractLogiclet{

	public Using(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p) {
		// nothing to do
	}
	
	@Override
	public void configure(Element e, Properties p) {
		super.configure(p);
		Properties props = new XmlElementProperties(e,p);
		configure(props);
		
		String xmlTag = PropertiesConstants.getString(props,"xmlTag","",true);
		String module = PropertiesConstants.getString(props,"module","",true);
		if (StringUtils.isNotEmpty(xmlTag) && StringUtils.isNotEmpty(module)){
			Class<? extends Logiclet> clazz = getClass(module);
			if (clazz != null){
				Logiclet _parent = parent();
				if (_parent != null){
					_parent.registerModule(xmlTag, clazz);				
				}else{
					logger.error("The parent is null,ignored");
				}
			}else{
				logger.error("Can not find class , module=" + module);
			}
		}	
	}	

	@Override
	protected void onExecute(Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx, ExecuteWatcher watcher) {
		// it's not an executable logiclet
	}

	public boolean isExecutable(){
		return false;
	}	
	
	@SuppressWarnings("unchecked")
	protected Class<? extends Logiclet> getClass(String module){
		ClassLoader cl = Settings.getClassLoader();
		try {
			return (Class<? extends Logiclet>)cl.loadClass(module);
		} catch (ClassNotFoundException e) {
			logger.error("Can not find class " + module,e);
			return null;
		}
	}

}
