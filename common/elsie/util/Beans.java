package elsie.util;

import java.lang.reflect.Method;

import botFramework.interfaces.IBot;
import botFramework.interfaces.IDatabase;
import botFramework.interfaces.IPlugins;
import botFramework.interfaces.IUserFunctions;

public class Beans {
	
	public static <T> T findInterface(Object o, Class<T> c)
	{
		if(c.isAssignableFrom(o.getClass()))
		{
			return (T)o;
		} else {
			T value = null;
			
			String name = c.getSimpleName();
			
			// strip of leading interface name 'I'
			if(value == null && name.length() > 2 && name.charAt(0) == 'I' && name.substring(0, 1).equals(name.substring(0, 1).toUpperCase()))
			{
				name = name.substring(1);
				
				try {
					value = getProperty(o, name, c);
				} catch (NoSuchMethodException e) {
					System.out.println("No method " + name + " on " + o);
				}
			}
			
			name = c.getSimpleName();
			
			try {
				
				value = getProperty(o, name, c);
			} catch (NoSuchMethodException e) {
				System.out.println("No method " + name + " on " + o);
			}

			if(value == null)
			{
				throw new ClassCastException("Cannot cast " + o + " to " + c);
			} else {
				System.out.println("Found interface " + c + " on " + o);
				return value;
			}
		}
	}
	
	public static <T> T getProperty(Object o, String name, Class<T> c) throws NoSuchMethodException
	{
		String getter = getGetter(name);
		
		try {
			Method m = o.getClass().getMethod(getter, null);
			
			return (T) m.invoke(o, null);
		} catch (NoSuchMethodException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void callMethod(Object o, String name, Object[] args, Class[] types)
	{
		try {
			System.out.println("calling method " + name + " on " + o);
			Method m = o.getClass().getMethod(name, types);
			m.invoke(o, args);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}
	
	public static String getGetter(String name)
	{
		return "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
	}
	
	public static String getSetter(String name)
	{
		return "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
	}
	
	public static void setProperty(Object o, String name, Object value, Class type)
	{
		try {
			String setter = getSetter(name); 
			System.out.println("setting property " + name + " using setter " + setter + " on " + o);
			Method m = o.getClass().getMethod(setter, type);
			m.invoke(o, value);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}
}
