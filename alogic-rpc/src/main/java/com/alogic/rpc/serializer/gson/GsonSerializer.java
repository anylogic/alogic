package com.alogic.rpc.serializer.gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import com.alogic.rpc.Parameters;
import com.alogic.rpc.Result;
import com.alogic.rpc.serializer.Serializer;
import com.alogic.rpc.serializer.gson.util.ParametersSerializer;
import com.alogic.rpc.serializer.gson.util.ResultSerializer;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.esotericsoftware.minlog.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 基于google gson的序列化器
 * 
 * @author yyduan
 * @since 1.6.8.7
 * 
 */
public class GsonSerializer extends Serializer.Abstract {
	protected Gson gson = null;
	protected String encoding = "utf-8";
	
	@Override
	public <D> D readObject(InputStream in, Class<D> clazz,Properties ctx) {
		InputStreamReader reader = null;
		try {
			reader = new InputStreamReader(in,encoding);
			return gson.fromJson(reader, clazz);
		} catch (UnsupportedEncodingException e) {
			Log.error(String.format("Encoding %s is not supported.",encoding));
			return null;
		} finally {
			IOTools.close(reader);
		}
	}

	@Override
	public void writeObject(OutputStream out, Object object,Properties ctx) {
		OutputStreamWriter writer = null;
		try {
			writer = new OutputStreamWriter(out,encoding);
			gson.toJson(object, writer);
		} catch (UnsupportedEncodingException e) {
			Log.error(String.format("Encoding %s is not supported.",encoding));
		}finally {
			IOTools.close(writer);
		}
	}

	@Override
	public void configure(Properties p) {		
		ResultSerializer resultSerializer = new ResultSerializer();
		resultSerializer.configure(p);
		ParametersSerializer parametersSerializer = new ParametersSerializer();
		parametersSerializer.configure(p);
		
		gson = (new GsonBuilder())
				.registerTypeAdapter(Result.Default.class, resultSerializer)
				.registerTypeAdapter(Parameters.Default.class, parametersSerializer).create();
	}
}
