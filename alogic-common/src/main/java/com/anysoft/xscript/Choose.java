package com.anysoft.xscript;

import org.w3c.dom.Element;

import com.anysoft.selector.Selector;
import com.anysoft.selector.impl.Constants;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;

/**
 * choose语句
 * 
 * @author duanyy
 * @since 1.6.3.22
 * @version 1.6.3.23 [20150513 duanyy] <br>
 * - 优化编译模式 <br>
 */
public class Choose extends Block {
	protected Selector selector = null;
	public Choose(String _tag, Statement _parent) {
		super(_tag, _parent);
	}

	public int compiling(Element _e, Properties _properties,CompileWatcher watcher){
		selector = Selector.newInstanceWithDefault(_e, _properties, Constants.class.getName());
		if (selector == null){
			if (watcher != null){
				watcher.message(this, "error", "Can not create selector.tag=" + getXmlTag());
			}
		}
		return super.compiling(_e, _properties,watcher);
	}

	protected int onCompiling(Element _e, Properties p, CompileWatcher watcher) {
		return 0;
	}	
	
	protected int onExecute(Properties p, ExecuteWatcher watcher) throws BaseException {
		if (children.size() <= 0){
			return -1;
		}
		
		boolean result = true;
		if (selector != null){
			String value = selector.select(p);
			result = !value.equals("false");
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
