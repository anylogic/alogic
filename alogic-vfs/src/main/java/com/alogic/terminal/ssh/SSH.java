package com.alogic.terminal.ssh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.apache.commons.lang3.StringUtils;

import com.alogic.terminal.Command;
import com.alogic.terminal.Resolver;
import com.alogic.terminal.Terminal;
import com.anysoft.util.BaseException;
import com.anysoft.util.DefaultProperties;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.code.Coder;
import com.anysoft.util.code.CoderFactory;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.HTTPProxyData;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;


/**
 * 基于ganymed的Shell实现
 * 
 * @author duanyy
 * @since 1.1.10.10
 * 
 * @version 1.6.7.11 [20170203 duanyy] <br>
 * - 支持采用coder加密后的密码 <br>
 */
public class SSH extends Terminal.Abstract{

	/**
	 * 用户名
	 */
	protected String username;
	
	/**
	 * 密码
	 */
	protected String password;
	
	/**
	 * 密码编码器
	 */
	protected String coder = "DES3";
	
	/**
	 * 端口
	 */
	protected int port = 22;
	
	/**
	 * 主机IP
	 */
	protected String host;
	
	/**
	 * 是否启用代理
	 */
	protected boolean proxyEnable = false;

	/**
	 * 代理的用户名
	 */
	protected String proxyUser;
	
	/**
	 * 代理的密码
	 */
	protected String proxyPwd;
	
	/**
	 * 代理主机ip
	 */
	protected String proxyHost;
	
	/**
	 * 代理主机端口
	 */
	protected int proxyPort = 80;
	
	/**
	 * ssh连接
	 */
	protected Connection conn = null;
	
	/**
	 * 超时
	 */
	protected int timeout = 5000;
	
	protected String encoding = "utf-8";

	
	@Override
	public int exec(Resolver resolver,String... cmd) {
		Command simple = new Command.Simple(resolver,cmd);
		return exec(simple);
	}

	@Override
	public int exec(Command command) {
		if (command.isShellMode()){
			return execShell(command);
		}
		Session session = null;
		try {
			// 创建会话
			session = conn.openSession();

			String cmd = command.getCommand();

			// 设置终端
			session.requestPTY("vt100");
			
			//执行指令
			session.execCommand(cmd);
			// 等待数据
			session.waitForCondition(ChannelCondition.STDOUT_DATA | ChannelCondition.STDERR_DATA, 5000);

			// 解析输出数据
			resolveResult(command, cmd, session.getStdout());

			// 等待结束信号
			session.waitForCondition(ChannelCondition.EXIT_STATUS | ChannelCondition.EXIT_SIGNAL, 5000);
			return session.getExitStatus();
		} catch (IOException e) {
			throw new BaseException("core.ssh_session", "Error occurs", e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	protected int execShell(final Command command) {
		Session session = null;
		try {
			// 创建会话
			session = conn.openSession();
			// 设置终端
			session.requestPTY("bash");
			session.startShell();


			String[] cmds = command.getCommands();

			for (final String cmd:cmds){
				PrintWriter writer = new PrintWriter(session.getStdin());
				writer.println(cmd);
				writer.close();
				
				//等待数据
				int condition = session.waitForCondition(ChannelCondition.STDOUT_DATA | ChannelCondition.STDERR_DATA, timeout);
				if ((condition & ChannelCondition.TIMEOUT) != 0){
					//没有数据
					break;
				}
				
				final InputStream in = session.getStdout();
				final InputStream err = session.getStderr();
				
				Thread stdout = new Thread(){
					public void run(){
						try {
							resolveResult(command, cmd, in);
						} catch (IOException e) {
						}
					}
				};

				Thread stderr = new Thread(){
					public void run(){
						try {
							resolveResult(command, cmd, err);
						} catch (IOException e) {
						}
					}
				};				
				
				stdout.start();
				stderr.start();
				
				//等待5秒
				session.waitForCondition(ChannelCondition.EXIT_STATUS | ChannelCondition.EXIT_SIGNAL | ChannelCondition.EOF | ChannelCondition.CLOSED, timeout);
				stdout.interrupt();
				stderr.interrupt();
			}
			return 0;
		} catch (IOException e) {
			throw new BaseException("core.ssh_session", "Error occurs", e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	protected void resolveResult(Command command,String cmd, InputStream in) throws IOException{
		BufferedReader br = null;
		try {
			InputStream stdout = new StreamGobbler(in);   
			
	        br = new BufferedReader(new InputStreamReader(stdout,encoding));  
	        
	        Object cookies = command.resolveBegin(cmd);

	        while (true)  
	        {  
	            String line = br.readLine();  
	            if (line == null)  
	                break;  
	            command.resolveLine(cookies, line);
	        } 
	        
	        command.resolveEnd(cookies);
		}finally{
			IOTools.close(br);
		}
	}

	@Override
	public void configure(Properties p) {
		username = PropertiesConstants.getString(p,"username",username,true);
		password = PropertiesConstants.getString(p,"password",password,true);
		host = PropertiesConstants.getString(p,"host",host,true);
		port = PropertiesConstants.getInt(p,"port",port,true);
		coder = PropertiesConstants.getString(p, "coder", coder);
		proxyEnable = PropertiesConstants.getBoolean(p, "proxy.enable", proxyEnable,true);
		
		proxyUser = PropertiesConstants.getString(p,"proxy.user",null,true);
		proxyPwd = PropertiesConstants.getString(p,"proxy.pwd",null,true);
		
		proxyHost = PropertiesConstants.getString(p,"proxy.host",null,true);
		proxyPort = PropertiesConstants.getInt(p,"proxy.port",port,true);	
		
		timeout = PropertiesConstants.getInt(p,"timeout",timeout,true);
		
		encoding = PropertiesConstants.getString(p,"encoding",encoding,true);
		
		if (StringUtils.isEmpty(proxyHost)){
			proxyEnable = false;
		}
	}

	@Override
	public void close() throws Exception {
		disconnect();
	}

	@Override
	public void connect() {
		if (proxyEnable){
			conn = new Connection(host,port,new HTTPProxyData(proxyHost,proxyPort,proxyUser,proxyPwd));
		}else{
			conn = new Connection(host,port);
		}
		
		try {
			conn.connect();
			
			String pwd = password;
			if (StringUtils.isNotEmpty(coder)) {
				// 通过coder进行密码解密
				try {
					Coder c = CoderFactory.newCoder(coder);
					pwd = c.decode(password, username);
				} catch (Exception ex) {
					LOG.error(String.format("Can not encrypt password:%s with %s",password,coder));
				}
			}			
            boolean isAuthenticated = conn.authenticateWithPassword(username, pwd);  
            
            if (!isAuthenticated){ 
            	throw new BaseException("core.ssh_auth","Authentication failed.");			
            }   
		} catch (IOException e) {
			throw new BaseException("core.ssh_connect","Can not connect to ssh server",e);
		}
	}

	@Override
	public void disconnect() {
		if (conn != null){
			conn.close();
			conn = null;
		}
	}

	public static void main(String[] args){
		Properties p = new DefaultProperties();
		p.SetValue("host", "10.128.91.41");
		p.SetValue("user", "alogic");
		p.SetValue("pwd", "shit1234_");
		
		Terminal shell = new SSH();
		shell.configure(p);
		
		try {
			shell.connect();
			System.out.println(shell.exec(new Command.Simple("ketty.sh start app=demo")));
			shell.disconnect();
		}finally{
			IOTools.close(shell);
		}
	}
}
