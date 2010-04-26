package botFramework.interfaces;

public interface IIrcEvent {

	public abstract IIrcMessage getIRCMessage();

	public abstract String getBotCommandName();
}