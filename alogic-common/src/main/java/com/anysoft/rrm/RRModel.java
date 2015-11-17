package com.anysoft.rrm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.util.BaseException;
import com.anysoft.util.Configurable;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;

/**
 * Round Robin Model
 * @author duanyy
 *
 */
public class RRModel<data extends RRData> implements XMLConfigurable,Configurable,Reportable{

	/**
	 * id
	 */
	private String id;
	
	/**
	 * 模型下的多个RRA
	 */
	private Map<String,RRArchive<data>> rras = new HashMap<String,RRArchive<data>>();
	
	/**
	 * 获取id
	 * @return id
	 */
	public String getId(){
		return id;
	}
	
	public RRModel(String _id){
		id = _id;
	}
	
	/**
	 * 获取指定id的rra
	 * @param id id
	 * @return rra
	 */
	public RRArchive<data> getRRA(String id){
		return rras.get(id);
	}
	
	/**
	 * 更新数据
	 * @param timestamp
	 * @param fragment
	 */
	public void update(long timestamp,data fragment){
		Iterator<RRArchive<data>> iterator = rras.values().iterator();
		
		while (iterator.hasNext()){
			RRArchive<data> rra = iterator.next();
			rra.update(timestamp, fragment);
		}
	}
	
	@Override
	public void report(Element xml) {
		if (xml != null){
			xml.setAttribute("id", id);
			
			String cycle = xml.getAttribute("cycle");
			Document doc = xml.getOwnerDocument();
			
			if (cycle != null && cycle.length() > 0){
				RRArchive<data> found = rras.get(cycle);
				if (found != null){
					Element _rra = doc.createElement("rra");
					_rra.setAttribute("hist", "true");
					found.report(_rra);
					xml.appendChild(_rra);
				}
			}else{	
				Iterator<RRArchive<data>> iterator = rras.values().iterator();
				while (iterator.hasNext()){
					RRArchive<data> rra = iterator.next();
					Element _rra = doc.createElement("rra");
					rra.report(_rra);
					xml.appendChild(_rra);
				}
			}
		}
	}

	@Override
	public void report(Map<String, Object> json) {
		if (json != null){
			json.put("id", id);
			
			String cycle = JsonTools.getString(json, "cycle", "");
			if (cycle != null && cycle.length() > 0){
				RRArchive<data> found = rras.get(cycle);
				if (found != null){
					Map<String,Object> _rra = new HashMap<String,Object>();
					_rra.put("hist", true);
					found.report(_rra);
					json.put("rra", _rra);
				}
			}else{
				List<Object> _rras = new ArrayList<Object>();
				
				Iterator<RRArchive<data>> iterator = rras.values().iterator();
				while (iterator.hasNext()){
					RRArchive<data> rra = iterator.next();
					Map<String,Object> _rra = new HashMap<String,Object>();
					rra.report(_rra);
					_rras.add(_rra);
				}
				
				json.put("rra", _rras);
			}
		}
	}

	@Override
	public void configure(Element _e, Properties _properties)
			throws BaseException {
		XmlElementProperties p = new XmlElementProperties(_e,_properties);		
		configure(p);
	}

	@Override
	public void configure(Properties p) throws BaseException {
		String[] _rras = PropertiesConstants.getString(p,"rrm.rras","minute,halfhour,hour").split(",");

		for (int i = 0 ;i < _rras.length ; i ++){
			String id = _rras[i];
			if (id != null && id.length() > 0){
				RRArchive<data> newRRA = new RRArchive<data>(id);
				newRRA.configure(p);
				rras.put(id, newRRA);
			}
		}
	}
}
