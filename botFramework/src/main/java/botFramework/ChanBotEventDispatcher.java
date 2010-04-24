package botFramework;

import elsie.util.attributes.Inject;
import botFramework.interfaces.IChanBotEvent;
import botFramework.interfaces.IChanBotListener;
import botFramework.interfaces.IChannels;
import botFramework.interfaces.IEventListener;
import botFramework.interfaces.IPlugins;

public class ChanBotEventDispatcher extends EventDispatcher<IChanBotEvent> {
	private IPlugins plugins;
	private IChannels channels;
	
	public ChanBotEventDispatcher()
	{
	}
	
	public IPlugins getPlugins()
	{
		return plugins;
	}
	
	@Inject
	public void setPlugins(IPlugins plugins)
	{
		this.plugins = plugins;
	}
	
	public IChannels getChannels()
	{
		return channels;
	}
	
	@Inject
	public void setChannels(IChannels channels)
	{
		if(this.channels != null)
		{
			this.channels.getChanBotEvents().remove(this);
		}
		this.channels = channels;
		if(this.channels != null)
		{
			this.channels.getChanBotEvents().add(this);
		}
	}

	@Override
	public String getKey(IChanBotEvent event) {
		return event.getBotCommand()[0];
	}

	@Override
	public IEventListener<IChanBotEvent> loadListener(IChanBotEvent event) {
		return plugins.findAndLoadPlugin(event, IChanBotListener.class);
	}
}
