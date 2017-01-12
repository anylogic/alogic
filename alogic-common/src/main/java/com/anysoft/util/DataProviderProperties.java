package com.anysoft.util;

import com.anysoft.formula.DataProvider;

/**
 * 将DataProvider包装为Properties
 * 
 * @author yyduan
 * @since 1.6.6.13
 */
public class DataProviderProperties extends Properties {

	/**
	 * DataProvider
	 */
	protected DataProvider provider = null;
	
	public DataProviderProperties(DataProvider p){
		provider = p;
	}
	
	@Override
	protected void _SetValue(String _name, String _value) {
		// noting to do
	}

	@Override
	protected String _GetValue(String _name) {
		String found = null;
		
		if (provider != null){
			Object context = provider.getContext(_name);			
			found = provider.getValue(_name, context, null);
		}
		
		return found;
	}

	@Override
	public void Clear() {
		// nothing to do
	}

}
