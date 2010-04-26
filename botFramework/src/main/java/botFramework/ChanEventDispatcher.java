package botFramework;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import botFramework.interfaces.IChanEvent;
import botFramework.interfaces.IChanListener;
import botFramework.interfaces.IChannels;
import botFramework.interfaces.IEventListener;
import botFramework.interfaces.IIrcMessage;
import botFramework.interfaces.IPlugins;

public class ChanEventDispatcher extends EventDispatcher<IChanEvent> {
	
	private static final Log log = LogFactory.getLog(ChanEventDispatcher.class);

	private IPlugins plugins;
	private IChannels channels;
	
	public ChanEventDispatcher()
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
			log.info("Unsubscribing to chan events from channel group " + channels);
			this.channels.getChanEvents().remove(this);
		}
		this.channels = channels;
		if(this.channels != null)
		{
			log.info("Subscribing to chan events from channel group " + channels);
			this.channels.getChanEvents().add(this);
		}
	}

	@Override
	public String getKey(IChanEvent event) {
		IIrcMessage msg = event.getIRCMessage();
		return msg.getPrefix();
	}


	@Override
	public IEventListener<IChanEvent> loadListener(IChanEvent event) {
		log.info("Finding plugin to handle " + event);
		return plugins.findAndLoadPlugin(event, IChanListener.class);
	}
}
