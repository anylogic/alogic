package com.anysoft.tree;

/**
 * 树输出器
 * 
 * @author yyduan
 * @since 1.6.11.58 
 */
public interface TreeOutput<O> {
	
	/**
	 * 发现根节点
	 * @param id 根节点id
	 * @return 上下文
	 */
	public Object rootFound(String id);
	
	/**
	 * 发现树节点
	 * @param cookies 上下文
	 * @param node 节点定义
	 * @param depth 当前深度
	 * @return 上下文
	 */
	public Object nodeFound(Object cookies,TreeNode<O> node,int depth);
	
	
}
