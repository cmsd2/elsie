package botFramework;

import java.util.HashMap;
import java.util.Map;

import botFramework.interfaces.IBot;
import botFramework.interfaces.IBotEvent;
import botFramework.interfaces.IBotListener;
import botFramework.interfaces.IChanBotEvent;
import botFramework.interfaces.IChanBotListener;
import botFramework.interfaces.IChanEvent;
import botFramework.interfaces.IChanListener;
import botFramework.interfaces.IChannel;
import botFramework.interfaces.IChannels;
import botFramework.interfaces.IErrorEvent;
import botFramework.interfaces.IEventSink;
import botFramework.interfaces.IEventSource;
import botFramework.interfaces.IIrcEvent;
import botFramework.interfaces.IIrcListener;

public class Channels implements IEventSink, IChannels {

	private IBot bot;
	private EventBridge eventHandler;
	private IEventSource<IErrorEvent> errorEvents;
	private IEventSource<IChanEvent> chanEvents;
	private IEventSource<IChanBotEvent> chanBotEvents;
	private IEventSource<IBotEvent> botEvents;
	private IEventSource<IIrcEvent> ircEvents;
	private IEventSource<IChanBotEvent> unknownCommandEvents;
	private Map<String, IChannel> chanMap = new HashMap<String, IChannel>();
	
	public Channels()
	{
		eventHandler = new EventBridge(this);
		errorEvents = new EventSource<IErrorEvent> (this, null);
		chanEvents = new EventSource<IChanEvent> (this, errorEvents);
		chanBotEvents = new EventSource<IChanBotEvent> (this, errorEvents);
		botEvents = new EventSource<IBotEvent> (this, errorEvents);
		ircEvents = new EventSource<IIrcEvent> (this, errorEvents);
		unknownCommandEvents = new EventSource<IChanBotEvent> (this, errorEvents);
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IChannels#getBot()
	 */
	public IBot getBot()
	{
		return bot;
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IChannels#setBot(botFramework.interfaces.IBot)
	 */
	public void setBot(IBot bot)
	{
		if(this.bot != null)
		{
			this.bot.getIrcEvents().remove(getIrcListener());
		}
		this.bot = bot;
		if(this.bot != null)
		{
			this.bot.getIrcEvents().add(getIrcListener());
		}
	}
	
	public Map<String,IChannel> getChannelMap()
	{
		return chanMap;
	}
	
	public IChannel getChannel(String name)
	{
		return chanMap.get(name);
	}
	
	public void addChannel(IChannel channel)
	{
		chanMap.put(channel.getChannel(), channel);
	}
	
	public void removeChannel(IChannel channel)
	{
		chanMap.remove(channel.getChannel());
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IChannels#getErrors()
	 */
	public IEventSource<IErrorEvent> getErrors()
	{
		return errorEvents;
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IChannels#getChanEvents()
	 */
	public IEventSource<IChanEvent> getChanEvents()
	{
		return chanEvents;
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IChannels#getChanBotEvents()
	 */
	public IEventSource<IChanBotEvent> getChanBotEvents()
	{
		return chanBotEvents;
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IChannels#getIrcEvents()
	 */
	public IEventSource<IIrcEvent> getIrcEvents()
	{
		return ircEvents;
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IChannels#getUnknownCommandEvents()
	 */
	public IEventSource<IChanBotEvent> getUnknownCommandEvents()
	{
		return unknownCommandEvents;
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IChannels#getChanListener()
	 */
	public IChanListener getChanListener()
	{
		return eventHandler.getChanListener();
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IChannels#getChanBotListener()
	 */
	public IChanBotListener getChanBotListener()
	{
		return eventHandler.getChanBotListener();
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IChannels#getBotListener()
	 */
	public IBotListener getBotListener()
	{
		return eventHandler.getBotListener();
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IChannels#getIrcListener()
	 */
	public IIrcListener getIrcListener()
	{
		return eventHandler.getIrcListener();
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IChannels#getUnknownCommandListener()
	 */
	public IChanBotListener getUnknownCommandListener()
	{
		return eventHandler.getUnknownCommandListener();
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IChannels#respondToBotEvent(botFramework.interfaces.IBotEvent)
	 */
	@Override
	public boolean respondToBotEvent(IBotEvent event) {
		return botEvents.sendEvent("ChannelManager", event);
	}

	/* (non-Javadoc)
	 * @see botFramework.IChannels#respondToChanBotEvent(botFramework.interfaces.IChanBotEvent)
	 */
	@Override
	public boolean respondToChanBotEvent(IChanBotEvent event) {
		return chanBotEvents.sendEvent("ChannelManager", event);
	}

	/* (non-Javadoc)
	 * @see botFramework.IChannels#respondToChanEvent(botFramework.interfaces.IChanEvent)
	 */
	@Override
	public boolean respondToChanEvent(IChanEvent event) {
		return chanEvents.sendEvent("ChannelManager", event);
	}

	/* (non-Javadoc)
	 * @see botFramework.IChannels#respondToIrcEvent(botFramework.interfaces.IIrcEvent)
	 */
	@Override
	public boolean respondToIrcEvent(IIrcEvent event) {
		return ircEvents.sendEvent("ChannelManager", event);
	}

	/* (non-Javadoc)
	 * @see botFramework.IChannels#respondToUnknownCommandListener(botFramework.interfaces.IChanBotEvent)
	 */
	@Override
	public boolean respondToUnknownCommandListener(IChanBotEvent event) {
		return unknownCommandEvents.sendEvent("ChannelManager", event);
	}
}
