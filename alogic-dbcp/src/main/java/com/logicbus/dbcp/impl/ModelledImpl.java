package com.logicbus.dbcp.impl;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.util.BaseException;
import com.anysoft.util.DefaultProperties;
import com.anysoft.util.Properties;

/**
 * 基于ConnectionModel的实现
 * 
 * @author duanyy
 * 
 * @since 1.2.9.1
 * 
 * @version 1.6.3.11 [20150402 duanyy] <br>
 * - {@link #createObject()}交给父类去实现 <br>
 */
public class ModelledImpl extends AbstractConnectionPool{

	public ModelledImpl(ConnectionModel _model){
		model = _model;
		
		Properties props = new DefaultProperties();		
		props.SetValue(getIdOfMaxQueueLength(),	String.valueOf(model.getMaxActive()));
		props.SetValue(getIdOfIdleQueueLength(),String.valueOf(model.getMaxIdle()));

		create(props);
	}
	
	
	public String getName() {
		return model.getName();
	}

	
	public void report(Element xml) {
		if (xml != null){
			model.report(xml);
			
			Document doc = xml.getOwnerDocument();
			
			// runtime
			{
				Element _runtime = doc.createElement("runtime");
				super.report(_runtime);
				xml.appendChild(_runtime);
			}
		}
	}

	
	public void report(Map<String, Object> json) {
		if (json != null){
			model.report(json);
			
			// runtime
			{
				Map<String,Object> _runtime = new HashMap<String,Object>();
				super.report(_runtime);
				json.put("runtime", _runtime);
			}
		}
	}
	
	
	protected int getMaxWait() {
		return model.getMaxWait();
	}
	
	protected ConnectionModel model;

	
	protected List<ReadOnlySource> getReadOnlySources() {
		return model.getReadOnlySources();
	}


	@Override
	protected Connection newConnection() throws BaseException{
		return model.newConnection();
	}

}