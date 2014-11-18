package com.anysoft.batch;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

/**
 * 指令
 * 
 * @author duanyy
 *
 */
public class Command implements XMLConfigurable,CommandHelper {
	protected String id;
	/**
	 * to get id
	 * @return the id
	 */
	public String getId(){
		return id;
	}
	
	protected String note;
	/**
	 * to get note
	 * @return the note
	 */
	public String getNote(){
		return note;
	}
	
	public String module;
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
	 *　命令行参数
	 */
	protected List<Argument> arguments = new ArrayList<Argument>();
	
	/**
	 * 获取命令行参数列表
	 * @return 参数列表
	 */
	public List<Argument> getArguments(){return arguments;}
	
	/**
	 * 从XML节点中装入配置
	 */
	public void configure(Element _e, Properties _properties)
			throws BaseException {
		Properties p = new XmlElementProperties(_e,_properties);
		
		id = PropertiesConstants.getString(p, "id", "");
		note = PropertiesConstants.getString(p, "note", "");
		module = PropertiesConstants.getString(p, "process", DefaultProcess.class.getName());
		
		NodeList _arguments = XmlTools.getNodeListByPath(_e, "argument");
		if (_arguments != null && _arguments.getLength() > 0){
			for (int i = 0 ;i < _arguments.getLength() ; i ++){
				Node n = _arguments.item(i);
				
				if (Node.ELEMENT_NODE != n.getNodeType()){
					continue;
				}
				
				Element _argument = (Element) n;
				
				Argument argu = new Argument();
				argu.configure(_argument, p);
				
				if (argu.isOK()){
					arguments.add(argu);
				}
			}
		}
	}

	public void printHelp(PrintStream ps) {
		ps.println("Command\t:" + getId() + "\t" + getNote());
		
		if (arguments != null && arguments.size() > 0){
			ps.println("\t|Arguments are listed below:");
			
			for (Argument argu:arguments){
				argu.printHelp(ps);
			}
		}
	}
}
