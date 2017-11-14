package com.alogic.remote.httpclient;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.w3c.dom.Element;

import com.alogic.remote.AbstractClient;
import com.alogic.remote.Request;
import com.alogic.remote.httpclient.customizer.Default;
import com.anysoft.util.Factory;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlTools;

/**
 * 基于apache http client实现的Client
 * 
 * @author yyduan
 * @since 1.6.8.12
 * 
 * @version 1.6.8.14 <br>
 * - 优化http远程调用的超时机制 <br>
 * 
 * @version 1.6.10.3 [20171009 duanyy] <br>
 * - 增加PUT,GET,DELETE,HEAD,OPTIONS,TRACE,PATCH等http方法; <br>
 * 
 * @version 1.6.10.6 [20171114 duanyy] <br>
 * - 增加Http调用请求级别的Filter和Client级别的Customizer <br>
 */
public class HttpClient extends AbstractClient{
	/**
	 * http client
	 */
	protected CloseableHttpClient httpClient = null;
	
	/**
	 * request configuration
	 */
	protected RequestConfig requestConfig = null;
	
	protected String encoding = "utf-8";
	
	protected int autoRetryCnt = 2;
	
	protected HttpCientFilter filter = null;
	
	protected HttpClientCustomizer customizer = null;
	
	/**
	 * Http method
	 * @author yyduan
	 *
	 */
	public enum Method {
		GET(HttpGet.class),
		POST(HttpPost.class),
		DELETE(HttpDelete.class),
		HEAD(HttpHead.class),
		OPTIONS(HttpOptions.class),
		TRACE(HttpTrace.class),
		PATCH(HttpPatch.class),
		PUT(HttpPut.class);
		
		protected Class<? extends HttpRequestBase> clazz;
		
		Method(Class<? extends HttpRequestBase> clazz){
			this.clazz = clazz;
		}
		
		public HttpRequestBase createRequest(){
			try {
				return this.clazz.newInstance();
			} catch (Exception ex){
				return null;
			}
		}
	};
	
	@Override
	protected void onConfigure(Element e,Properties p){
		//装入filter配置
		Element filterElem = XmlTools.getFirstElementByPath(e, "filter");
		if (filterElem != null){
			Factory<HttpCientFilter> factory = new Factory<HttpCientFilter>();
			try {
				filter = factory.newInstance(filterElem, p, "module");
			}catch (Exception ex){
				LOG.error(String.format("Can not create filter with %s", XmlTools.node2String(filterElem)));
			}
		}	
		
		Element customizerElem = XmlTools.getFirstElementByPath(e, "customizer");
		if (customizerElem != null){
			Factory<HttpClientCustomizer> factory = new Factory<HttpClientCustomizer>();
			try {
				customizer = factory.newInstance(customizerElem, p, "module");
			}catch (Exception ex){
				LOG.error(String.format("Can not create customizer with %s", XmlTools.node2String(customizerElem)));
			}
		}
		configure(p);
	}
	
	@Override
	public void configure(Properties p) {
		super.configure(p);
		
		encoding = PropertiesConstants.getString(p,"http.encoding",encoding);
		autoRetryCnt = PropertiesConstants.getInt(p, "rpc.ketty.autoRetryTimes", autoRetryCnt);
		
		if (customizer == null){
			customizer = new Default();
			customizer.configure(p);
		}
		
		httpClient = customizer.customizeHttpClient(HttpClients.custom(), p).build();
		requestConfig = customizer.customizeRequestConfig(RequestConfig.custom(), p).build();
	}
	
	@Override
	public Request build(String method) {
		HttpRequestBase request = getRequestByMethod(method);
		if (request == null){
			request = new HttpPost();
		}
		request.setConfig(requestConfig);
		return new HttpClientRequest(httpClient,request,this,encoding,autoRetryCnt);
	}
	
	public HttpRequestBase getRequestByMethod(final String method){
		Method m = Method.valueOf(method.toUpperCase());
		return m.createRequest();
	}
	
	public  void onRequest(HttpClientRequest request){
		if (filter != null){
			filter.onRequest(request);
		}
	}
	
	public void onResponse(HttpClientResponse response){
		if (filter != null){
			filter.onResponse(response);
		}
	}
}
