package botFramework.interfaces;

public interface IChannels {
	
	String ROLE = IChannels.class.getName();

	public abstract IBot getBot();

	public abstract void setBot(IBot bot);

	public abstract IEventSource<IErrorEvent> getErrors();

	public abstract IEventSource<IChanEvent> getChanEvents();

	public abstract IEventSource<IChanBotEvent> getChanBotEvents();

	public abstract IEventSource<IIrcEvent> getIrcEvents();

	public abstract IEventSource<IChanBotEvent> getUnknownCommandEvents();

	public abstract IChanListener getChanListener();

	public abstract IChanBotListener getChanBotListener();

	public abstract IBotListener getBotListener();

	public abstract IIrcListener getIrcListener();

	public abstract IChanBotListener getUnknownCommandListener();

	public abstract boolean respondToBotEvent(IBotEvent event);

	public abstract boolean respondToChanBotEvent(IChanBotEvent event);

	public abstract boolean respondToChanEvent(IChanEvent event);

	public abstract boolean respondToIrcEvent(IIrcEvent event);

	public abstract boolean respondToUnknownCommandListener(IChanBotEvent event);

	public abstract IChannel getChannel(String name);
	
	public abstract void addChannel(IChannel channel);
	
	public abstract void removeChannel(IChannel channel);
}