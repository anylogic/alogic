package com.anysoft.util.datatree;


public class DataTree<object> implements DataTreeBuilder<object> {
	protected DataTreeNode<object> root;
	
	public DataTree(){
		root = null;
	}
	
	public void scan(DataTreeBuilder<object> scanner){
		if (scanner == null){
			return ;
		}
		
		if (root == null){
			//是一个空树
			return ;
		}
		
		Object cookies = scanner.rootFound(root.getContent());
		if (root.child != null){
			scan(cookies,root.child,scanner);
		}
	}
	
	protected void scan(Object cookies,DataTreeNode<object> node,DataTreeBuilder<object> scanner){
		DataTreeNode<object> temp = node;
		
		while (temp != null){
			Object newCookies = scanner.nodeFound(cookies, temp.getContent());
			if (temp.child != null){
				scan(newCookies,temp.child,scanner);
			}
			temp = temp.brother;
		}
	}

	public Object nodeFound(Object _cookies, object _content) {
		if (_cookies == null) {
			// 插入的是根节点
			root = new DataTreeNode<object>(_content);
			return root;
		}
		DataTreeNode<object> newNode = new DataTreeNode<object>(_content);
		@SuppressWarnings("unchecked")
		DataTreeNode<object> parentNode = (DataTreeNode<object>)_cookies;
		if (parentNode.child == null) {
			parentNode.child = newNode;
			return newNode;
		}

		DataTreeNode<object> temp = parentNode.child;
		while (temp.brother != null) {
			temp = temp.brother;
		}
		temp.brother = newNode;

		return newNode;
	}

	public Object rootFound(object _root) {
		return nodeFound(null,_root);
	}
}

