package com.alogic.cube.mdr;

import com.anysoft.util.Properties;

/**
 * 可累加
 * @author yyduan
 * @since 1.6.11.35
 */
public interface Summarable {
	
	/**
	 * 收集数据，进行加法计算
	 * @param provider 数据提供者
	 */	
	public void sum(final Properties provider);
}
