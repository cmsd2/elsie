package botFramework.interfaces;

public interface IBot {

	public abstract void addChannel(IChannel c);

	public abstract void addIRCListener(IIRCListener l);

	public abstract void removeIRCListener(IIRCListener l);

	public abstract void sendIRCEvent(IIRCMessage msg);

	public abstract void addBotListener(IBotListener l);

	public abstract void removeBotListener(IBotListener l);

	public abstract void sendBotEvent(String source, String[] botCommand,
			boolean isPrivate);

	public abstract void addErrorListener(IErrorListener l);

	public abstract void removeErrorListener(IErrorListener l);

	public abstract void sendErrorEvent(String module, String type,
			String message);

	public abstract boolean send(String string);

	public abstract void enqueueCommand(String command);

	public abstract void sendCommands();

	public abstract void enqueueMessage(String target, String message,
			long delay);

	public abstract void sendMessages();
	
	public String getNick();

}