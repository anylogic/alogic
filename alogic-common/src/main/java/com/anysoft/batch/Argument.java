package com.anysoft.batch;

import java.io.PrintStream;

import org.w3c.dom.Element;

import com.anysoft.formula.DataProvider;
import com.anysoft.selector.Selector;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;

/**
 * 指令参数
 * 
 * @author duanyy
 *
 */
public class Argument implements XMLConfigurable,CommandHelper {
	protected String id;
	/**
	 * to get the argument id
	 * @return the argument id
	 */
	public String getId(){
		return id;
	}
	
	protected String name;
	/**
	 * to get the argument name
	 * @return the argument name
	 */
	public String getName(){
		return name;
	}
	
	protected String note;
	/**
	 * to get the note
	 * @return the note
	 */
	public String getNote(){
		return note;
	}
	
	protected boolean nullable = false;
	
	/**
	 * whether or not the value can be null
	 * @return whether or not
	 */
	public boolean isNullable(){
		return nullable;
	}
	
	public boolean isOK(){
		return selector != null && selector.isOk();
	}
	/**
	 * the selector
	 */
	protected Selector selector = null;
	
	public void configure(Element _e, Properties _properties)
			throws BaseException {
		Properties p = new XmlElementProperties(_e,_properties);
		
		id = PropertiesConstants.getString(p, "selector-id", "");
		name = PropertiesConstants.getString(p, "name", "");
		note = PropertiesConstants.getString(p,"note","");
		nullable = PropertiesConstants.getBoolean(p, "nullable", nullable);
		
		selector = Selector.newInstance(_e,p,"SingleField");
	}

	/**
	 * 计算参数值
	 * 
	 * @param dp data provider
	 * @return 计算之后的value
	 */
	public String getValue(DataProvider dp){
		return selector != null?selector.select(dp):"";
	}

	public void printHelp(PrintStream ps) {
		ps.println("\t|" + getId() + "\t-" + getName());
		if (note != null && note.length() > 0){
			ps.println("\t\t|" + getNote());
		}
		{
			// nullable
			ps.println("\t\t|Nullable = " + Boolean.toString(nullable));
		}
		{
			// default value
			ps.println("\t\t|Default Value = " + (selector == null ? "":selector.getDefaultValue()));
		}
		{
			// selector
			ps.println("\t\t|Selector = " + (selector == null ? "null":selector.getClass().getName()));
		}
	}
}
