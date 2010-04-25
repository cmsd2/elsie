package elsie.util;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Beans {
	
	private static final Log log = LogFactory.getLog(Beans.class);

	public static <T> T findInterface(Object o, Class<T> c)
	{
		log.debug("Looking for interface " + c + " on object " + o);

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
					log.debug("No method " + name + " on " + o);
				}
			}
			
			name = c.getSimpleName();
			
			try {
				
				value = getProperty(o, name, c);
			} catch (NoSuchMethodException e) {
				log.debug("No method " + name + " on " + o);
			}

			if(value == null)
			{
				throw new ClassCastException("Cannot cast " + o + " to " + c);
			} else {
				log.debug("Found interface " + c + " on " + o);
				return value;
			}
		}
	}
	
	public static <T> T getProperty(Object o, String name, Class<T> c) throws NoSuchMethodException
	{
		String getter = getGetter(name);
		
		log.debug("Getting property " + name + " from " + o + " via method " + getter);
		try {
			Method m = o.getClass().getMethod(getter, null);
			
			return (T) m.invoke(o, null);
		} catch (NoSuchMethodException e) {
			String msg = "Failed to get property " + name + " from " + o + " via method " + getter;
			log.error(msg, e);
			throw e;
		} catch (Exception e) {
			throw new RuntimeException("Failed to get property " + name + " from " + o + " via method " + getter, e);
		}
	}
	
	public static void callMethod(Object o, String name, Object[] args, Class[] types)
	{
		try {
			log.debug("Calling method " + name + " on " + o);
			Method m = o.getClass().getMethod(name, types);
			m.invoke(o, args);
		} catch (Exception e) {
			log.error("Error calling method " + name + " on " + o, e);
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
		String setter = getSetter(name);

		try {
			Method m = o.getClass().getMethod(setter, type);
			log.debug("setting property " + name + " using setter " + setter + " on " + o);
			m.invoke(o, value);
		} catch (NoSuchMethodException e) {
			log.error("Failed to set property " + name + " using setter " + setter + " on " + o, e);
		} catch (Exception e) {
			log.error("Failed to set property " + name + " using setter " + setter + " on " + o, e);
		}
	}
}
