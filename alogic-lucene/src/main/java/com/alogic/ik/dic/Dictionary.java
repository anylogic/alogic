/**
 * IK 中文分词  版本 5.0
 * IK Analyzer release 5.0
 * 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * 源代码由林良益(linliangyi2005@gmail.com)提供
 * 版权声明 2012，乌龙茶工作室
 * provided by Linliangyi and copyright 2012 by Oolong studio
 * 
 * 
 */
package com.alogic.ik.dic;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alogic.ik.configuration.DictionaryConfiguration;

/**
 * 词典管理类
 * 
 * @since 1.6.11.32
 * 
 * @version 1.6.11.34 [20180606 duanyy] <br>
 * - 增加单个关键在加入的接口 <br>
 */
public class Dictionary {

	private DictSegment _MainDict;
	
	/*
	 * 停止词词典 
	 */
	private DictSegment _StopWordDict;
	/*
	 * 量词词典
	 */
	private DictSegment _QuantifierDict;
	
	public Dictionary(){
        _MainDict = new DictSegment((char) 0);
		_StopWordDict = new DictSegment((char)0);        
		_QuantifierDict = new DictSegment((char)0);		
	}
	
	public void addConfiguration(DictionaryConfiguration cfg){
		this.loadMainDict(cfg);
		this.loadStopWordDict(cfg);
		this.loadQuantifierDict(cfg);		
	}
	
	public void addWord(String word){
		if (StringUtils.isNotEmpty(word)){
			_MainDict.fillSegment(word.trim().toLowerCase().toCharArray());
		}
	}
	
	public void disableWord(String word){
		if (StringUtils.isNotEmpty(word)){
			_MainDict.disableSegment(word.trim().toLowerCase().toCharArray());
		}		
	}
	
	public void addWords(Collection<String> words){
		if(words != null){
			for(String word : words){
				if (word != null) {
					//批量加载词条到主内存词典中
					_MainDict.fillSegment(word.trim().toLowerCase().toCharArray());
				}
			}
		}
	}
	
	public void disableWords(Collection<String> words){
		if(words != null){
			for(String word : words){
				if (word != null) {
					//批量屏蔽词条
					_MainDict.disableSegment(word.trim().toLowerCase().toCharArray());
				}
			}
		}
	}
	
	public Hit matchInMainDict(char[] charArray){
		return _MainDict.match(charArray);
	}
	
	public Hit matchInMainDict(char[] charArray , int begin, int length){
		return _MainDict.match(charArray, begin, length);
	}
	
	public Hit matchInQuantifierDict(char[] charArray , int begin, int length){
		return _QuantifierDict.match(charArray, begin, length);
	}
	
	public Hit matchWithHit(char[] charArray , int currentIndex , Hit matchedHit){
		DictSegment ds = matchedHit.getMatchedDictSegment();
		return ds.match(charArray, currentIndex, 1 , matchedHit);
	}
	
	public boolean isStopWord(char[] charArray , int begin, int length){
		return _StopWordDict.match(charArray, begin, length).isMatch();
	}	
	
	/**
	 * 加载主词典及扩展词典
	 */
	private void loadMainDict(DictionaryConfiguration cfg) {
		List<char[]> list = cfg.getMainDictionary();
		
		if (list != null){
	        for (char[] segment : list) {
	            _MainDict.fillSegment(segment);
	        }
		}
    }

    /**
	 * 加载用户扩展的停止词词典
	 */
	private void loadStopWordDict(DictionaryConfiguration cfg){
		List<char[]> list = cfg.getStopWordDictionary();
		if (list != null){
	        for (char[] segment : list) {
	            _StopWordDict.fillSegment(segment);
	        }
		}
	}
	
	/**
	 * 加载量词词典
	 */
	private void loadQuantifierDict(DictionaryConfiguration cfg){
		List<char[]> list = cfg.getQuantifierDictionary();
		if (list != null){
	        for (char[] segment : list) {
	            _QuantifierDict.fillSegment(segment);
	        }
		}
	}
	
}
