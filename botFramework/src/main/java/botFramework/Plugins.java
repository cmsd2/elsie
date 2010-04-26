package botFramework;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import botFramework.interfaces.IChanBotEvent;
import botFramework.interfaces.ICommandsMap;
import botFramework.interfaces.IIrcEvent;
import botFramework.interfaces.IPlugins;
import elsie.util.Beans;

public class Plugins implements IPlugins, ICommandsMap, ApplicationContextAware {
	private static final Log log = LogFactory.getLog(Plugins.class);

	private ClassLoader loader = null;
	private String loaderId;
	private Map<String, String> chanBotPluginClasses = new HashMap<String, String> ();
	private Map<Class, Object> chanBotPlugins = new HashMap<Class, Object>();

	private ApplicationContext context;
	private ApplicationContext pluginContext;
	private String fallbackHandler;
	
	public Plugins()
	{
	}
	
	public void init() throws Exception
	{
		ClassLoader prevClassLoader = Thread.currentThread().getContextClassLoader();
		ClassLoader loader = getPluginClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(loader);
			doInit();
		} finally {
			Thread.currentThread().setContextClassLoader(prevClassLoader);
		}
	}
	
	public void doInit() throws Exception
	{
		Class c = Class.forName("elsie.plugins.Version", true, loader);
		log.info("Found version plugin " + c + " in classpath");
		this.pluginContext = new ClassPathXmlApplicationContext(new String[] {
			"plugins.xml"
		}, c, context);
		ObjectFactory f = (ObjectFactory) pluginContext.getBean("versionFactory");
		log.info("Found version plugin factory " + f);
		Object v = f.getObject();
		log.info("Plugin version " + v.getClass() + " loaded with " + v.getClass().getClassLoader());
	}
	
	public ApplicationContext getApplicationContext()
	{
		return context;
	}
	
	public void setApplicationContext(ApplicationContext context)
	{
		this.context = context;
	}
	
	public ICommandsMap getCommandsMap()
	{
		return this;
	}
	
	public String getClassLoaderId()
	{
		return loaderId;
	}
	
	public void setClassLoaderId(String loaderId)
	{
		this.loaderId = loaderId;
	}
	
	public ClassLoader getPluginClassLoader()
	{
		if (loader == null)
		{
			loader = (ClassLoader) context.getBean(loaderId);
			log.info("Got fresh plugin class loader " + loader);
		}
		return loader;
	}
	
	public Map<String, String> getChanBotPluginClasses() {
		return chanBotPluginClasses;
	}
	
	public boolean hasPluginCommand(String cmd)
	{
		return chanBotPluginClasses.containsKey(cmd);
	}
	
	public String getPluginCommand(String cmd)
	{
		return chanBotPluginClasses.get(cmd);
	}
	
	public void addPluginCommand(String cmd, String cname)
	{
		if(chanBotPluginClasses.containsKey(cmd))
		{
			throw new IllegalArgumentException("Can't overwrite existing command hook");
		} else {
			log.info("Adding plugin mapping " + cmd + " => " + cname);
			chanBotPluginClasses.put(cmd, cname);
		}
	}
	
	public void removePluginCommand(String cmd)
	{
		String cname = chanBotPluginClasses.get(cmd);
		log.info("Removing plugin mapping " + cmd + " => " + cname);
		chanBotPluginClasses.remove(cmd);
	}
	
	public String getFallbackHandler()
	{
		return fallbackHandler;
	}
	
	public void setFallbackHandler(String fbc)
	{
		this.fallbackHandler = fbc;
	}
	
	public void reloadPlugins()
	{
		log.info("reloading plugins. throwing away " + chanBotPlugins.size() + " plugin instances and class loader");
		this.loader = null;
		this.chanBotPlugins.clear();
	}

	@Override
	public <T extends IIrcEvent> Object findAndLoadPlugin(T event) {
		System.out.println("finding plugin for " + event);
		
		String cmd = event.getBotCommandName();
		Object plugin = null;
		
		if(cmd != null)
		{
			plugin = findPluginForCommand(cmd);
		}

		if(plugin == null)
		{
			plugin = loadPlugin(fallbackHandler);
		}
		
		return plugin;
	}
	
	public <T extends IIrcEvent,K> K findAndLoadPlugin(T event, Class<K> iface)
	{
		Object plugin = findAndLoadPlugin(event);
		
		if(plugin == null)
			return null;
		else
			return Beans.findInterface(plugin, iface);
	}
	
	public Object findPluginForCommand(String cmd)
	{
		log.info("Finding plugin to handle command " + cmd);
		Object plugin = null;
		String cname = chanBotPluginClasses.get(cmd);
		
		if(cname != null)
		{
			log.info("No class configured to handle command " + cmd);
			return null;
		}

		plugin = loadPlugin(cname);

		if(plugin == null)
		{
			log.info("No plugin could be loaded to handle command " + cmd);
			return null;
		}
		
		return plugin;
	}
	
	public <K> K findPluginForCommand(String cmd, Class<K> iface)
	{
		Object plugin = findPluginForCommand(cmd);
		
		if(plugin == null)
			return null;
		else
			return Beans.findInterface(plugin, iface);
	}
	
	public Object loadPlugin(String cname)
	{
		log.info("trying lookup of " + cname);
		
		try {
			Class c = null;
			
			log.info("trying class load for " + cname);
			c = Class.forName(cname, true, getPluginClassLoader());
		
			if(c != null)
				return getPluginInstance(c);
		} catch (Exception e) {
			log.error("Failed to load plugin class " + cname, e);
		}

		return null;
	}
	
	public Object getPluginInstance(Class c)
	{
		log.info("Getting plugin instance of " + c);
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

	public void configurePlugin(Object l)
	{
	}
}
