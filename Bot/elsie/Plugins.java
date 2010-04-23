package elsie;

import java.util.HashSet;
import java.util.Hashtable;

import botFramework.interfaces.IBot;
import botFramework.interfaces.IChanBotEvent;
import botFramework.interfaces.ICommandsMap;
import botFramework.interfaces.IDatabase;
import botFramework.interfaces.IPlugins;
import botFramework.interfaces.IUserFunctions;
import elsie.util.Beans;

public class Plugins implements IPlugins, ICommandsMap {
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
	
	public ICommandsMap getCommandsMap()
	{
		return this;
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
			chanBotPluginClasses.put(cmd, cname);
		}
	}
	
	public void removePluginCommand(String cmd)
	{
		chanBotPluginClasses.remove(cmd);
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
	
	public <T> String[] getCommand(Object event)
	{
		if(event instanceof IChanBotEvent)
		{
			return ((IChanBotEvent)event).getBotCommand();
		} else {
			return null;
		}
	}

	@Override
	public <T> Object findAndLoadPlugin(T event) {
		System.out.println("finding plugin for " + event);
		
		String cmds[] = getCommand(event);
		
		if(cmds == null) {
			System.out.println("couldn't get command from event " + event);
		} else if(cmds.length == 0) {
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
			
			plugin = findPluginForCommand(cmd);
		}

		plugin = findPluginForCommand(fallbackCommand);
		
		return plugin;
	}
	
	public <T,K> K findAndLoadPlugin(T event, Class<K> iface)
	{
		Object plugin = findAndLoadPlugin(event);
		
		if(plugin == null)
			return null;
		else
			return Beans.findInterface(plugin, iface);
	}
	
	public Object findPluginForCommand(String cmd)
	{
		Object plugin = null;
		String cname = chanBotPluginClasses.get(cmd);
		
		if(cname == null)
			return null;
		
		plugin = loadPlugin(cname);

		if(plugin == null)
			return null;
		
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

	public void configurePlugin(Object l)
	{
		Class c = l.getClass();
		
		Beans.setProperty(l, "bot", this.bot, IBot.class);
		Beans.setProperty(l, "plugins", this, IPlugins.class);
		Beans.setProperty(l, "userFunctions", this.userFunctions, IUserFunctions.class);
		Beans.setProperty(l, "database", this.database, IDatabase.class);
		
		Beans.callMethod(l, "prepare", null, null);
	}

	public IUserFunctions getUserFunctions() {
		return userFunctions;
	}

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
