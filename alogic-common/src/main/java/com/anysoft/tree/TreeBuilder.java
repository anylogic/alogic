package com.anysoft.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * 树构建器
 * @author yyduan
 * @since 1.6.11.58 
 */
public interface TreeBuilder<O> {
	
	/**
	 * 增加树节点
	 * @param node 树节点
	 */
	public void addTreeNode(TreeNode<O> node);

	/**
	 * 构建树，并输出
	 * @param output 输出器
	 */
	public void build(String parentId,TreeOutput<O> output,int depth);
	
	/**
	 * 缺省实现
	 * @author yyduan
	 *
	 * @param <O>
	 */
	public static class Default<O> implements TreeBuilder<O>{
		protected List<TreeNode<O>> nodes = new ArrayList<TreeNode<O>>();
		
		public Default(){
			
		}
		
		@Override
		public void addTreeNode(TreeNode<O> node) {
			nodes.add(node);
		}

		@Override
		public void build(String parentId,TreeOutput<O> output,int depth) {
			if (output != null){
				Object cookies = output.rootFound(parentId);
				build(cookies,parentId,output,0,depth);
			}
		}
		
		protected void build(Object cookies,String parentId,TreeOutput<O> output,int current,int depth){
			if (current >= depth){
				return;
			}
			
			for (TreeNode<O> node:nodes){
				if (parentId.equals(node.getParentId())){
					Object childCookies = output.nodeFound(cookies, node, current);
					build(childCookies,node.getId(),output,current + 1,depth);
				}
			}
		}
	}
}
