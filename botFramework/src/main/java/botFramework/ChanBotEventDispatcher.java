package botFramework;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import botFramework.interfaces.IChanBotEvent;
import botFramework.interfaces.IChanBotListener;
import botFramework.interfaces.IChannels;
import botFramework.interfaces.IEventListener;
import botFramework.interfaces.IPlugins;

public class ChanBotEventDispatcher extends EventDispatcher<IChanBotEvent> {
	private static final Log log = LogFactory.getLog(ChanBotEventDispatcher.class);

	private IPlugins plugins;
	private IChannels channels;
	
	public ChanBotEventDispatcher()
	{
	}
	
	public IPlugins getPlugins()
	{
		return plugins;
	}

	public void setPlugins(IPlugins plugins)
	{
		this.plugins = plugins;
	}
	
	public IChannels getChannels()
	{
		return channels;
	}

	public void setChannels(IChannels channels)
	{
		if(this.channels != null)
		{
			log.info("Unsubscribing to chan bot events from channel group " + channels);
			this.channels.getChanBotEvents().remove(this);
		}
		this.channels = channels;
		if(this.channels != null)
		{
			log.info("Subscribing to chan bot events from channel group " + channels);
			this.channels.getChanBotEvents().add(this);
		}
	}

	@Override
	public String getKey(IChanBotEvent event) {
		return event.getBotCommand()[0];
	}

	@Override
	public IEventListener<IChanBotEvent> loadListener(IChanBotEvent event) {
		log.info("Finding plugin to handle " + event);
		return plugins.findAndLoadPlugin(event, IChanBotListener.class);
	}
}
