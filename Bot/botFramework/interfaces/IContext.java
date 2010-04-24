package botFramework.interfaces;

import java.util.Map;

import elsie.Property;

public interface IContext {

	public abstract Map<String, Property> getProperties();

	public abstract void apply(Object o);

	public abstract <T> void setProperty(String name, Class<T> type, T value);

	public abstract <T> T getProperty(String name, Class<T> type);
	
	public <T> T getObject(Class<T> c);

}