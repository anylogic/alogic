package com.alogic.rpc.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import com.alogic.rpc.Call;
import com.alogic.rpc.InvokeContext;
import com.alogic.rpc.InvokeFilter;
import com.alogic.rpc.Parameters;
import com.alogic.rpc.Result;
import com.alogic.rpc.call.local.LocalCall;
import com.alogic.rpc.message.RPCMessage;
import com.alogic.rpc.serializer.Serializer;
import com.alogic.rpc.serializer.kryo.KryoSerializer;
import com.anysoft.util.Factory;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.backend.Context;
import com.logicbus.backend.Servant;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.models.servant.ServiceDescription;

/**
 * RPC专用Servant
 * 
 * @author duanyy
 * @since 1.6.7.15
 * 
 * @version 1.6.8.7 [20170417 duanyy]<br>
 * - 序列化接口增加上下文 <br>
 */
public class RPCServant extends Servant {
	protected Object instance = null;
	protected Call theCall = null;
	protected Serializer serializer = null;
	protected InvokeFilter filter = null;
	protected boolean keepAliveEnable = true;
	
	@Override
	public int actionProcess(Context ctx){
		String method = getArgument("method","$list",ctx);
		if (method.equals("$list")){
			list(ctx);
			return 0;
		}

		RPCMessage msg = (RPCMessage) ctx.asMessage(RPCMessage.class);
		InputStream in = msg.getInputStream();
		Parameters params = (Parameters) serializer.readObject(in, Parameters.Default.class,ctx);	
		if (params == null){
			params = new Parameters.Default();
		}
		if (filter != null){
			InvokeContext invokeContext = params.context();
			if (invokeContext != null){
				filter.doFilter(invokeContext);
			}
		}
		
		Result result = theCall.invoke(instance.getClass().getName(),method, params);
		result.host(ctx.getHost());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		serializer.writeObject(out, result,ctx);
		byte[] data = out.toByteArray();
		ctx.setResponseContentLength(data.length);
		if (keepAliveEnable){
			ctx.setResponseHeader("Connection","Keep-Alive");
		}else{
			ctx.setResponseHeader("Connection","Close");
		}
		Context.writeToOutpuStream(msg.getOutputStream(), data);
		return 0;
	}

	private void list(Context ctx) {
		JsonMessage msg = (JsonMessage)ctx.asMessage(JsonMessage.class);
		
		if (instance == null){
			throw new ServantException("core.impl_is_null","The impl instance is null.");
		}
		
		Map<String,Object> root = msg.getRoot();

		Map<String,Object> service = new HashMap<String,Object>();
				
		Class<?> clazz = instance.getClass();
		
		List<Object> methodList = new ArrayList<Object>();
		
		Method [] methods = clazz.getDeclaredMethods();
		
		for (Method m:methods){
			if (Modifier.isPublic(m.getModifiers())){
				//只发布public的方法
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("name", m.getName());
				map.put("return", m.getReturnType().getName());
			
				List<Object> parameterList = new ArrayList<Object>();
				
				Class<?>[] paramClasses = m.getParameterTypes();
				for (Class<?> c:paramClasses){
					parameterList.add(c.getName());
				}
				
				map.put("parameters", parameterList);
				
				methodList.add(map);
			}
		}
		
		service.put("methods", methodList);
		JsonTools.setString(service, "module", clazz.getName());
		root.put("service", service);
	}

	public void create(ServiceDescription sd){
		super.create(sd);		
		Properties p = sd.getProperties();
		keepAliveEnable = PropertiesConstants.getBoolean(p,"rpc.http.keepAlive.enable",keepAliveEnable);
		String impl = PropertiesConstants.getString(p,"servant.impl","");
		if (StringUtils.isEmpty(impl)){
			throw new ServantException("core.servant_no_impl","Can not find impl class name in proeprties.");
		}

		try {
			Factory<Object> f = new Factory<Object>();
			instance = f.newInstance(impl, p);
		}catch (Exception ex){
			throw new ServantException("core.instance_failed","Can not create impl instance:" + impl);
		}
		
		theCall = new LocalCall();
		
		String serializerClass = PropertiesConstants.getString(p,"rpc.serializer",KryoSerializer.class.getName());		
		Factory<Serializer> factory = new Factory<Serializer>();
		try {
			serializer = factory.newInstance(serializerClass, p);
		}catch (Exception ex){
			serializer = new KryoSerializer();
			serializer.configure(p);
			logger.error("Can not create serializer,use default:" + serializer.getClass().getName(),ex);
		}
		
		String filterClass = PropertiesConstants.getString(p,"rpc.filter","");
		if (StringUtils.isNotEmpty(filterClass)){
			try {
				Factory<InvokeFilter> f = new Factory<InvokeFilter>();
				filter = f.newInstance(filterClass, p);
			}catch (Exception ex){
				logger.error("Can not create filter,module=" + filterClass);
			}
		}
	}

	public void close(){
		super.close();
	}	
}
