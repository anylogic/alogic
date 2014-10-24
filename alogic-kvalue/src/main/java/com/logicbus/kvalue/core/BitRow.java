package com.logicbus.kvalue.core;



/**
 * 可进行位操作的Row
 * 
 * <br>
 * 
 * Bit操作对于一些特定类型的计算非常有效。
 * <br>
 * 假设现在我们希望记录自己网站上的用户的上线频率，比如说，计算用户 A 上线了多少天，用户 B 上线了多少天，
 * 诸如此类，以此作为数据，从而决定让哪些用户参加 beta 测试等活动 —— 这个模式可以使用 SETBIT 和 BITCOUNT 来实现。
 * <br>
 * 比如说，每当用户在某一天上线的时候，我们就使用 SETBIT ，以用户名作为 key ，将那天所代表的网站的上线日作为 offset 参数，
 * 并将这个 offset 上的为设置为 1 。
 * <br>
 * 举个例子，如果今天是网站上线的第 100 天，而用户 peter 在今天阅览过网站，那么执行命令 SETBIT peter 100 1 ；如果明天 
 * peter 也继续阅览网站，那么执行命令 SETBIT peter 101 1 ，以此类推。
 * <br>
 * 
 * 当要计算 peter 总共以来的上线次数时，就使用 BITCOUNT 命令：执行 BITCOUNT peter ，得出的结果就是 peter 上线的总天数。
 * </p>
 * 
 * @author duanyy
 *
 */
public interface BitRow extends KeyValueRow{
	/**
	 * 在指定的位置覆盖bit值
	 * @param offset
	 * @param value
	 */
	public boolean setBit(final long offset,final boolean value);	

	/**
	 * 获取指定位置的bit值
	 * @param offset
	 * @return
	 */
	public boolean getBit(final long offset);
	
	/**
	 * 计算指定范围内bit位为1的个数
	 * @param start
	 * @param end
	 * @return
	 */
	public long bitCount(final long start,final long end);
}
