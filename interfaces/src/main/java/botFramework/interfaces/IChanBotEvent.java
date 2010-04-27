package botFramework.interfaces;

public interface IChanBotEvent extends IBotEvent, IChanEvent, IIrcEvent {

	public abstract String getCommandSource();

	public abstract String[] getBotCommand();

	public abstract boolean getIsPrivate();

	public abstract String toString();

	public IChannel getChannelSource();
	
	public String getEventCommandId();
}