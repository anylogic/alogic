package com.alogic.terminal;

import java.util.ArrayList;
import java.util.List;

/**
 * Shell指令
 * 
 * @author duanyy
 * @since 1.1.10.10
 */
public interface Command extends Resolver{
	
	/**
	 * 获取指令
	 * @return 指令
	 */
	public String[] getCommands();
	
	/**
	 * 获取指令
	 * 
	 * 多条指令以回车分隔
	 * @return 指令
	 */
	public String getCommand();
	
	/**
	 * 是否按照Shell方式执行
	 * @return true|false
	 */
	boolean isShellMode();
	
	/**
	 * 简单指令
	 * 
	 * @author duanyy
	 *
	 */
	public class Simple implements Command{
		protected Resolver resolver = null;
		protected List<String> commands = new ArrayList<String>();
		protected boolean shellMode = true;
		
		public Simple(Resolver r,boolean shell,String...cmds){
			resolver = r;
			shellMode = shell;
			
			for (String c:cmds){
				commands.add(c);
			}
		}

		public Simple(Resolver r,String...cmds){
			this(r,true,cmds);
		}
		
		public Simple(String...cmds){
			this(new Resolver.Default(),cmds);
		}
		
		public Simple(boolean shell,String ...cmds){
			this(new Resolver.Default(),shell,cmds);
		}
		
		@Override
		public Object resolveBegin(String cmd) {
			if (resolver != null){
				return resolver.resolveBegin(cmd);
			}else{
				return null;
			}
		}

		@Override
		public void resolveLine(Object cookies, String content) {
			if (resolver != null){
				resolver.resolveLine(cookies,content);
			}
		}

		@Override
		public void resolveEnd(Object cookies) {
			if (resolver != null){
				resolver.resolveEnd(cookies);
			}
		}
		
		@Override
		public String[] getCommands() {
			return commands.toArray(new String[0]);
		}

		@Override
		public boolean isShellMode() {
			return shellMode;
		}

		@Override
		public String getCommand() {
			StringBuffer command = new StringBuffer();
			int size = commands.size();
			for (int i = 0;i < size ; i ++){
				String cmd = commands.get(i);
				command.append(cmd);
				if (i < size - 1){
					command.append("\n");
				}
			}
			return command.toString();
		}
	}
}
