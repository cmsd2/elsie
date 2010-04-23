package elsie;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Hashtable;

import botFramework.interfaces.*;

import java.beans.*;

public class Plugins implements IPlugins {
	private String rootDir;
	private ClassLoader loader = null;
	private HashSet<String> prefixExceptions = new HashSet<String>();
	private Hashtable<String, String> chanBotPluginClasses = new Hashtable<String, String> ();
	private Hashtable<Class, Object> chanBotPlugins = new Hashtable<Class, Object>();

	private IBot bot;
	private IUserFunctions userFunctions;
	private IDatabase database;
	private String fallbackCommand = "!unknown-command";
	
	public Plugins(String rootDir)
	{
		this.rootDir = rootDir;
	}
	
	public HashSet<String> getPrefixExceptions()
	{
		return prefixExceptions;
	}
	
	public ClassLoader getPluginClassLoader()
	{
		if (loader == null)
		{
			loader = new PluginClassLoader(rootDir, prefixExceptions);
		}
		return loader;
	}
	
	public Hashtable<String, String> getChanBotPluginClasses() {
		return chanBotPluginClasses;
	}
	
	public String getFallbackCommand()
	{
		return fallbackCommand;
	}
	
	public void setFallbackCommand(String fbc)
	{
		this.fallbackCommand = fbc;
	}
	
	public IBot getBot()
	{
		return bot;
	}
	
	public void setBot(IBot bot)
	{
		this.bot = bot;
	}
	
	public void reloadPlugins()
	{
		System.out.println("reloading plugins. throwing away " + chanBotPlugins.size() + " plugin instances and class loader");
		this.loader = null;
		this.chanBotPlugins.clear();
	}

	@Override
	public IChanListener findPlugin(IChanEvent event) {
		System.out.println("no plugin for " + event);
		return null;
	}

	@Override
	public Object findPlugin(IChanBotEvent event) {
		System.out.println("finding plugin for " + event);
		
		String cmds[] = event.getBotCommand();
		
		if(cmds.length == 0) {
			System.out.println("command too short");
			return null;
		} else if(cmds.length == 1) {
			cmds = cmds[0].split(" ");
		}
		
		String cmd;
		Object plugin;
		
		for(int i = 0; i < cmds.length; i++)
		{
			cmd = cmds[i];
			
			String cname = chanBotPluginClasses.get(fallbackCommand);
			
			plugin = loadPlugin(cname, event);
			
			if(plugin != null)
				return plugin;
		}
		
		String cname = chanBotPluginClasses.get(fallbackCommand);
		
		plugin = loadPlugin(cname, event);
		
		return plugin;
	}
	
	public Object loadPlugin(String cname, IChanBotEvent event)
	{
		System.out.println("trying lookup of " + cname);
		
		try {
			Class c = null;
			
			System.out.println("trying class load for " + cname);
			c = Class.forName(cname, true, getPluginClassLoader());
		
			if(c != null)
				return getPluginInstance(c);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		
		System.out.println("failed to load plugin " + cname);

		return null;
	}
	
	public Object getPluginInstance(Class c)
	{
		Object o = chanBotPlugins.get(c);
		
		if(o == null)
		{
			try {
				o = c.newInstance();

				chanBotPlugins.put(c, o);
				
				configurePlugin(o);
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}
		}
		
		return o;
	}
	
	public <T> T findInterface(Object o, Class<T> c)
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
	
	public void configurePlugin(Object l)
	{
		Class c = l.getClass();
		
		setProperty(l, "bot", this.bot, IBot.class);
		setProperty(l, "plugins", this, IPlugins.class);
		setProperty(l, "userFunctions", this.userFunctions, IUserFunctions.class);
		setProperty(l, "database", this.database, IDatabase.class);
		
		callMethod(l, "prepare", null, null);
	}
	
	public void callMethod(Object o, String name, Object[] args, Class[] types)
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
	
	public void setProperty(Object o, String name, Object value, Class type)
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

	@Override
	public IUserFunctions getUserFunctions() {
		return userFunctions;
	}

	@Override
	public void setUserFunctions(IUserFunctions f) {
		this.userFunctions = f;
	}
	
	public IDatabase getDatabase() {
		return database;
	}
	
	public void setDatabase(IDatabase db)
	{
		this.database = db;
	}
}
