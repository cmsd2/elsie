package botFramework.interfaces;

public interface IBotEvent {

	public abstract String getCommandSource();

	public abstract String[] getBotCommand();

	public abstract boolean getIsPrivate();

}