package com.alogic.lucene.analyzer.ik.script;

import org.apache.commons.lang3.StringUtils;

import com.alogic.ik.dic.Dictionary;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 字典中屏蔽关键字
 * 
 * @author yyduan
 * @since 1.6.11.34
 */
public class DicDisableWord extends AbstractLogiclet{
	protected String pid = "$indexer-dic";
	protected String $word = "";
	
	public DicDisableWord(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		$word = PropertiesConstants.getRaw(p, "word", $word);
	}

	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		Dictionary dic = ctx.getObject(pid);
		if (dic == null){
			throw new BaseException("core.e1001","It must be in a lucene-dic context,check your together script.");
		}

		String word = PropertiesConstants.transform(ctx, $word, "");
		if (StringUtils.isNotEmpty(word)){
			dic.disableWord(word);
		}
	}
}
