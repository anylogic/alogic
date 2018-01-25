package com.anysoft.util;

/**
 * Pager,用于分页控制
 * @author yyduan
 *
 */
public class Pager {
	/**
	 * 本次查询的数据条数，作为查询的输入
	 */
	protected int limit = 50;

	/**
	 * 本次查询的起始位置，作为查询的输入
	 */
	protected int offset = 0;
	
	/**
	 * 本次查询的匹配关键字，作为查询的输入
	 */
	protected String keyword = "";
	
	/**
	 * 匹配本次查询keyword的总数据量，作为查询的输出（查询引擎不一定支持）
	 */
	protected long total = 0;
	
	/**
	 * 整个数据集的数据量，作为查询的输出（查询引擎不一定支持）
	 */
	protected long all = 0;
	
	public Pager(String keyword,int offset,int limit){
		this.keyword = keyword;
		this.offset = offset;
		this.limit = limit;
	}
	
	public Pager(int offset,int limit){
		this("",offset,limit);
	}
	
	public Pager(){
		this("",0,50);
	}
	
	public int getOffset(){
		return offset;
	}
	
	public int getLimit(){
		return limit;
	}
	
	public String getKeyword(){
		return keyword;
	}
	
	public long getTotal(){
		return total;
	}
	
	public long getAll(){
		return all;
	}
	
	public Pager setOffset(int offset){
		this.offset = offset;
		return this;
	}
	
	public Pager setLimit(int limit){
		this.limit = limit;
		return this;
	}
	
	public Pager setKeyword(final String keyword){
		this.keyword = keyword;
		return this;
	}
	
	public Pager setTotal(long total){
		this.total = total;
		return this;
	}
	
	public Pager setAll(long all){
		this.all = all;
		return this;
	}
}
