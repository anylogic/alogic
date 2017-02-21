package com.alogic.rpc.call.local;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.reflect.MethodUtils;

import com.alogic.rpc.Call;
import com.alogic.rpc.Parameters;
import com.alogic.rpc.Result;
import com.alogic.tracer.Tool;
import com.alogic.tracer.TraceContext;
import com.anysoft.util.Properties;
import com.anysoft.util.Settings;

/**
 * 本地过程调用
 * 
 * @author duanyy
 * @since 1.6.7.15
 */
public class LocalCall extends Call.Abstract {

	/**
	 * 实例缓存
	 */
	protected Map<String, Object> objects = new ConcurrentHashMap<String, Object>();
	protected static final Map<String, String> mappings = new HashMap<String, String>();

	public LocalCall() {
		// nothing to do
	}

	public static void addMaping(String intf, String impl) {
		mappings.put(intf, impl);
	}

	protected Object getAndCreateInstance(String id)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Object found = objects.get(id);
		if (found == null) {
			synchronized (this) {
				found = objects.get(id);
				if (found == null) {
					found = createInstance(id);
					objects.put(id, found);
				}
			}
		}
		return found;
	}

	protected Object createInstance(String id)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		String impl = mappings.get(id);
		if (impl == null) {
			impl = id;
		}

		ClassLoader cl = Settings.getClassLoader();
		return cl.loadClass(impl).newInstance();
	}

	@Override
	public Result invoke(String id, String method, Parameters params) {
		Result result = new Result.Default();

		long now = System.currentTimeMillis();
		String code = "core.ok";
		String reason = "It is ok";

		TraceContext ctx = Tool.start();
		try {
			// 从缓存中获取对象实例，如果不存在，则根据ID来创建，在某些场合下，ID就是类名
			Object instance = getAndCreateInstance(id);
			// 输入参数
			Object[] parameters = params.params();
			Object ret = MethodUtils.invokeMethod(instance, method, parameters);
			result.ret(ret);
		} catch (IllegalAccessException e) {
			code = "core.illegal_access";
			reason = e.getMessage();
		} catch (IllegalArgumentException e) {
			code = "core.illegal_argument";
			reason = e.getMessage();
		} catch (NoSuchMethodException e) {
			code = "core.no_such_method";
			reason = e.getMessage();
		} catch (SecurityException e) {
			code = "core.security";
			reason = e.getMessage();
		} catch (InstantiationException e) {
			code = "core.instantiation";
			reason = e.getMessage();
		} catch (ClassNotFoundException e) {
			code = "core.class_not_found";
			reason = e.getMessage();
		} catch (InvocationTargetException e) {
			result.setThrowable(e.getTargetException());
		} finally {
			// 输出结果(代码，原因，时长等)
			result.result(code, reason, System.currentTimeMillis() - now);
			if (code.equals("core.ok")) {
				Tool.end(ctx, "LocalCall", method + "@" + id, "OK", "");
			} else {
				Tool.end(ctx, "LocalCall", method + "@" + id, "FAILED", reason);
			}
		}
		return result;
	}

	@Override
	public void configure(Properties p) {

	}
}
