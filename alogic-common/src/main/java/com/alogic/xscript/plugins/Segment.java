package com.alogic.xscript.plugins;

import java.util.List;
import java.util.Map;

import com.alogic.xscript.Block;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;

/**
 * 同步执行块
 * 
 * @author duanyy
 *
 */
public class Segment extends Block {

	public Segment(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	protected void onExecute(Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx, ExecuteWatcher watcher) {
		List<Logiclet> list = children;

		for (int i = 0 ; i < list.size(); i ++){
			Logiclet logiclet = list.get(i);
			if (logiclet != null){
				logiclet.execute(root,current,ctx,watcher);
			}
		}
	}

}
