package com.logicbus.kvalue.common;

import java.util.Map;

import org.w3c.dom.Element;

import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;

/**
 * 缺省的分区
 * 
 * @author duanyy
 *
 */
public class DefaultPartition implements Partition {
	protected String source = "default";
	protected String [] replicates = null;
	
	
	public void configure(Element _e, Properties _properties) {
		XmlElementProperties p = new XmlElementProperties(_e,_properties);
		
		source = PropertiesConstants.getString(p,"src",source,true);
		
		String _replicates = PropertiesConstants.getString(p, "replicates","",true);
		if (_replicates != null && _replicates.length() > 0)
			replicates = _replicates.split("[,]");
	}

	
	public void report(Element xml) {
		if (xml != null){
			xml.setAttribute("src", source);
			if (replicates != null && replicates.length > 0){
				StringBuffer buffer = new StringBuffer();
				for (int i = 0 ;i < replicates.length ; i ++){
					buffer.append(replicates[i]);
					if (i != replicates.length - 1){
						buffer.append(",");
					}
				}
				
				xml.setAttribute("replicates",buffer.toString());
			}
		}
	}

	
	public void report(Map<String, Object> json) {
		json.put("src", source);
		if (replicates != null && replicates.length > 0){
			StringBuffer buffer = new StringBuffer();
			for (int i = 0 ;i < replicates.length ; i ++){
				buffer.append(replicates[i]);
				if (i != replicates.length - 1){
					buffer.append(",");
				}
			}
			
			json.put("replicates", buffer.toString());
		}
	}

	
	public String getSource() {
		return source;
	}

	
	public String[] getReplicates() {
		return replicates;
	}
}
