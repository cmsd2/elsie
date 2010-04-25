package elsie.plugins;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import botFramework.interfaces.IBot;
import botFramework.interfaces.IChanBotEvent;
import botFramework.interfaces.IChanBotListener;
import botFramework.interfaces.IChanBotUnknownCmdListener;
import botFramework.interfaces.IChanEvent;
import botFramework.interfaces.IChanListener;
import botFramework.interfaces.IDatabase;
import botFramework.interfaces.IPlugins;
import botFramework.interfaces.IUserFunctions;

public abstract class AbstractPlugin implements ApplicationContextAware {
	private ApplicationContext context;
	
	public IDatabase getDatabase()
	{
		return (IDatabase) context.getBean("database");
	}

	public IBot getBot()
	{
		return (IBot) context.getBean("bot");
	}
	
	public ApplicationContext getApplicationContext()
	{
		return context;
	}

	public void setApplicationContext(ApplicationContext context)
	{
		this.context = context;
	}
	
	public IPlugins getPlugins()
	{
		return (IPlugins) context.getBean("plugins");
	}

	public IUserFunctions getUserFunctions()
	{
		return (IUserFunctions) context.getBean("userFunctions");
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
