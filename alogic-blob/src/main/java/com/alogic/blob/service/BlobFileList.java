package com.alogic.blob.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.blob.context.BlobManagerSource;
import com.alogic.blob.core.BlobManager;
import com.logicbus.backend.AbstractServant;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.backend.message.XMLMessage;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 查询指定blob中文件id列表
 * 
 * @author duanyy
 * @since 1.6.4.7
 */
public class BlobFileList extends AbstractServant {

	protected int onXml(Context ctx) throws Exception{
		XMLMessage msg = (XMLMessage) ctx.asMessage(XMLMessage.class);

		String id = getArgument("id",ctx);
		
		Document doc = msg.getDocument();
		Element root = msg.getRoot();
		
		BlobManagerSource src = BlobManagerSource.get();
		BlobManager found = src.get(id);
		if (found == null){
			throw new ServantException("user.data_not_found","Can not find the blob manager :" + id);
		}
		
		String cookies = getArgument("cookies","",ctx);
		int limit = getLimit(ctx);
		
		List<String> ids = new ArrayList<String>();
		
		cookies = found.list(ids, cookies, limit);
		
		Element _blob = doc.createElement("blob");
		_blob.setAttribute("id", id);
		
		if (cookies != null){
			_blob.setAttribute("cookies", cookies);
		}
		
		_blob.setAttribute("limit", String.valueOf(limit));
		
		if (ids.size() > 0){
			for (String _id:ids){
				Element elem = doc.createElement("file");
				elem.setAttribute("id", _id);
				_blob.appendChild(elem);
			}
		}
		
		root.appendChild(_blob);

		return 0;
	}
	
	private int getLimit(Context ctx){
		String value = getArgument("limit","100",ctx);
		try {
			return Integer.parseInt(value);
		}catch (Exception ex){
			return 100;
		}
	}
	
	protected int onJson(Context ctx) throws Exception{
		JsonMessage msg = (JsonMessage)ctx.asMessage(JsonMessage.class);
		String id = getArgument("id",ctx);
		
		BlobManagerSource src = BlobManagerSource.get();
		BlobManager found = src.get(id);
		if (found == null){
			throw new ServantException("user.data_not_found","Can not find the blob manager :" + id);
		}
		
		String cookies = getArgument("cookies","",ctx);
		int limit = getLimit(ctx);
		
		List<String> ids = new ArrayList<String>();
		
		cookies = found.list(ids, cookies, limit);
		
		Map<String,Object> _blob = new HashMap<String,Object>();
		
		_blob.put("file",ids);
		_blob.put("id", id);
		if (cookies != null){
			_blob.put("cookies",cookies);
		}
		_blob.put("limit", limit);
		
		msg.getRoot().put("blob", _blob);
		
		return 0;
	}
	
	@Override
	protected void onDestroy() {
		
	}
	@Override
	protected void onCreate(ServiceDescription sd) throws ServantException {

	}

}