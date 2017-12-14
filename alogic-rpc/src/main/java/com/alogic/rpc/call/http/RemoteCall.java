package com.alogic.rpc.call.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import com.alogic.remote.Client;
import com.alogic.remote.Request;
import com.alogic.remote.Response;
import com.alogic.remote.httpclient.HttpClient;
import com.alogic.rpc.Call;
import com.alogic.rpc.CallException;
import com.alogic.rpc.InvokeContext;
import com.alogic.rpc.InvokeFilter;
import com.alogic.rpc.Parameters;
import com.alogic.rpc.Result;
import com.alogic.rpc.serializer.Serializer;
import com.alogic.rpc.serializer.kryo.KryoSerializer;
import com.alogic.tracer.Tool;
import com.alogic.tracer.TraceContext;
import com.anysoft.util.DefaultProperties;
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;

/**
 * 基于remote模块的Call
 * 
 * @author yyduan
 * @since 1.6.8.13
 */
public class RemoteCall extends Call.Abstract {
	
	/**
	 * 序列化器
	 */
	protected Serializer serializer = null;
	
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
	protected String path = "/services/ctgae";
	
	@Override
	public Result invoke(String id, String method, Parameters params) {
		// 处理context
		if (!filters.isEmpty()) {
			InvokeContext ctx = new InvokeContext.Default();
			for (InvokeFilter f : filters) {
				if (f != null) {
					f.doFilter(ctx);
				}
			}

			if (!ctx.isEmpty()) {
				params.context(ctx);
			}
		}		
		Result result = null;
		//开始调用			
		TraceContext ctx = Tool.start();		
		try {	
			TraceContext child = ctx == null ? null:ctx.newChild();
			//按照路由生成路径
			String invokePath = path + "/" + id + "?method=" + method;

			result = invoke(child == null ? null:child.sn(),child == null ? "1":child.order(),invokePath,params);
			
			//结束调用
			Tool.end(ctx,"HttpCall",method + "@" + id,"OK","");			
		} catch (Exception e) {
			//结束调用
			Tool.end(ctx,"HttpCall",method + "@" + id,"FAILED",e.getMessage());			
			throw e;
		}			
		return result;
	}

	protected Result invoke(final String sn, final String order,final String path,final Parameters params) {
		Request request = client.build("post");
		try {
			if (params != null) {
				params.sn(sn);
				params.order(order);
				request.setHeader("GlobalSerial", sn);
				request.setHeader("GlobalSerialOrder", order);
			}
			
			request.setBody(new Request.DirectOutput(){
				@Override
				public void writeTo(OutputStream outstream) throws IOException {
					serializer.writeObject(outstream, params,callContext);
				}
			});
			
			Response response = request.execute(path, sn, callContext);
			
			if (response.getStatusCode() != HttpURLConnection.HTTP_OK) {
				throw new CallException("core.e1605",
						"Error occurs when invoking service :" + response.getReasonPhrase());
			}
			
			InputStream in = response.asStream();
			try{
				if (in != null){
					return serializer.readObject(in, Result.Default.class,callContext);
				}else{
					throw new CallException("core.e1004","the inputstream from server is null");				
				}
			}finally{
				IOTools.close(in);
			}
		}catch (IOException ex){
			throw new CallException("core.e1004","Can not read result from server.", ex);
		}finally{
			IOTools.close(request);
		}
	}

	@Override
	public void configure(Properties p) {
		callContext = new DefaultProperties("default",Settings.get());
		path = PropertiesConstants.getString(p,"rpc.ketty.root",path);
		String version = PropertiesConstants.getString(p,"rpc.ketty.version", "");
		String label = PropertiesConstants.getString(p,"rpc.ketty.label", "");
	
		if (StringUtils.isNotEmpty(label)){
			callContext.SetValue("label", label);
		}
		
		if (StringUtils.isNotEmpty(version)){
			callContext.SetValue("version", version);
		}	
		/**
		 * 初始化序列化器
		 */
		if (serializer == null){
			String serializerClass = PropertiesConstants.getString(p,"rpc.serializer",KryoSerializer.class.getName());
			Factory<Serializer> factory = new Factory<Serializer>();
			try {
				serializer = factory.newInstance(serializerClass, p);
			}catch (Exception ex){ // NOSONAR
				LOG.error(String.format("Can not create serializer %s", serializerClass));
				serializer = new KryoSerializer();
				serializer.configure(p);
				LOG.info(String.format("Using default, Current serailizer is %s",serializer.getClass().getName()));
			}		
		}
	}
	
	public void configure(Element e, Properties p) {
		super.configure(e, p);
		
		if (client == null){
			Factory<Client> f = new Factory<Client>();
			try {
				client = f.newInstance(e, p, "remote", HttpClient.class.getName());
			}catch (Exception ex){
				LOG.error(String.format("Can not remote client with %s", XmlTools.node2String(e)));
				client = new HttpClient();
				client.configure(e, p);
				LOG.info(String.format("Using default,Current remote client is %s",client.getClass().getName()));
			}
		}
	}

}
