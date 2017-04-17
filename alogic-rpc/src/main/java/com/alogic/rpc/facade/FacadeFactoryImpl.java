package com.alogic.rpc.facade;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alogic.rpc.Call;
import com.alogic.rpc.CallException;
import com.alogic.rpc.naming.CallFactory;
import com.anysoft.util.Properties;
import com.anysoft.util.Settings;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.LoaderClassPath;
import javassist.Modifier;
import javassist.NotFoundException;

/**
 * 通过Call实现的FacadeFactory
 * 
 * @author duanyy
 * @since 1.6.7.15
 * 
 * @version 1.6.8.8 [20170417 duanyy] <br>
 * - 对象缓存改为基于callId+className缓存 <br>
 */
public class FacadeFactoryImpl extends FacadeFactory.Abstract{
	/**
	 * a logger of log4j
	 */
	protected static final Logger LOG = LoggerFactory.getLogger(FacadeFactory.class);
	
	/**
	 * 缓存已经找到的类
	 */
	private static Map<String,Class<?>> cachedClass = new ConcurrentHashMap<String,Class<?>>();
	
	/**
	 * 缓存已经创建的类
	 */
	private Map<String,Facade> cachedObject = new ConcurrentHashMap<String,Facade>();
	
	public FacadeFactoryImpl(){
		// nothing to do
	}

	@Override
	public <I> I getInterface(String callId, Class<I> clazz) {
		return getInterface(callId,"",clazz);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <I> I getInterface(String callId, String service, Class<I> clazz) {
		try {
			String className = clazz.getName() + "Facade" + service;
			Facade facade = cachedObject.get(className + callId);
			if (facade == null){
				synchronized(this){
					facade = cachedObject.get(className);
					if (facade == null){
						Call call = CallFactory.getCall(callId);
						if (call == null){
							throw new CallException("client.call_not_found","Can not find a call named " + callId);
						}
						Class<I> facadeClass = makeFacadeClass(StringUtils.isEmpty(service)?clazz.getName():service,className,clazz);
						facade = (Facade) facadeClass.newInstance();
						facade.setCall(call);	
						cachedObject.put(className + callId, facade);
					}
				}
			}
			return (I)facade;
		} catch (NotFoundException e) {
			throw new CallException("client.class_not_found",e.getMessage(),e);
		} catch (CannotCompileException e) {
			throw new CallException("client.cannot_compile",e.getMessage(),e);
		} catch (IOException e) {
			throw new CallException("client.io_except",e.getMessage(),e);
		} catch (InstantiationException e) {
			throw new CallException("client.instantiation",e.getMessage(),e);
		} catch (IllegalAccessException e) {
			throw new CallException("client.illegal_access",e.getMessage(),e);
		} catch (ClassNotFoundException e) {
			throw new CallException("client.class_not_found",e.getMessage(),e);
		}
	}	

	@Override
	public void configure(Properties p) {
		// nothing to do
	}

	@SuppressWarnings("unchecked")
	private static <I> Class<I> makeFacadeClass(String service,String className,Class<I> intf) throws NotFoundException, CannotCompileException, IOException, ClassNotFoundException{
		@SuppressWarnings("rawtypes")
		Class clazz = cachedClass.get(className);
		if (clazz == null){	
			synchronized (FacadeFactoryImpl.class){
				ClassPool pool = ClassPool.getDefault();
				pool.appendClassPath(new LoaderClassPath(Settings.getClassLoader()));				
				CtClass cc = getClassIfExist(pool,className);
				if (cc == null){
					LOG.debug("Generate facade class...");
					LOG.debug("Class:" + className);
					CtClass facadeBase = pool.get(Facade.class.getName());
					LOG.debug("Base class:" + facadeBase.getName());
					
					cc = pool.makeClass(className,facadeBase);
					
					CtClass intfClass = pool.get(intf.getName());	
					cc.setInterfaces(new CtClass[]{intfClass});
					LOG.debug("Interface class:" + intfClass.getName());
			
					CtMethod [] methods = intfClass.getMethods();
					
					for (CtMethod m:methods){
						if ((m.getModifiers() & Modifier.ABSTRACT) != 0) {
							//只去实现abstract方法
							CtMethod ctMethod = CtNewMethod.make(
									m.getModifiers() ^ Modifier.ABSTRACT, 
									m.getReturnType(), 
									m.getName(), 
									m.getParameterTypes(), 
									m.getExceptionTypes(), "{return ($r) execute(\"" + service + "\",\"" + m.getName() + "\",$args);}", 
									cc);
							cc.addMethod(ctMethod);
							
							LOG.debug("Add method:" + ctMethod.getLongName());
						}
					}
					LOG.debug("Facade class is generated successfully.");
					clazz = cc.toClass();
				}else{
					clazz = Settings.getClassLoader().loadClass(className);
				}
				
				cachedClass.put(className, clazz);
			}
		}
		
		return clazz;
	}
	
	private static CtClass getClassIfExist(ClassPool pool,String name){
		return pool.getOrNull(name);
	}

}
