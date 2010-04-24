package elsie;

import elsie.util.Beans;

public class Property {

	private static final Object empty = new Object();
	
	private String name;
	private Class type;
	private Object value;
	
	public Property(String name, Class type)
	{
		this.name = name;
		this.type = type;
		this.value = empty;
	}
	
	public Object getValue()
	{
		return value;
	}
	
	public void setValue(Object value)
	{
		this.value = value;
	}
	
	public void clearValue()
	{
		this.value = empty;
	}
	
	public void apply(Object bean)
	{
		if(value != empty)
		{
			Beans.setProperty(bean, name, value, type);
		}
	}
}
