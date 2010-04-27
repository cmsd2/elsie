package botFramework;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import botFramework.interfaces.ICommandsMap;
import botFramework.interfaces.IIrcEvent;
import botFramework.interfaces.IPlugins;
import elsie.util.Beans;

public class Plugins implements IPlugins, ICommandsMap, ApplicationContextAware {
	private static final Log log = LogFactory.getLog(Plugins.class);

	private Map<String, String> pluginBeanIds = new HashMap<String, String> ();
	private ApplicationContext context;
	private String fallbackHandler;
	private PluginFactory pluginFactory;
	
	public Plugins()
	{
	}
	
	public ApplicationContext getApplicationContext()
	{
		return context;
	}
	
	public void setApplicationContext(ApplicationContext context)
	{
		this.context = context;
	}
	
	public PluginFactory getPluginFactory()
	{
		return pluginFactory;
	}
	
	public void setPluginFactory(PluginFactory pluginFactory)
	{
		this.pluginFactory = pluginFactory;
	}
	
	public ICommandsMap getCommandsMap()
	{
		return this;
	}
	
	public Map<String, String> getChanBotPluginClasses() {
		return pluginBeanIds;
	}
	
	public boolean hasPluginCommand(String cmd)
	{
		return pluginBeanIds.containsKey(cmd);
	}
	
	public String getPluginCommand(String cmd)
	{
		return pluginBeanIds.get(cmd);
	}
	
	public void addPluginCommand(String cmd, String cname)
	{
		if(pluginBeanIds.containsKey(cmd))
		{
			throw new IllegalArgumentException("Can't overwrite existing command hook");
		} else {
			log.info("Adding plugin mapping " + cmd + " => " + cname);
			pluginBeanIds.put(cmd, cname);
		}
	}
	
	public void removePluginCommand(String cmd)
	{
		String cname = pluginBeanIds.get(cmd);
		log.info("Removing plugin mapping " + cmd + " => " + cname);
		pluginBeanIds.remove(cmd);
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
		log.info("reloading plugins.");
		this.pluginFactory.clear();
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

		if(plugin == null && fallbackHandler != null)
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
		String cname = pluginBeanIds.get(cmd);
		
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
		
		return pluginFactory.getPluginContext().getBean(cname);
	}

}
