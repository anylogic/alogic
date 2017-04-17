package com.alogic.rpc.serializer.gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import com.alogic.rpc.Parameters;
import com.alogic.rpc.Result;
import com.alogic.rpc.serializer.Serializer;
import com.alogic.rpc.serializer.gson.util.ParametersSerializer2;
import com.alogic.rpc.serializer.gson.util.ResultSerializer2;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 基于google gson的序列化器
 * 
 * @author yyduan
 * @since 1.6.8.7
 */

public class GsonSerializer2 extends Serializer.Abstract {
	protected Gson gson = null;
	protected ResultSerializer2 resultSerializer = null;
	protected ParametersSerializer2 parametersSerializer = null;
	
	public void setRequestClass(Class<?> requestClass){
		if (parametersSerializer != null){
			parametersSerializer.resetRequestClass(requestClass);
		}
	}
	@Override
	public <D> D readObject(InputStream in, Class<D> clazz,Properties ctx) {
		InputStreamReader reader = new InputStreamReader(in);
		try {
			return gson.fromJson(reader, clazz);
		} finally {
			IOTools.close(reader);
		}
	}

	@Override
	public void writeObject(OutputStream out, Object object,Properties ctx) {
		OutputStreamWriter writer = new OutputStreamWriter(out);
		try {
			gson.toJson(object, writer);
		} finally {
			IOTools.close(writer);
		}
	}

	@Override
	public void configure(Properties p) {
		resultSerializer = new ResultSerializer2();
		resultSerializer.configure(p);
		parametersSerializer = new ParametersSerializer2();
		parametersSerializer.configure(p);
		
		gson = (new GsonBuilder())
				.registerTypeAdapter(Result.Default.class, resultSerializer)
				.registerTypeAdapter(Parameters.Default.class, parametersSerializer).create();
	}
}
