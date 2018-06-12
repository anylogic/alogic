package com.alogic.cube.mdr;

import java.util.ArrayList;
import java.util.List;

import com.anysoft.util.Properties;

/**
 * 数据格
 * 
 * 
 * @author duanyy
 * @since 1.6.11.35
 */
public class DataCell  implements Summarable{
	protected List<DataValue> values = null;
	protected List<Measure> measures = null;
	
	public List<DataValue> getValues(){
		return values;
	}
	
	public List<Measure> getMeasures(){
		return this.measures;
	}
	
	protected DataCell(final List<Measure> measures){
		this.measures = measures;
		values = new ArrayList<DataValue>(measures.size());
		for (Measure measure:measures){
			values.add(new DataValue(measure));
		}
	}
	
	@Override
	public void sum(final Properties provider){
		for (DataValue value:values){
			value.sum(provider);
		}
	}
}
