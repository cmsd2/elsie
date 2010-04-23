package botFramework.interfaces;

public interface IChanBotEvent extends IBotEvent {

	public abstract String getCommandSource();

	public abstract String[] getBotCommand();

	public abstract boolean getIsPrivate();

	public abstract String toString();

	public IChannel getChannelSource();
}