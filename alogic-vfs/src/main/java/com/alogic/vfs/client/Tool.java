package com.alogic.vfs.client;

import java.util.Map;

import com.anysoft.util.Configurable;
import com.anysoft.util.XMLConfigurable;

/**
 * 工具
 * 
 * @author duanyy
 * @version 1.6.7.14 [20170210 duanyy] <br>
 * - 增加{@link #addSource(Directory)}方法，以便支持多来源目录 <br>
 * 
 * @version 1.6.10.6 [20171114 duanyy] <br>
 * - 比较和同步增加路径的白名单和黑名单功能 <br>
 * 
 */
public interface Tool extends Configurable,XMLConfigurable{	
	/**
	 * 设置源目录
	 * @param src 源目录
	 */
	public void setSource(Directory src);
	
	/**
	 * 增加扩展的源目录
	 * @param src 源目录
	 */
	public void addSource(Directory src);
	
	/**
	 * 设置黑名单路径
	 * @param paths 路径集，分号(;)分隔
	 */
	public void setBlacklist(String paths);
	
	/**
	 * 设置白名单路径
	 * @param paths 路径集，分号(;)分隔
	 */
	public void setWhitelist(String paths);
	
	/**
	 * 获取源目录
	 * @return 源目录实例
	 */
	public Directory getSource();
	
	/**
	 * 设置目的目录
	 * @param dest 目的目录
	 */
	public void setDestination(Directory dest);
	
	/**
	 * 获取目的目录
	 * @return 目的目录实例
	 */
	public Directory getDestination();

	/**
	 * 比较源目录和目的目录的差异性
	 * 
	 * @param watcher 监听器
	 */
	public void compare(Watcher watcher);
	
	/**
	 * 从源目录向目的目录同步
	 * 
	 * @param watcher 监听器
	 */
	public void sync(Watcher watcher);
	
	/**
	 *  处理结果
	 * @author duanyy
	 *
	 */
	public enum Result {
		Same("=="),
		Differ("!="),
		More(">="),
		Less("<="),
		New(">>"),
		Overwrite(">!"),
		Del(">-"),
		Keep("<>"),
		Failed("!!");
		
		private String sign;
		
		private Result(String code){
			sign = code;
		}
		
		public String sign(){
			return sign;
		}
	}	
	
	/**
	 * 监听器
	 * @author duanyy
	 *
	 */
	public static interface Watcher {
		
		/**
		 * 开始在源目录和目的目录之间进行操作
		 * @param src 源目录
		 * @param dest 目的目录
		 */
		public void begin(Directory src,Directory dest);
		
		/**
		 * 操作进度事件
		 * @param src 来源目录
		 * @param dest 目的目录
		 * @param fileInfo 文件信息
		 * @param result 操作结果
		 * @param progress 进度
		 */
		public void progress(
				Directory src,Directory dest,
				FileInfo fileInfo,Result result,float progress);
		
		/**
		 * 操作进度事件
		 * @param src 来源目录
		 * @param dest 目的目录
		 * @param message 消息
		 * @param level 消息等级
		 * @param progress 进度
		 */
		public void progress(
				Directory src,Directory dest,
				String message,String level,float progress);
		
		/**
		 * 结束操作
		 * @param src 源目录
		 * @param dest 目的目录
		 */
		public void end(Directory src,Directory dest);
	}

	/**
	 * 文件信息
	 * @author duanyy
	 *
	 */
	public static interface FileInfo{
		public String uPath();
		public String uParent();
		public Map<String,Object> srcAttrs();
		public Map<String,Object> destAttrs();
	}	
}
