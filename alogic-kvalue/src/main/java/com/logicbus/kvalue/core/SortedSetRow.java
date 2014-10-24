package com.logicbus.kvalue.core;

import java.util.List;
import java.util.Map;

import com.anysoft.util.Pair;

/**
 * 基于SortedSet的Row
 * 
 * @author duanyy
 *
 */
public interface SortedSetRow extends KeyValueRow {
	
	/**
	 * 向SortedSet中增加元素
	 * 
	 * @param member
	 * @param score
	 */
	public boolean add(String member,double score);
	
	/**
	 * 向SortedSet中增加元素
	 * @param element
	 */
	public boolean add(Pair<String,Double> element);
	
	/**
	 * 向SortedSet中增加多个元素
	 * @param elements
	 */
	public long add(Map<String,Double> elements);
	
	/**
	 * 获取SortedSet大小
	 * @return
	 */
	public long size();
	
	/**
	 * 对指定元素的score值进行增减
	 * @param member
	 * @param score
	 * @return
	 */
	public double incr(final String member,final double score);
	
	/**
	 * 获取指定元素的Score值
	 * @param member
	 * @return
	 */
	public double score(final String member);
	
	/**
	 * 获取指定元素的排名(Rank)
	 * @param member
	 * @return
	 */
	public long rank(final String member,boolean reverse);
	
	/**
	 * 统计Score值在min和max之间的元素个数
	 * @param min
	 * @param max
	 * @return
	 */
	public long count(double min,double max);
	
	/**
	 * 移除一个或多个指定的元素
	 * @param members
	 * @return
	 */
	public long remove(final String...members);
	
	/**
	 * 移除指定排名范围(from start to stop)的元素
	 * @param start
	 * @param stop
	 * @return
	 */
	public long remove(final long start,final long stop);
	
	/**
	 * 移除指定Score范围(from min to max)的元素
	 * @param min
	 * @param max
	 * @return
	 */
	public long remove(final double min,final double max);
	
	/**
	 * 获取指定Score区间(from min to max)的元素列表(不带Score)
	 * @param min
	 * @param max
	 * @param reverse
	 * @return
	 */
	public List<String> rangeByScore(final double min,final double max,boolean reverse);
	
	/**
	 * 获取指定Score区间(from min to max)的元素列表(带Score)
	 * @param min
	 * @param max
	 * @param reverse
	 * @return
	 */
	public List<Pair<String,Double>> rangeByScoreWithScores(
			final double min,final double max,boolean reverse);
	
	/**
	 * 获取指定排名范围（from start to stop）的元素列表(不带Score)
	 * @param start
	 * @param stop
	 * @param reverse 是否逆序(从大到小)
	 * @return
	 */
	public List<String> range(final long start,final long stop,boolean reverse);
	
	/**
	 * 获取指定排名范围（from start to stop）的元素列表(带Score)
	 * @param start
	 * @param stop
	 * @param reverse
	 * @return
	 */
	public List<Pair<String,Double>> rangeWithScores(final long start,final long stop,boolean reverse);
}
