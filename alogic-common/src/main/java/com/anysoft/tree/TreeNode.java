package com.anysoft.tree;

/**
 * 树的节点
 * @author yyduan
 * @since 1.6.11.58 
 */
public interface TreeNode<O> {
	/**
	 * 获取当前节点id
	 * @return 节点id
	 */
	public String getId();
	
	/**
	 * 获取父节点id
	 * @return 父节点id
	 */
	public String getParentId();
	
	/**
	 * 获取节点数据
	 * @return 节点数据对象
	 */
	public O getData();
	
	/**
	 * 缺省实现
	 * 
	 * @author yyduan
	 *
	 * @param <O>
	 */
	public static class Default<O> implements TreeNode<O>{
		protected String id;
		protected String parentId = "0";
		protected O data;
		
		public Default(String id,String parentId,O data){
			this.id = id;
			this.parentId = parentId;
			this.data = data;
		}
		
		public Default(String id,O data){
			this(id,"0",data);
		}
		
		@Override
		public String getId() {
			return id;
		}

		@Override
		public String getParentId() {
			return parentId;
		}

		@Override
		public O getData() {
			return data;
		}
		
	}
}
