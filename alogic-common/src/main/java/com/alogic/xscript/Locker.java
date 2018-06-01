package com.alogic.xscript;

import java.lang.reflect.Constructor;
import java.util.concurrent.locks.Lock;
import org.apache.commons.lang3.StringUtils;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.plugins.Segment;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;

/**
 * 锁
 * @author yyduan
 *
 */
public class Locker extends Segment{
	protected String $id = "";
	protected String module = "";
	protected boolean wait = false;
	public Locker(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		$id = PropertiesConstants.getRaw(p,"id","");
		module = PropertiesConstants.getString(p, "module","");
		wait = PropertiesConstants.getBoolean(p,"wait",wait);
	}
	
	@Override
	protected void onExecute(XsObject root, XsObject current,
			final LogicletContext ctx, final ExecuteWatcher watcher) {
		String id = PropertiesConstants.transform(ctx, $id, "");
		
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
