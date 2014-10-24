package com.logicbus.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.util.DefaultProperties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.backend.AbstractServant;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.backend.message.MessageDoc;
import com.logicbus.backend.message.XMLMessage;
import com.logicbus.models.servant.ServiceDescription;


/**
 * 查询当前JVM环境的Env变量
 * 
 * @author duanyy
 * 
 * @since 1.2.5.2
 */
public class EnvQuery extends AbstractServant {
	protected DefaultProperties toArray = null;
	
	protected void onDestroy() {
	}

	
	protected void onCreate(ServiceDescription sd) throws ServantException {
		String array = PropertiesConstants.getString(
				sd.getProperties(),
				"arrays",
				"java.class.path=:;java.library.path=:;PATH=:;CLASSPATH=:;java.ext.dirs=:;sun.boot.class.path=:",
				true);
		
		String delimeter1 = PropertiesConstants.getString(
				sd.getProperties(),
				"arrays.delimeter1",
				";",
				true);
		String delimeter2 = PropertiesConstants.getString(
				sd.getProperties(),
				"arrays.delimeter2",
				"=",
				true);		
		
		toArray = new DefaultProperties("Default",sd.getProperties());
		toArray.loadFromString(array,delimeter1,delimeter2);
	}

	
	protected int onXml(MessageDoc msgDoc, Context ctx) throws Exception {
		XMLMessage msg = (XMLMessage) msgDoc.asMessage(XMLMessage.class);		
		Document doc = msg.getDocument();
		Element root = msg.getRoot();
		
		{
			Element envs = doc.createElement("envs");
			Map<String,String> map = System.getenv();
			Set<Entry<String,String>> entries = map.entrySet();
			Iterator<Entry<String, String>> iter = entries.iterator();
			while (iter.hasNext()){
				Entry<String, String> value = iter.next();
				writeXml(value.getKey(),value.getValue(),"env",envs,doc);
			}
			root.appendChild(envs);
		}
		
		{
			Element envs = doc.createElement("properties");
			Set<Entry<Object,Object>> entries = System.getProperties().entrySet();
			Iterator<Entry<Object, Object>> iter = entries.iterator();
			while (iter.hasNext()){
				Entry<Object, Object> value = iter.next();
				writeXml(value.getKey().toString(),value.getValue().toString(),"property",envs,doc);
			}
			root.appendChild(envs);
		}		
		
		return 0;
	}

	private void writeXml(String id, String value, String tag,Element envs, Document doc) {
		Element env = doc.createElement(tag);
		String delimeter = PropertiesConstants.getString(toArray, id, "",true);
		if (validString(delimeter)){
			String [] values = value.split(delimeter);
			for (String v:values){
				Element vElem = doc.createElement("value");
				vElem.setAttribute("value", v);
				env.appendChild(vElem);
			}
			env.setAttribute("id", id);
		}else{
			env.setAttribute("id", id);
			env.setAttribute("value", value);
		}
		envs.appendChild(env);
	}

	
	protected int onJson(MessageDoc msgDoc, Context ctx) throws Exception {
		JsonMessage msg = (JsonMessage) msgDoc.asMessage(JsonMessage.class);

		Map<String,Object> json = msg.getRoot();
		{
			List<Object> envs = new ArrayList<Object>();
			Map<String,String> map = System.getenv();
			Set<Entry<String,String>> entries = map.entrySet();
			Iterator<Entry<String, String>> iter = entries.iterator();
			while (iter.hasNext()){
				Entry<String, String> value = iter.next();
				writeJson(value.getKey(),value.getValue(),envs);
			}
			json.put("envs", envs);
		}
		{
			List<Object> envs = new ArrayList<Object>();
			Set<Entry<Object,Object>> entries = System.getProperties().entrySet();
			Iterator<Entry<Object, Object>> iter = entries.iterator();
			while (iter.hasNext()){
				Entry<Object, Object> value = iter.next();
				writeJson(value.getKey().toString(),value.getValue().toString(),envs);
			}
			json.put("envs", envs);
		}		
		return 0;
	}
	
	private void writeJson(String id, String value, List<Object> envs) {
		Map<String,Object> env = new HashMap<String,Object>(2);
		String delimeter = PropertiesConstants.getString(toArray, id, "",true);
		if (validString(delimeter)){
			String [] values = value.split(delimeter);
			List<Object> vList = new ArrayList<Object>(values.length);
			
			for (String v:values){
				Map<String,Object> vMap = new HashMap<String,Object>(1);
				vMap.put("value", v);
				vList.add(vMap);
			}
			
			env.put("value", vList);
			env.put("id", id);
		}else{
			env.put("id", id);
			env.put("value", value);
		}
		envs.add(env);
	}

	protected boolean validString(String str){
		return str != null && str.length() > 0;
	}
}
