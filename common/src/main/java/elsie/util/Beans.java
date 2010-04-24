package elsie.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import elsie.util.attributes.Initializer;

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
	
	public static void callInitialiseMethod(Object o)
	{
		callAnnotatedMethods(o, Initializer.class, null);
	}
	
	public static <A extends Annotation> Object callAnnotatedMethods(Object o, Class<A> a, Object[] args)
	{
		Class c = o.getClass();
		try {
			List<Method> ms = findMethodByAnnotation(c, a);
			for(Method m : ms)
			{
				return m.invoke(o, args);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return null;
	}
	
	public static <A extends Annotation> List<Method> findMethodByAnnotation(Class c, Class<A> annotation)
	{
		ArrayList<Method> results = new ArrayList<Method>();
		Method[] ms = c.getMethods();
		for(int i = 0; i < ms.length; i++)
		{
			A annot = ms[i].getAnnotation(annotation);
			if(annot != null)
			{
				results.add(ms[i]);
			}
		}
		return results;
	}
	
	public static <A extends Annotation> List<Field> findFieldsByAnnotation(Class c, Class<A> annotation)
	{
		ArrayList<Field> results = new ArrayList<Field>();
		Field[] fs = c.getFields();
		for(int i = 0; i < fs.length; i++)
		{
			A annot = fs[i].getAnnotation(annotation);
			if(annot != null)
			{
				results.add(fs[i]);
			}
		}
		return results;
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
			Method m = o.getClass().getMethod(setter, type);
			System.out.println("setting property " + name + " using setter " + setter + " on " + o);
			m.invoke(o, value);
		} catch (NoSuchMethodException e) {
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}
}
