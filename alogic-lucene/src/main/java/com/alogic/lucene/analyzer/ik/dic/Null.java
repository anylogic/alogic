package com.alogic.lucene.analyzer.ik.dic;

import java.util.List;

import com.alogic.lucene.analyzer.ik.DictionaryLoader;

public class Null extends DictionaryLoader.Abstract{

	@Override
	public List<char[]> getMainDictionary() {
		return null;
	}

	@Override
	public List<char[]> getStopWordDictionary() {
		return null;
	}

	@Override
	public List<char[]> getQuantifierDictionary() {
		return null;
	}
	
}