package com.alogic.terminal.xscript;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import com.alogic.terminal.Terminal;
import com.alogic.terminal.local.Local;
import com.alogic.terminal.ssh.SSH;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.plugins.Segment;
import com.anysoft.util.DefaultProperties;
import com.anysoft.util.Factory;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * Shell插件
 * @author duanyy
 * @version 1.6.7.11 [20170203 duanyy] <br>
 * - 指令为空时将忽略，不执行 <br>
 * 
 * @version 1.6.9.9 [20170829 duanyy] <br>
 * - 增加ssh改密码功能 <br>
 */
public class XShell extends Segment{

	/**
	 * 属性列表
	 */
	protected DefaultProperties props = new DefaultProperties();	
	
	/**
	 * 上下文id
	 */
	protected String id = "$xshell";
	
	public XShell(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		id = PropertiesConstants.getString(p, "id", id ,true);
	}
	
	@Override
	public void configure(Element e, Properties p) {
		super.configure(e, p);

		//将element的配置保存下来
		props.Clear();
		props.loadFromElementAttrs(e);				
	}	

	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher){
		props.PutParent(ctx);
		try{
			Terminal t = null;
			TerminalFactory f = new TerminalFactory();
			String module = PropertiesConstants.getString(props,"module","local",true);
			try{
				t = f.newInstance(module, props);
			}catch (Exception ex){
				log(String.format("Can not create terminal with %s",module));
			}
			
			if (t != null){
				try{
					t.connect();
					ctx.setObject(id, t);
					super.onExecute(root, current, ctx, watcher);
				}finally{
					ctx.removeObject(id);
					t.disconnect();										
				}
			}
		}finally{
			props.PutParent(null);
		}
	}
	
	/**
	 * 工厂类
	 * @author yyduan
	 *
	 */
	public static class TerminalFactory extends Factory<Terminal>{
		protected static Map<String,String> mapping = new HashMap<String,String>();
		public String getClassName(String module){
			String found = mapping.get(module);
			return StringUtils.isEmpty(found)?module:found;
		}
		
		static{
			mapping.put("local", Local.class.getName());
			mapping.put("ssh", SSH.class.getName());
		}
	}
}
