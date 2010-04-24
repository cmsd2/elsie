package botFramework.interfaces;

public interface IEventSink {
	public boolean respondToChanEvent(IChanEvent event);
	
	public boolean respondToChanBotEvent(IChanBotEvent event);
	
	public boolean respondToBotEvent(IBotEvent event);
	
	public boolean respondToIrcEvent(IIrcEvent event);
	
	public boolean respondToUnknownCommandListener(IChanBotEvent event);
}
