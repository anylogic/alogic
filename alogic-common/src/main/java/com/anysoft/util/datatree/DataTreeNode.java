package com.anysoft.util.datatree;

public class DataTreeNode<object> {
	protected object content;
	
	public DataTreeNode<object> brother = null;
	public DataTreeNode<object> child = null;
	
	public DataTreeNode(object _content){
		content = _content;
	}
	
	public String toString(){
		return content.toString();
	}
	
	public object getContent(){
		return content;
	}
}
