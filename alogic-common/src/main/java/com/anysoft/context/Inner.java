package com.anysoft.context;

import java.util.Map;

import org.w3c.dom.Element;
import com.anysoft.util.Properties;
import com.anysoft.util.Reportable;
import com.anysoft.util.Watcher;

/**
 * Source文件内置的Context
 * 
 * @author duanyy
 *
 * @param <O>
 * 
 * @since 1.5.0
 * 
 * @version 1.5.2 [20141017 duanyy] <br>
 * - 实现Reportable接口 <br>
 * 
 * @version 1.6.0.2 [20141108 duanyy] <br>
 * - 优化Reportable实现，输出所持有的对象信息 <br>
 * 
 * @version 1.6.4.20 [20151222 duanyy] <br>
 * - 根据sonar建议优化代码 <br>
 */
public abstract class Inner<O extends Reportable> implements Context<O> {
	
	/**
	 * Holder
	 */
	protected Holder<O> holder = null;
	
	@Override
	public void close(){
		if (holder != null){
			holder.close();
		}
	}

	@Override
	public void configure(Element element, Properties props){
		holder = new Holder<O>(getDefaultClass(),getObjectName()); // NOSONAR
		holder.configure(element, props);
	}
	
	public abstract String getObjectName();
	public abstract String getDefaultClass();

	@Override
	public O get(String id) {
		return holder != null ? holder.get(id) : null;
	}

	@Override
	public void addWatcher(Watcher<O> watcher) {
		// nothing to do
	}

	@Override
	public void removeWatcher(Watcher<O> watcher) {
		// nothing to do
	}

	@Override
	public void report(Element xml){
		if (xml != null){
			xml.setAttribute("module", getClass().getName());
			xml.setAttribute("dftClass", getDefaultClass());
			xml.setAttribute("objName", getObjectName());
			
			xml.setAttribute("objCnt", String.valueOf(holder != null ? holder.getObjectCnt():0));
			
			if (holder != null && holder.getObjectCnt() > 0){
				holder.report(xml);
			}
		}
	}
	
	@Override
	public void report(Map<String,Object> json){
		if (json != null){
			json.put("module", getClass().getName());
			json.put("dftClass", getDefaultClass());
			json.put("objName", getObjectName());
			
			json.put("objCnt", String.valueOf(holder != null ? holder.getObjectCnt():0));
			
			if (holder != null && holder.getObjectCnt() > 0){
				holder.report(json);
			}
		}
	}
}
