package botFramework.interfaces;

import java.util.Set;

public interface IBot {
	
	String ROLE = IBot.class.getName();

	public Set<IChannel> getChannels();
	
	public abstract IEventSource<IErrorEvent> getErrors();	
	public abstract IEventSource<IIrcEvent> getIrcEvents();
	public abstract IEventSource<IBotEvent> getBotEvents();

	public abstract void sendIRCEvent(IIrcMessage msg);

	public abstract void sendBotEvent(String source, String[] botCommand,
			boolean isPrivate);

	public abstract void sendErrorEvent(String module, String type,
			String message);

	public abstract boolean send(String string);

	public abstract void enqueueCommand(String command);

	public abstract void sendCommands();

	public abstract void enqueueMessage(String target, String message,
			long delay);

	public abstract void sendMessages();
	
	public String getNick();
	
	public String getHostname();

	public IIrcProtocol getIrc();
}