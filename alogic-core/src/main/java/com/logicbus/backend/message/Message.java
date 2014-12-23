package com.logicbus.backend.message;

/**
 * 消息
 * @author duanyy
 * @version 1.0.4 [20140410 duanyy] <br>
 * - 增加encoding成员
 * 
 * @version 1.0.5 [20140412 duanyy] <br>
 * - 修改消息传递模型。<br>
 * 
 * @version 1.4.0 [20141117 duanyy] <br>
 * - 改造为接口 <br>
 * 
 * @version 1.6.1.1 [20141118 duanyy] <br>
 * - 简化Message接口 <br>
 * 
 * @version 1.6.2.1 [20141223 duanyy] <br>
 * - 增加对Comet的支持 <br>
 */
public interface Message {
	/**
	 * 初始化
	 * @param ctx 上下文
	 */
	public void init(MessageDoc ctx);
	
	/**
	 * 完成
	 * @param ctx 上下文身上
	 * @param closeStream 是否关闭链接
	 */
	public void finish(MessageDoc ctx,boolean closeStream);
}
