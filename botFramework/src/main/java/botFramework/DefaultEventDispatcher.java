package botFramework;

import botFramework.interfaces.IChannels;
import botFramework.interfaces.IIrcEvent;
import botFramework.interfaces.IPlugins;

public abstract class DefaultEventDispatcher<T extends IIrcEvent> extends EventDispatcher<T> {

	private IPlugins plugins;
	private IChannels channels;
	
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
			removeChannelListeners(this.channels);
		}
		this.channels = channels;
		if(this.channels != null)
		{
			addChannelListeners(this.channels);
		}
	}
	
	protected abstract void addChannelListeners(IChannels channels);
	
	protected abstract void removeChannelListeners(IChannels channels);
}
