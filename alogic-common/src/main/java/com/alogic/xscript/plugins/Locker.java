package com.alogic.xscript.plugins;

import java.lang.reflect.Constructor;
import java.util.concurrent.locks.Lock;

import org.apache.commons.lang3.StringUtils;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.LocalLock;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;

/**
 * 锁
 * @author yyduan
 * @version 1.6.11.44 [20180713 duanyy] <br>
 * - 采用LocalLock作为缺省实现 <br>
 */
public class Locker extends Segment{
	protected String $id = "";
	protected String $module = LocalLock.class.getName();
	protected boolean wait = false;
	public Locker(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		$id = PropertiesConstants.getRaw(p,"id","");
		$module = PropertiesConstants.getRaw(p, "module",$module);
		wait = PropertiesConstants.getBoolean(p,"wait",wait);
	}
	
	@Override
	protected void onExecute(XsObject root, XsObject current,
			final LogicletContext ctx, final ExecuteWatcher watcher) {
		String id = PropertiesConstants.transform(ctx, $id, "");
		String module = PropertiesConstants.transform(ctx, $module, "");
		Lock locker = null;
		if (StringUtils.isNotEmpty(id) && StringUtils.isNotEmpty(module)){
			locker = getLock(ctx.transform($id),module,ctx);
		}
		if (locker != null){
			if (wait){
				try {
					locker.lock();
					super.onExecute(root, current, ctx, watcher);
				}finally{
					locker.unlock();
				}				
			}else{
				try {
					if (locker.tryLock()){
						ctx.SetValue("$locked", "false");
						super.onExecute(root, current, ctx, watcher);
					}else{
						ctx.SetValue("$locked", "true");
					}
				}finally{
					locker.unlock();
				}
			}
		}else{
			synchronized(this){
				super.onExecute(root, current, ctx, watcher);
			}				
		}		
	}	
	
	private Lock getLock(String name,String module,Properties ctx) {
		Lock lock = null;
		try {
			ClassLoader cl = Settings.getClassLoader();
			
			@SuppressWarnings("unchecked")
			Constructor<Lock> constructor = (Constructor<Lock>) cl.loadClass(module).getConstructor(
					new Class[]{String.class,Properties.class}
					);
			
			lock = (Lock)constructor.newInstance(new Object[]{name,ctx});
		} catch (Exception e) {
			logger.error("Can not create Lock instance,module:" + module);
		}
		return lock;
	}	
}
