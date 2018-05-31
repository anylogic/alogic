
package com.alogic.ik.configuration;

import java.util.List;

/**
 * 字典配置
 * 
 * @author yyduan
 * @since 1.6.11.32
 */
public interface DictionaryConfiguration {
	
    List<char[]> getMainDictionary();

    List<char[]> getStopWordDictionary();

    List<char[]> getQuantifierDictionary();
}
