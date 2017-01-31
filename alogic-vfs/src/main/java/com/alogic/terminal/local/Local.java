package com.alogic.terminal.local;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.alogic.terminal.Command;
import com.alogic.terminal.Resolver;
import com.alogic.terminal.Terminal;
import com.anysoft.util.BaseException;
import com.anysoft.util.DefaultProperties;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 本地ProcessBuilder实现
 * 
 * @author duanyy
 *
 */
public class Local extends Terminal.Abstract{
	protected String encoding = "gbk";

	@Override
	public int exec(Resolver resolver,String... cmd) {
		Command simple = new Command.Simple(resolver,cmd);
		return exec(simple);
	}

	@Override
	public int exec(Command command) {
		String[] cmds = command.getCommands();
		int ret = 0;
		for (String cmd:cmds){
			try {
				cmd = cmd.startsWith("cmd /c")?cmd:"cmd /c " + cmd;
				ProcessBuilder pb = new ProcessBuilder(cmd.split(" "));
				
				pb.redirectErrorStream(true);
				Process p = pb.start();
				resolveResult(command,cmd,p.getInputStream());
				
				ret = p.waitFor();
			} catch (IOException e) {
				throw new BaseException("core.process_error",String.format("Command[%s] execute error:%s",cmd,e.getMessage()));
			} catch (InterruptedException e) {
				throw new BaseException("core.process_error",String.format("Command[%s] execute error:%s",cmd,e.getMessage()));
			}
		}
		return ret;
	}
	
	protected void resolveResult(Command command,String cmd, InputStream in) throws IOException{
		BufferedReader br = null;
		try {
	        br = new BufferedReader(new InputStreamReader(in,encoding));  
	        
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
		encoding = PropertiesConstants.getString(p,"encoding",encoding,true);
	}

	@Override
	public void close() throws Exception {
		disconnect();
	}

	@Override
	public void connect() {
		// nothing to do
	}

	@Override
	public void disconnect() {
		// nothing to do
	}

	public static void main(String[] args){
		Properties p = new DefaultProperties();

		Terminal shell = new Local();
		shell.configure(p);
		
		try {
			shell.connect();
			System.out.println(shell.exec(new Command.Simple("java")));
			shell.disconnect();
		}finally{
			IOTools.close(shell);
		}
	}
}