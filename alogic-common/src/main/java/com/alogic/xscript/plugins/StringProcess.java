package com.alogic.xscript.plugins;

import org.apache.commons.lang3.StringUtils;

import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 字符串处理
 * 
 * @author yyduan
 *
 */
public class StringProcess extends Segment{
	protected String cid = "$string-buffer";
	protected String id;
	protected boolean output = true;
	protected String $value;

	public StringProcess(String tag, Logiclet p) {
		super(tag, p);
		
		registerModule("sp-replace",Replace.class);
		registerModule("sp-append",Append.class);
		registerModule("sp-substr",Substr.class);
		registerModule("sp-index",Index.class);
		registerModule("sp-reverse",Reverse.class);
		registerModule("sp-insert",Insert.class);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		cid = PropertiesConstants.getString(p, "cid", cid,true);
		id = PropertiesConstants.getString(p, "id", "$" + this.getXmlTag(),true);
		$value = PropertiesConstants.getRaw(p,"value","");
		output = PropertiesConstants.getBoolean(p,"output",true,true);
	}
	
	@Override
	protected void onExecute(XsObject root, XsObject current,LogicletContext ctx, ExecuteWatcher watcher) {
		String value = PropertiesConstants.transform(ctx, $value, "");
		StringBuffer sb = new StringBuffer(value);
		try {
			ctx.setObject(cid, sb);
			super.onExecute(root, current, ctx, watcher);
			if (output && StringUtils.isNotEmpty(id)) {
				String result = sb.toString();
				if (StringUtils.isNotEmpty(result)) {
					ctx.SetValue(id, result);
				}
			}
		} finally {
			ctx.removeObject(cid);
		}
	}
	
	/**
	 * 替代
	 * @author yyduan
	 *
	 */
	public static final class Replace extends AbstractLogiclet{
		protected String pid = "$string-buffer";
		protected String $value = "*";
		protected String $start = "0";
		protected String $end = "0";
		
		public Replace(String tag, Logiclet p) {
			super(tag, p);
		}
		
		@Override
		public void configure(Properties p){
			super.configure(p);
			
			pid = PropertiesConstants.getString(p, "cid", pid,true);
			$value = PropertiesConstants.getRaw(p,"value","");
			$start = PropertiesConstants.getRaw(p,"start","0");
			$end = PropertiesConstants.getRaw(p,"end","0");
		}	
		
		@Override
		protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
			StringBuffer sb = ctx.getObject(pid);
			if (sb != null){
				int start = PropertiesConstants.transform(ctx, $start, 0);
				int end = PropertiesConstants.transform(ctx, $end, 0);
				String value = PropertiesConstants.transform(ctx,$value,"*");
				if (end > start){
					sb.replace(start, end, getReplaceStr(value,end - start));
				}
			}
		}		
		
		protected static String getReplaceStr(String replaceStr,int length){
			int size = replaceStr.length();
			if (size >= length){
				return replaceStr.substring(0, length);
			}
			
			return getReplaceStr(replaceStr + replaceStr,length);
		}
		
	}
	
	/**
	 * Append
	 * @author yyduan
	 *
	 */
	public static final class Append extends AbstractLogiclet{
		protected String pid = "$string-buffer";
		protected String $value = "";
		
		public Append(String tag, Logiclet p) {
			super(tag, p);
		}
		
		@Override
		public void configure(Properties p){
			super.configure(p);
			
			pid = PropertiesConstants.getString(p, "cid", pid,true);
			$value = PropertiesConstants.getRaw(p,"value","");
		}	
		
		@Override
		protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
			StringBuffer sb = ctx.getObject(pid);
			if (sb != null){
				String value = PropertiesConstants.transform(ctx,$value,"");
				if (StringUtils.isNotEmpty(value)){
					sb.append(value);
				}
			}
		}	
	}

	/**
	 * reverse
	 * 
	 * @author yyduan
	 *
	 */
	public static final class Reverse extends AbstractLogiclet {
		protected String pid = "$string-buffer";

		public Reverse(String tag, Logiclet p) {
			super(tag, p);
		}

		@Override
		public void configure(Properties p) {
			super.configure(p);

			pid = PropertiesConstants.getString(p, "cid", pid, true);
		}

		@Override
		protected void onExecute(XsObject root, XsObject current,
				LogicletContext ctx, ExecuteWatcher watcher) {
			StringBuffer sb = ctx.getObject(pid);
			if (sb != null) {
				sb.reverse();
			}
		}
	}

	/**
	 * reverse
	 * 
	 * @author yyduan
	 *
	 */
	public static final class Substr extends AbstractLogiclet {
		protected String pid = "$string-buffer";
		protected String id = "";
		protected String $start = "0";
		protected String $end = "0";

		public Substr(String tag, Logiclet p) {
			super(tag, p);
		}

		@Override
		public void configure(Properties p) {
			super.configure(p);

			pid = PropertiesConstants.getString(p, "cid", pid, true);
			id = PropertiesConstants.getString(p, "id", "$" + this.getXmlTag(),
					true);
			$start = PropertiesConstants.getRaw(p, "start", "0");
			$end = PropertiesConstants.getRaw(p, "end", "0");
		}

		@Override
		protected void onExecute(XsObject root, XsObject current,
				LogicletContext ctx, ExecuteWatcher watcher) {
			StringBuffer sb = ctx.getObject(pid);
			if (sb != null) {
				int start = PropertiesConstants.transform(ctx, $start, 0);
				int end = PropertiesConstants.transform(ctx, $end, sb.length());
				if (end > start) {
					String result = sb.substring(start, end);
					if (StringUtils.isNotEmpty(id)) {
						ctx.SetValue(id, result);
					}
				}
			}
		}
	}

	/**
	 * Index
	 * 
	 * @author yyduan
	 *
	 */
	public static final class Index extends AbstractLogiclet {
		protected String pid = "$string-buffer";
		protected String id = "";
		protected String $from = "0";
		protected String $value = "";

		public Index(String tag, Logiclet p) {
			super(tag, p);
		}

		@Override
		public void configure(Properties p) {
			super.configure(p);

			pid = PropertiesConstants.getString(p, "cid", pid, true);
			id = PropertiesConstants.getString(p, "id", "$" + this.getXmlTag(),
					true);
			$from = PropertiesConstants.getRaw(p, "from", "0");
			$value = PropertiesConstants.getRaw(p, "value", "");
		}

		@Override
		protected void onExecute(XsObject root, XsObject current,
				LogicletContext ctx, ExecuteWatcher watcher) {
			StringBuffer sb = ctx.getObject(pid);
			if (sb != null && StringUtils.isNotEmpty(id)) {
				int from = PropertiesConstants.transform(ctx, $from, 0);
				String value = PropertiesConstants.transform(ctx, $value, "");
				int offset = sb.indexOf(value, from);
				ctx.SetValue(id, String.valueOf(offset));
			}
		}
	}

	/**
	 * Insert
	 * 
	 * @author yyduan
	 *
	 */
	public static final class Insert extends AbstractLogiclet {
		protected String pid = "$string-buffer";
		protected String $from = "0";
		protected String $value = "";

		public Insert(String tag, Logiclet p) {
			super(tag, p);
		}

		@Override
		public void configure(Properties p) {
			super.configure(p);

			pid = PropertiesConstants.getString(p, "cid", pid, true);
			$from = PropertiesConstants.getRaw(p, "from", "0");
			$value = PropertiesConstants.getRaw(p, "value", "");
		}

		@Override
		protected void onExecute(XsObject root, XsObject current,
				LogicletContext ctx, ExecuteWatcher watcher) {
			StringBuffer sb = ctx.getObject(pid);
			if (sb != null) {
				int from = PropertiesConstants.transform(ctx, $from, 0);
				String value = PropertiesConstants.transform(ctx, $value, "");
				if (from > 0 && from < sb.length()) {
					sb.insert(from, value);
				}
			}
		}
	}
}
