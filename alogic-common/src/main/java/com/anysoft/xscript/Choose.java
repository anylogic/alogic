package com.anysoft.xscript;

import org.w3c.dom.Element;

import com.anysoft.selector.Selector;
import com.anysoft.selector.impl.Constants;
import com.anysoft.util.Properties;

/**
 * choose语句
 * 
 * @author duanyy
 * @since 1.6.3.22
 * @version 1.6.3.23 [20150513 duanyy] <br>
 * - 优化编译模式 <br>
 * 
 * @version 1.6.4.33 [20160304 duanyy] <br>
 * - 根据sonar建议优化代码 <br>
 */
public class Choose extends Block {
	protected Selector selector = null;
	public Choose(String tag, Statement parent) {
		super(tag, parent);
	}

	@Override
	public int compiling(Element element, Properties props,CompileWatcher watcher){
		selector = Selector.newInstanceWithDefault(element, props, Constants.class.getName());
		if (selector == null && watcher != null){
			watcher.message(this, "error", "Can not create selector.tag=" + getXmlTag());
		}
		return super.compiling(element, props,watcher);
	}

	@Override
	protected int onCompiling(Element elem, Properties p, CompileWatcher watcher) {
		return 0;
	}	
	
	@Override
	protected int onExecute(Properties p, ExecuteWatcher watcher){
		if (children.isEmpty()){
			return -1;
		}
		
		boolean result = true;
		if (selector != null){
			String value = selector.select(p);
			result = !"false".equals(value);
		}
		
		if (result){
			Statement stmt = children.get(0);
			stmt.execute(p, watcher);
		}else{
			if (children.size() > 1){
				Statement stmt = children.get(1);
				stmt.execute(p, watcher);
			}
		}
		return 0;
	}


}
