package com.anysoft.batch;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

/**
 * 指令
 * 
 * @author duanyy
 * @version 1.6.4.17 [20151216 duanyy] <br>
 * - 根据sonar建议优化代码 <br>
 */
public class Command implements XMLConfigurable,CommandHelper {
	protected String id;

	protected String note;
		
	protected String module;
	
	/**
	 *　命令行参数
	 */
	protected List<Argument> arguments = new ArrayList<Argument>();	//NO SONAR
	
	/**
	 * to get note
	 * @return the note
	 */
	public String getNote(){
		return note;
	}
	
	/**
	 * to get id
	 * @return the id
	 */
	public String getId(){
		return id;
	}	
	/**
	 * to get module
	 * @return the module
	 */
	public String getModule(){
		return module;
	}
	
	public boolean isOk(){
		return id != null && id.length() > 0;
	}
	
	/**
	 * 获取命令行参数列表
	 * @return 参数列表
	 */
	public List<Argument> getArguments(){return arguments;}
	
	/**
	 * 从XML节点中装入配置
	 */
	@Override
	public void configure(Element element, Properties props) {
		Properties p = new XmlElementProperties(element,props);
		
		id = PropertiesConstants.getString(p, "id", "");
		note = PropertiesConstants.getString(p, "note", "");
		module = PropertiesConstants.getString(p, "process", DefaultProcess.class.getName());
		
		NodeList argus = XmlTools.getNodeListByPath(element, "argument");
		if (argus != null && argus.getLength() > 0){
			for (int i = 0 ;i < argus.getLength() ; i ++){
				Node n = argus.item(i);
				
				if (Node.ELEMENT_NODE != n.getNodeType()){
					continue;
				}
				
				Element argument = (Element) n;
				
				Argument argu = new Argument();
				argu.configure(argument, p);
				
				if (argu.isOK()){
					arguments.add(argu);
				}
			}
		}
	}

	@Override
	public void printHelp(PrintStream ps) {
		ps.println("Command\t:" + getId() + "\t" + getNote());
		
		if (!arguments.isEmpty()){
			ps.println("\t|Arguments are listed below:");
			
			for (Argument argu:arguments){
				argu.printHelp(ps);
			}
		}
	}
}
