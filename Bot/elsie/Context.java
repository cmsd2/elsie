package elsie;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import elsie.util.Beans;
import elsie.util.Lifecycle;
import elsie.util.attributes.Inject;

import botFramework.interfaces.IContext;
import botFramework.interfaces.IObjectFactory;

public class Context implements IContext {
	private Map<String,Property> properties = new HashMap<String,Property>();
	private Map<Class,IObjectFactory> factories = new HashMap<Class,IObjectFactory>();
	
	public Context()
	{
		factories.put(Object.class, new IObjectFactory() {
			public Object create(Class c) {
				try {
					Object o = c.newInstance();
					
					Context.this.initialiseObject(o);
					
					return o;
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
		setProperty("context", IContext.class, this);
	}
	
	public <T> T getObject(Class<T> c)
	{
		return createObject(c, Lifecycle.Default);
	}
	
	public <T> T getSingleton(Class<T> c)
	{
		return createObject(c, Lifecycle.Singleton);
	}
	
	public <T> T getSingleton(String name, Class<T> c)
	{
		return createNamedObject(name, c, Lifecycle.Singleton);
	}
	
	public <T> T createSingleton(Class<T> c, Class[] interfaces)
	{
		T s = createObject(c, Lifecycle.Singleton);
		
		for(int i = 0; i < interfaces.length; i++)
		{
			IObjectFactory<T> factory = getFactory(c);
			createSingletonFactory(interfaces[i], factory, s);
		}
		
		return s;
	}
	
	public <T> IObjectFactory<T> createSingletonFactory(Class<T> c, IObjectFactory<T> factory, T bean)
	{
		SingletonObjectFactory<T> singletonFactory = new SingletonObjectFactory<T>(factory, bean);
		setFactory(c, singletonFactory);
		return singletonFactory;
	}
	
	public <T> T createObject(Class<T> c, Lifecycle lifecycle)
	{
		IObjectFactory<T> factory = getFactory(c);
		
		if(lifecycle == Lifecycle.Singleton)
		{
			factory = createSingletonFactory(c, factory, null);
		}
		
		T bean = factory.create(c);
		
		return bean;
	}
	
	public <T> T createNamedObject(String n, Class<T> c, Lifecycle lifecycle)
	{
		T bean = createObject(c, lifecycle);
		
		return nameObject(n, c, bean, Lifecycle.Default);
	}
	
	public <T> T nameObject(String n, Class<T> c, T bean)
	{
		return nameObject(n, c, bean, Lifecycle.Default);
	}
	
	public <T> T nameObject(String n, Class<T> c, T bean, Lifecycle lifecycle)
	{		
		setProperty(n, c, bean);
		
		if(lifecycle == Lifecycle.Singleton)
		{
			IObjectFactory<T> currentFactory = getFactory(c);
			createSingletonFactory(c, currentFactory, bean);
		}
		
		return bean;
	}
	
	public void initialiseObject(Object bean)
	{
		apply(bean);
		
		Beans.callInitialiseMethod(bean);
	}
	
	public <T> IObjectFactory<T> getFactory(Class<T> c)
	{
		IObjectFactory<T> factory = (IObjectFactory<T>) factories.get(c);
		
		if(factory == null)
		{
			return factories.get(Object.class);
		}
		
		return factory;
	}
	
	public <T> void setFactory(Class<T> c, IObjectFactory<T> factory)
	{
		factories.put(c, factory);
	}
	
	/* (non-Javadoc)
	 * @see elsie.IContext#getProperties()
	 */
	public Map<String,Property> getProperties()
	{
		return properties;
	}
	
	/* (non-Javadoc)
	 * @see elsie.IContext#apply(java.lang.Object)
	 */
	public void apply(Object o)
	{
		/*for(Map.Entry<String,Property> p : properties.entrySet())
		{
			p.getValue().apply(o);
		}*/
		injectProperties(o);
	}
	
	/* (non-Javadoc)
	 * @see elsie.IContext#setProperty(java.lang.String, java.lang.Class, T)
	 */
	public <T> void setProperty(String name, Class<T> type, T value)
	{
		Property p = properties.get(name);
		if(p == null)
		{
			p = new Property(name, type);
			properties.put(name, p);
		}
		p.setValue(value);
	}
	
	/* (non-Javadoc)
	 * @see elsie.IContext#getProperty(java.lang.String, java.lang.Class)
	 */
	public <T> T getProperty(String name, Class<T> type)
	{
		Property p = properties.get(name);
		if(p == null)
			return null;
		else
			return (T) p.getValue();
	}
	
	public void injectProperties(Object o)
	{
		Class c = o.getClass();
		
		NameFromAnnotation<Inject> injectNamePicker = new NameFromAnnotation<Inject>() {
			@Override
			public String name(Inject annotation) {
				return annotation.name();
			}
		};
		
		TypeFromAnnotation<Inject> injectTypePicker = new TypeFromAnnotation<Inject>() {
			@Override
			public Class type(Inject annotation) {
				return annotation.type();
			}
		};
		
		injectMethodValues(o, Inject.class, injectNamePicker, injectTypePicker);
		injectFieldValues(o, Inject.class, injectNamePicker, injectTypePicker);

	}
	
	public static interface TypeFromAnnotation<A extends Annotation> {
		public Class type(A annotation);
	}
	
	public static interface NameFromAnnotation<A extends Annotation> {
		public String name(A annotation);
	}
	
	public <A extends Annotation> void injectMethodValues(Object o, Class<A> annotationClass, NameFromAnnotation<A> namePicker, TypeFromAnnotation<A> typePicker)
	{
		Class c = o.getClass();
		List<Method> ms = Beans.findMethodByAnnotation(c, Inject.class);
		for(Method m : ms)
		{
			A i = m.getAnnotation(annotationClass);
			Class[] paramTypes = m.getParameterTypes();
			if(paramTypes.length != 1)
			{
				throw new RuntimeException("can't inject value into method expecting anything other than 1 parameter");
			}
			Class type = paramTypes[0];
			try {
				Object value = resolveInjectionValue(i, type, namePicker);
				injectIntoMethod(o, m, value);
			} catch (Exception e) {
				throw new RuntimeException("failed to inject value of type " + type + " into object " + o + " via method " + m);
			}
		}
	}
	
	public <A extends Annotation> void injectFieldValues(Object o, Class<A> annotationClass, NameFromAnnotation<A> namePicker, TypeFromAnnotation<A> typePicker)
	{
		Class c = o.getClass();
		List<Field> fs = Beans.findFieldsByAnnotation(c, annotationClass);
		for(Field f : fs)
		{
			A i = f.getAnnotation(annotationClass);
			Class type = f.getType();
			Class aType = typePicker.type(i);
			if(aType != null && !aType.equals(Object.class))
			{
				type = aType;
			}
			Object value = resolveInjectionValue(i, type, namePicker);
			injectIntoField(o, f, value);
		}
	}
	
	public <A extends Annotation, T> T resolveInjectionValue(A i, Class<T> type, NameFromAnnotation<A> namePicker)
	{
		String name = namePicker.name(i);
		if(name != null && name.length() != 0)
		{
			return getProperty(name, type);
		} else {
			return getObject(type);
		}
	}
	
	public void injectIntoMethod(Object o, Method m, Object value)
	{
		try {
			System.out.println("Injecting " + value + " into " + o + " via method " + m);
			m.invoke(o, new Object[] { value });
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void injectIntoField(Object o, Field f, Object value)
	{
		boolean overrideAccessible = false;
		if(!f.isAccessible())
		{
			f.setAccessible(true);
		}
		try {
			f.set(o, value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if(overrideAccessible)
			{
				f.setAccessible(false);
			}
		}
		
		
	}
}
