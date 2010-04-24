package elsie.plugins;

import elsie.util.attributes.Inject;
import botFramework.interfaces.*;

public abstract class AbstractPlugin {
	private IBot bot;
	private IPlugins plugins;
	private IUserFunctions usr;
	private IDatabase database;
	private IContext context;
	
	public IDatabase getDatabase()
	{
		return database;
	}
	
	@Inject
	public void setDatabase(IDatabase database)
	{
		this.database = database;
	}

	public IBot getBot()
	{
		return bot;
	}
	
	@Inject
	public void setBot(IBot bot)
	{
		System.out.println("setting bot property to " + bot);
		this.bot = bot;
	}
	
	public IContext getContext()
	{
		return context;
	}
	
	@Inject
	public void setContext(IContext context)
	{
		this.context = context;
	}
	
	public IPlugins getPlugins()
	{
		return plugins;
	}
	
	@Inject
	public void setPlugins(IPlugins plugins)
	{
		System.out.println("setting plugins property to " + plugins);
		this.plugins = plugins;
	}
	
	public IUserFunctions getUserFunctions()
	{
		return usr;
	}
	
	@Inject
	public void setUserFunctions(IUserFunctions usr)
	{
		this.usr = usr;
	}
	
	public IChanListener getChanListener()
	{
		return new IChanListener() {
			
			@Override
			public boolean respond(IChanEvent event) {
				AbstractPlugin.this.chanRespond(event);
				return true;
			}
		};
	}

	public IChanBotListener getChanBotListener()
	{
		return new IChanBotListener() {
			
			@Override
			public boolean respond(IChanBotEvent event) {
				return AbstractPlugin.this.chanBotRespond(event);
			}
		};
	}
	
	public IChanBotUnknownCmdListener getChanBotUnknownCmdListener()
	{
		return new IChanBotUnknownCmdListener() {
			@Override
			public boolean respond(IChanBotEvent event) {
				AbstractPlugin.this.chanBotUnknownCmdRespond(event);
				return true;
			}
		};
	}
	
	protected boolean chanBotRespond(IChanBotEvent event) {
		return false;
	}
	
	protected void chanRespond(IChanEvent event) {
	}

	protected void chanBotUnknownCmdRespond(IChanBotEvent event) {
	}
}
