package com.alogic.xscript.plugins;

import java.util.List;
import com.alogic.xscript.Block;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;

/**
 * 同步执行块
 * 
 * @author duanyy
 * 
 * @version 1.6.8.14 [20170509 duanyy] <br>
 * - 增加xscript的中间文档模型,以便支持多种报文协议 <br>
 * 
 */
public class Segment extends Block {

	public Segment(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		List<Logiclet> list = children;

		for (int i = 0 ; i < list.size(); i ++){
			Logiclet logiclet = list.get(i);
			if (logiclet != null){
				logiclet.execute(root,current,ctx,watcher);
			}
		}
	}

}
