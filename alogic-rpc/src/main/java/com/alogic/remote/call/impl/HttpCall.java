package com.alogic.remote.call.impl;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alogic.remote.Client;
import com.alogic.remote.Request;
import com.alogic.remote.Response;
import com.alogic.remote.call.Call;
import com.alogic.remote.call.CallException;
import com.alogic.remote.call.Parameters;
import com.alogic.remote.call.Result;
import com.alogic.remote.httpclient.HttpClient;
import com.alogic.remote.util.HttpQuery;
import com.anysoft.selector.FieldList;
import com.anysoft.selector.Selector;
import com.anysoft.util.DefaultProperties;
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.jayway.jsonpath.spi.JsonProvider;
import com.jayway.jsonpath.spi.JsonProviderFactory;

/**
 * Http Call
 * @author yyduan
 * @version 1.6.8.13 [duanyy 20170427] <br>
 * - 从alogic-remote中迁移过来 <br>
 */
public class HttpCall implements Call {
	/**
	 * a logger of slf4j
	 */
	protected static final Logger LOG = LoggerFactory.getLogger(Call.class);	
	
	/**
	 * json provider
	 */
	protected static final JsonProvider jsonProvider = JsonProviderFactory.createProvider();
	
	/**
	 * URL中的参数
	 */
	private FieldList queryParameters = null;

	/**
	 * Http entity中的数据
	 */
	private FieldList arguments = null;
	
	/**
	 * id paths
	 */
	protected Map<String,String> idPaths = new HashMap<String,String>();
	
	/**
	 * 调用上下文
	 */
	protected Properties callContext = null;
	
	/**
	 * remote client
	 */
	protected Client client = null;
	
	/**
	 * 服务调用根路径
	 */
	protected String path = "/services";
	
	@Override
	public void close() throws Exception {
	}

	@Override
	public void configure(Properties p){
		callContext = new DefaultProperties("default",Settings.get());		
		path = PropertiesConstants.getString(p,"rpc.ketty.root",path);
	}
	
	@Override
	public void configure(Element root, Properties props) {
		Properties p = new XmlElementProperties(root,props);
		
		//queryParameters
		Element qp = XmlTools.getFirstElementByPath(root, "request/query");
		if (qp != null){
			queryParameters = new FieldList();
			queryParameters.configure(qp, p);
		}
		
		//argument data
		Element ad = XmlTools.getFirstElementByPath(root, "request/data");
		if (ad != null){
			arguments = new FieldList();
			arguments.configure(ad, p);
		}
		
		//idpaths
		NodeList ips = XmlTools.getNodeListByPath(root, "response/data/field");
		if (ips != null)
		{
			for (int i = 0 ; i < ips.getLength() ; i ++){
				Node n = ips.item(i);
				if (n.getNodeType() != Node.ELEMENT_NODE){
					continue;
				}
				
				Element e = (Element)n;
				
				String id = e.getAttribute("id");
				String path = e.getAttribute("path");
				
				if (id == null || id.length() <= 0 || path == null || path.length() <= 0){
					continue;
				}
				
				idPaths.put(id, path);
			}
		}
		
		if (client == null){
			Factory<Client> f = new Factory<Client>();
			try {
				client = f.newInstance(root, props, "remote", HttpClient.class.getName());
			}catch (Exception ex){
				LOG.error(String.format("Can not remote client with %s", XmlTools.node2String(root)));
				client = new HttpClient();
				client.configure(root, p);
				LOG.info(String.format("Using default,Current remote client is %s",client.getClass().getName()));
			}
		}
		
		configure(p);
	}

	@Override
	public void report(Element xml) {
		if (xml != null){
			XmlTools.setString(xml, "module", getClass().getName());
		}
	}

	@Override
	public void report(Map<String, Object> json) {
		if (json != null){
			JsonTools.setString(json, "module", getClass().getName());
		}
	}

	@Override
	public Parameters createParameter() {
		return new HttpParameters();
	}

	@Override
	public Result execute(Parameters paras) throws CallException {
		return execute(paras,null,null);
	}

	@Override
	public Result execute(Parameters paras, String sn,String order)throws CallException {
		HttpQuery query = new HttpQuery(path);
		
		if (paras != null && queryParameters != null){
			Selector[] fields = queryParameters.getFields();
			if (fields != null && fields.length > 0){
				for (Selector s:fields){
					if (!s.isOk()){
						continue;
					}
					String id = s.getId();
					String value = s.select(paras);
					query.param(id, value);
				}
			}
		}
		
		Request request = client.build("post");
		
		try {
			if (StringUtils.isNotEmpty(sn) && StringUtils.isNotEmpty(order)){
				request.setHeader("GlobalSerial", sn);
				request.setHeader("GlobalSerialOrder", order);
			}
			
			Map<String,Object> docRoot = new HashMap<String,Object>();
			
			if (paras != null && arguments != null){
				Selector[] fields = arguments.getFields();
				if (fields != null && fields.length > 0){
					for (Selector s:fields){
						if (!s.isOk()){
							continue;
						}
						String id = s.getId();
						
						Object data = paras.getData(id);
						if (data != null){
							docRoot.put(id, data);
						}
					}
				}
			}
			
			request.setBody(jsonProvider.toJson(docRoot));
			
			Response response = request.execute(query.toString(), sn, callContext);
		
			if (response.getStatusCode() != HttpURLConnection.HTTP_OK) {
				throw new CallException("core.invoke_error",
						"Error occurs when invoking service :"
								+ response.getReasonPhrase());
			}
			
			InputStream in = response.asStream();			
			try{
				if (in != null){
					@SuppressWarnings("unchecked")
					Map<String,Object> doc = (Map<String, Object>) jsonProvider.parse(in);
					return new HttpResult(doc,idPaths);
				}else{
					throw new CallException("core.invoke_error","the inputstream from server is null");				
				}
			}finally{
				IOTools.close(in);
			}			
		}catch (Exception ex){
			throw new CallException("core.io_error","Can not read result from server.", ex);
		}finally{
			IOTools.close(request);
		}
	}

}