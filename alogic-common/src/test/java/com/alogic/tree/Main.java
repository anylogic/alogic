package com.alogic.tree;

import com.anysoft.tree.TreeBuilder;
import com.anysoft.tree.TreeNode;
import com.anysoft.tree.TreeOutput;

public class Main implements TreeOutput<String>{


	@Override
	public Object rootFound(String id) {
		System.out.println(id);
		return new Integer(4);
	}

	@Override
	public Object nodeFound(Object cookies, TreeNode<String> node, int depth) {
		Integer step = (Integer) cookies;
		System.out.println(String.format("%s--->%s", node.getParentId(),node.getId()));
		return step + 4;
	}

	public static void main(String[] args) {
		Main main = new Main();
		
		TreeBuilder<String> builder = new TreeBuilder.Default<String>();
		
		builder.addTreeNode(new TreeNode.Default<String>("100", "0","aaa"));
		builder.addTreeNode(new TreeNode.Default<String>("101", "0","aaa"));
		builder.addTreeNode(new TreeNode.Default<String>("102", "0","aaa"));
		
		builder.addTreeNode(new TreeNode.Default<String>("1000", "100","aaa"));
		builder.addTreeNode(new TreeNode.Default<String>("1001", "100","aaa"));
		
		builder.addTreeNode(new TreeNode.Default<String>("1020", "102","aaa"));
		builder.addTreeNode(new TreeNode.Default<String>("1021", "102","aaa"));
		
		builder.build("0", main, 3);
		builder.build("0", main, 1);
		builder.build("100", main, 1);
	}
}
