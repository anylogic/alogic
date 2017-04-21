package com.alogic.remote.backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Reportable;
import com.anysoft.util.XmlTools;

/**
 * 指定App的后端节点列表
 * 
 * @author duanyy
 *
 * @version 1.1.10.23 [20161118 duanyy] <br>
 * - 支持超时，只能缓存30分钟 <br>
 */
public class AppBackends implements Reportable{
	/**
	 * 应用id
	 */
	protected String appId;
	
	/**
	 * 后端列表
	 */
	protected List<Backend> backends = new ArrayList<Backend>();
	
	/**
	 * 时间戳
	 */
	protected long timestamp = System.currentTimeMillis();
	
	public AppBackends(String id){
		appId = id;
	}
	
	public void addBackend(Backend b){
		backends.add(b);
	}
	
	public List<Backend> getBackends(){
		return backends;
	}

	@Override
	public void report(Element root) {
		if (root != null){
			XmlTools.setString(root,"id",appId);
			
			if (!backends.isEmpty()){				
				Document doc = root.getOwnerDocument();		
				for (Backend b:backends){
					Element backend = doc.createElement("backend");
					b.report(root);
					root.appendChild(backend);
				}
			}
		}
	}

	@Override
	public void report(Map<String, Object> json) {
		if (json != null){
			JsonTools.setString(json,"id",appId);
			
			if (!backends.isEmpty()){
				List<Object> backend = new ArrayList<Object>();
				
				for (Backend b:backends){
					Map<String,Object> map = new HashMap<String,Object>();					
					b.report(map);					
					backend.add(map);
				}
				
				json.put("backend", backend);
			}
		}
	}

	public String getId() {
		return appId;
	}

	public boolean isExpired() {
		return System.currentTimeMillis() - timestamp > 30 * 60 * 1000L;
	}

	public void expire() {
		 timestamp = 0;
	}	
}