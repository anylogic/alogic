package com.anysoft.batch;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anysoft.util.CommandLine;
import com.anysoft.util.Copyright;
import com.anysoft.util.DefaultProperties;
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.SystemProperties;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;

/**
 * 处理入口
 * 
 * @author duanyy
 *
 * @version 1.6.1.3 [20141119 duanyy] <br>
 * - 增加对统一资源的配置文件支持，通过conf.url来指定<br>
 * 
 * @version 1.6.1.4 [20141128 duanyy] <br>
 * - 在装入include文件时，通过loadable变量检测是否装入 <br>
 * 
 * @version 1.6.3.19 [20150420 duanyy] <br>
 * - 调整commands和includes处理次序，以便command参数能读取includes文件中的变量 <br>
 * 
 * @version 1.6.3.20 [20150421 duanyy] <br>
 * - 对于某些环境变量，设置到System的Properties中 <br>
 */
public class Main implements CommandHelper,Process{
	
	/**
	 * a logger of log4j
	 */
	protected static Logger logger = LogManager.getLogger(Main.class);
	
	/**
	 * a print stream to print help
	 */
	protected static PrintStream helpPS = System.out;
	/**
	 * 当前指令
	 */
	protected String command = CMD_HELP;
	
	/**
	 * 命令行的变量集
	 */
	protected DefaultProperties commandLine = null;
	
	/**
	 * 帮助主题
	 */
	protected String helpTopic = "all";
	
	/**
	 * Help指令
	 */
	public static final String CMD_HELP = "help";
	
	public void printHelp(PrintStream ps) {
		if (!helpTopic.equals("all")){
			Command cmd = commands.get(helpTopic);
			if (cmd != null){
				ps.println("Syntax:");
				ps.println("Main cmd=<command> [<var>=<value>]");
				cmd.printHelp(ps);
			}else{
				printRootHelp(ps);
			}
		}else{
			printRootHelp(ps);
		}
	}

	private void printRootHelp(PrintStream ps) {
		ps.println("Syntax:");
		ps.println("Main cmd=<command> [<var>=<value>]");
		ps.println("Commands supported are listed below:");
		
		Enumeration<Command> _commands = commands.elements();
		
		while (_commands.hasMoreElements()){
			Command command = _commands.nextElement();
			ps.println("\t-" + command.getId() + "\t:" + command.getNote());
		}
		
		ps.println("\t-help\t:Print this help");
	}

	/**
	 * 当前支持的command列表
	 */
	protected Hashtable<String,Command> commands = new Hashtable<String,Command>();
	
	/**
	 * 当前的资源工厂
	 */
	protected ResourceFactory resourceFactory = null;
	
	/**
	 * 装入配置信息
	 * @param p 环境变量集
	 * 
	 */
	protected void loadConfig(DefaultProperties p){
		//装入配置文件,从参数conf中读入，缺省为config.xml
		String filename = PropertiesConstants.getString(p,"conf","");
		if (filename != null && filename.length() > 0){
			File _conf = new File(filename);
			if (_conf.exists() && _conf.isFile()) {
				try {
					Document doc = XmlTools.loadFromFile(_conf);
					if (doc != null){
						loadConfigFromElement(p,doc.getDocumentElement());
					}
				} catch (Exception e) {
					logger.error("Can not load xml file,url = " + filename, e);
				}
			} else {
				logger.error("The config file is not a valid file,url = "
						+ filename);
			}
		}else{
			if (resourceFactory == null) {
				// 设置全局的ResourceFactory
				String rf = p.GetValue("resource.factory",
						"com.anysoft.util.resource.ResourceFactory");
				try {
					logger.info("Use resource factory:" + rf);
					resourceFactory = (ResourceFactory) Class.forName(rf).newInstance();
				} catch (Exception ex) {
					logger.error("Can not create instance of :" + rf);
				}
				if (resourceFactory == null) {
					resourceFactory = new ResourceFactory();
					logger.info("Use default:" + ResourceFactory.class.getName());
				}
			}
			
			filename = PropertiesConstants.getString(p,"conf.url","java:///config.xml");
			InputStream in = null;
			try {
				in = resourceFactory.load(filename, null);
				Document doc = XmlTools.loadFromInputStream(in);
				if (doc != null){
					loadConfigFromElement(p,doc.getDocumentElement());
				}
			}catch (Exception ex){
				logger.error("The config file is not a valid file,url = "
						+ filename);
			}finally{
				IOTools.close(in);
			}
		}
	}	

	protected void loadConfig(DefaultProperties p, String link) {
		if (resourceFactory == null) {
			// 设置全局的ResourceFactory
			String rf = p.GetValue("resource.factory",
					"com.anysoft.util.resource.ResourceFactory");
			try {
				logger.info("Use resource factory:" + rf);
				resourceFactory = (ResourceFactory) Class.forName(rf).newInstance();
			} catch (Exception ex) {
				logger.error("Can not create instance of :" + rf);
			}
			if (resourceFactory == null) {
				resourceFactory = new ResourceFactory();
				logger.info("Use default:" + ResourceFactory.class.getName());
			}
		}
		
		InputStream in = null;
		try {
			in = resourceFactory.load(link, null);
			Document doc = XmlTools.loadFromInputStream(in);	
			if (doc != null){
				loadConfigFromElement(p,doc.getDocumentElement());
			}
		}catch (Throwable ex){
			logger.error("Error occurs when load xml file,source=" + link, ex);
		}finally {
			IOTools.closeStream(in);
		}
	}	
	
	protected void loadConfigFromElement(DefaultProperties p,Element e) {
		//首先处理环境变量：settings/parameter
		NodeList _parameters = XmlTools.getNodeListByPath(e, "settings/parameter");
		if (_parameters != null && _parameters.getLength() > 0){
			
			for (int i = 0 ;i < _parameters.getLength() ; i ++){
				Node _n = _parameters.item(i);
				
				if (Node.ELEMENT_NODE != _n.getNodeType()){
					continue;
				}
				
				Element _parameter = (Element)_n;
				
				String id = _parameter.getAttribute("id");
				String value = _parameter.getAttribute("value");
				boolean system = e.getAttribute("system").equals("true")?true:false;
				if (system){
					if (id != null && value != null){
						System.setProperty(id, value);
					}
				}else{

					// 支持final标示,如果final为true,则不覆盖原有的取值
					boolean isFinal = e.getAttribute("final").equals("true") ? true
							: false;
					if (isFinal) {
						String oldValue = p.GetValue(id, "", false, true);
						if (oldValue == null || oldValue.length() <= 0) {
							p.SetValue(id, value);
						}
					} else {
						p.SetValue(id, value);
					}
				}
			}
		}
		
		//处理commands/command
		NodeList _commands = XmlTools.getNodeListByPath(e, "commands/command");
		if (_commands != null && _commands.getLength() > 0){
			
			for (int i = 0 ;i < _commands.getLength() ; i ++){
				Node _n = _commands.item(i);
				
				if (Node.ELEMENT_NODE != _n.getNodeType()){
					continue;
				}
				
				Element _command = (Element) _n;
				
				Command cmd = new Command();
				cmd.configure(_command, p);
				
				if (cmd.isOk()){
					commands.put(cmd.getId(), cmd);
				}
			}
		}
		
		//处理includes/include
		NodeList _includes = XmlTools.getNodeListByPath(e, "includes/include");
		if (_includes != null && _includes.getLength() > 0){
			
			for (int i = 0 ;i < _includes.getLength() ; i++){
				Node _n = _includes.item(i);
				
				if (Node.ELEMENT_NODE != _n.getNodeType()){
					continue;
				}
				
				Element _include = (Element)_n;
				
				String link = _include.getAttribute("link");
				if (link != null && link.length() > 0){
					String loadable = _include.getAttribute("loadable");
					if (loadable != null){
						String _loadable = p.transform(loadable);
						if (_loadable.length() > 0){
							loadConfig(p,p.transform(link));
						}else{
							logger.info("Find xml link file,but the loadable is null");
						}
					}else{
						loadConfig(p,p.transform(link));
					}
				}
			}
		}
	}

	public int init(DefaultProperties p) {
		commandLine = new DefaultProperties("default",p);
		//从配置文件中装入
		loadConfig(commandLine);
		command = PropertiesConstants.getString(p, "cmd", CMD_HELP);
		if (command.equals(CMD_HELP)) {
			helpTopic = PropertiesConstants.getString(p, "topic", "all");
		} else {
			boolean helpMode = PropertiesConstants.getBoolean(p, CMD_HELP,
					false);
			if (helpMode) {
				helpTopic = command;
				command = CMD_HELP;
			}
		}
		return 0;
	}

	public int run() {
		if (command.equals(CMD_HELP)){
			printHelp(helpPS);
			return -1;
		}
		Command cmd = commands.get(command);
		if (cmd == null){
			helpTopic = "all";
			helpPS.println("Please give me a valid command...");
			printHelp(helpPS);
			return -1;
		}
		
		//创建Process对象
		String module = cmd.getModule();
		Process process = null;
		try {
			Factory<Process> factory = new Factory<Process>();
			process = factory.newInstance(module);
		}catch (Exception ex){
			logger.error("Can not create process instance,module=" +module,ex);
			return -1;
		}
		
		DefaultProperties p = commandLine;
		{
			//检查参数
			List<Argument> arguments = cmd.getArguments();
			
			for (Argument argu:arguments){
				String id = argu.getId();
				String value = argu.getValue(p);
				
				if (value == null || value.length() <= 0){
					if (!argu.isNullable()){
						helpTopic = command;
						helpPS.println("Can not find argument named " + id );
						printHelp(helpPS);
						return -1;
					}
				}else{
					p.SetValue(id, value);
				}
			}
		}
		
		int result = 0;
		result = process.init(p);
		if  (result == 0){
			result = process.run();
		}
		return result;
	}

	public static void main(String[] args) {
		Copyright.bless(logger, "\t\t");
		int result = 0;
		try {
			CommandLine cmdLine = new CommandLine(args,new SystemProperties());		
			Main main = new Main();
			
			result = main.init(cmdLine);
			if (result == 0){
				result = main.run();
			}
		}catch (Exception ex){
			logger.error(ex.getMessage(), ex);
			result = -1;
		}
		System.exit(result);
	}	
}
