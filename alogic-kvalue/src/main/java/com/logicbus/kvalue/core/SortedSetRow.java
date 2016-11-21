package com.logicbus.kvalue.core;

import java.util.List;
import java.util.Map;

import com.anysoft.util.Pair;

/**
 * 基于SortedSet的Row
 * 
 * @author duanyy
 *
 * @version 1.6.6.2 [20160826 duanyy] <br>
 * - rangeByScore和rangeByScoreWithScores增加分页接口 <br>
 * 
 * @version 1.6.6.6 [20161121 duanyy] <br>
 * - 增加对redis指令ZRANGEBYLEX,ZLEXCOUNT,ZREMRANGEBYLEX的支持，该指令支持通过字典区间来操作zset类型的数据，适合redis-2.8.9版本 <br>
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
	 * 获取指定Score区间(from min to max)的元素列表(不带Score)
	 * @param min
	 * @param max
	 * @param reverse
	 * @param offset 偏移，用于分页查询
	 * @param cnt 本次查询数据个数，用于分页查询
	 * @return 元素列表
	 * 
	 * @since 1.6.6.2
	 */
	public List<String> rangeByScore(final double min,final double max,boolean reverse,final long offset,final long cnt);	
	
	/**
	 * 获取指定Score区间(from min to max)的元素列表(带Score)
	 * @param min
	 * @param max
	 * @param reverse
	 * @return 元素列表
	 */
	public List<Pair<String,Double>> rangeByScoreWithScores(
			final double min,final double max,boolean reverse);
	
	/**
	 * 获取指定Score区间(from min to max)的元素列表(带Score)
	 * @param min
	 * @param max
	 * @param reverse
	 * @param offset 偏移，用于分页查询
	 * @param cnt 本次查询数据个数，用于分页查询
	 * @return 元素列表
	 * 
	 * @since 1.6.6.2
	 */
	public List<Pair<String,Double>> rangeByScoreWithScores(
			final double min,final double max,boolean reverse,final long offset,final long cnt);	
	
	/**
	 * 获取指定排名范围（from start to stop）的元素列表(不带Score)
	 * @param start
	 * @param stop
	 * @param reverse 是否逆序(从大到小)
	 * @return 元素列表
	 */
	public List<String> range(final long start,final long stop,boolean reverse);
	
	/**
	 * 获取指定排名范围（from start to stop）的元素列表(带Score)
	 * @param start
	 * @param stop
	 * @param reverse
	 * @return 元素列表
	 */
	public List<Pair<String,Double>> rangeWithScores(final long start,final long stop,boolean reverse);
	
	/**
	 * 获取指定字典区间(from min to max)的元素列表
	 * @param min min lex
	 * @param max max lex
	 * @param offset 偏移，用于分页查询
	 * @param cnt 本次查询数据个数，用于分页查询
	 * @return 元素列表
	 */
	public List<String> rangeByLex(final String min,final String max,final long offset,final long cnt);
	
	/**
	 * 删除指定字典区间(from min to max)的元素
	 * @param min min lex
	 * @param max max lex
	 * @return 删除元素的个数
	 */
	public long removeByLex(final String min,final String max);
	
	/**
	 * 计算有序集合中指定字典区间内成员数量
	 * @param min min lex
	 * @param max max lex
	 * @return 成员数量
	 */
	public long countByLex(final String min,final String max);
}
