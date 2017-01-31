package com.alogic.terminal.xscript;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alogic.terminal.Resolver;
import com.alogic.terminal.Terminal;
import com.alogic.terminal.local.Local;
import com.alogic.terminal.ssh.SSH;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.DefaultProperties;
import com.anysoft.util.Factory;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

/**
 * Shell插件
 * @author duanyy
 *
 */
public class XShell extends AbstractLogiclet implements Resolver{
	
	/**
	 * 指令
	 */
	protected List<String> cmds = new ArrayList<String>();
	
	/**
	 * 属性列表
	 */
	protected DefaultProperties props = new DefaultProperties();	
	
	public XShell(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Element e, Properties p) {
		Properties props = new XmlElementProperties(e,p);
		configure(props);
	
		//将element的配置保存下来
		props.Clear();
		props.loadFromElementAttrs(e);		
		
		NodeList nodeList = XmlTools.getNodeListByPath(e, "cmd");
		cmds.clear();
		for (int i = 0 ;i < nodeList.getLength() ; i ++){
			Node n = nodeList.item(i);
			
			if (Node.ELEMENT_NODE != n.getNodeType()){
				continue;
			}
			
			Element elem = (Element)n;
			
			String c = elem.getAttribute("value");
			if (StringUtils.isNotEmpty(c)){
				cmds.add(c);
			}
		}		
	}	

	@Override
	public Object resolveBegin(String cmd) {
		return this;
	}

	@Override
	public void resolveLine(Object cookies, String content) {
		log(content, "info");
	}

	@Override
	public void resolveEnd(Object cookies) {
		// nothing to do
	}

	@Override
	protected void onExecute(Map<String, Object> root, Map<String, Object> current, LogicletContext ctx,
			ExecuteWatcher watcher) {
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
					for (String cmd:cmds){
						String transformed = ctx.transform(cmd);
						t.exec(this,transformed);
					}
				}finally{
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
