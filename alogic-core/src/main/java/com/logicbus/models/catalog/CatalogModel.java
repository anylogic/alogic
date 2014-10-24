package com.logicbus.models.catalog;

/**
 * Catalog接口模型
 * @author hmyyduan
 *
 */
public interface CatalogModel {
	/**
	 * 获取根节点
	 * @return 根节点
	 */
	public  CatalogNode getRoot();
	
	/**
	 * 获取指定节点的子节点
	 * @return 子节点列表
	 */
	public CatalogNode [] getChildren(CatalogNode parent);
	
	/**
	 * 通过路径来获取指定节点的子节点
	 * @param parent 父节点
	 * @param path 路径
	 * @return 子节点
	 */
	public CatalogNode getChildByPath(CatalogNode parent,Path path);
}
