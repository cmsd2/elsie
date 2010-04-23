package botFramework.interfaces;

import java.util.Enumeration;

public interface IChannel {

	public abstract void addChanListener(IEventListener<IChanEvent> l);

	public abstract void removeChanListener(IEventListener<IChanEvent> l);

	public abstract void sendChanEvent(IIrcMessage msg);

	public abstract void addChanBotListener(IEventListener<IChanBotEvent> l);

	public abstract void removeChanBotListener(IEventListener<IChanBotEvent> l);

	public abstract void sendChanBotEvent(String source, String[] botCommand,
			boolean isPrivate);
	
	public abstract void respondToChanEvent(IChanEvent event);
	
	public abstract void respondToIrcEvent(IIrcEvent event);

	public abstract void addChanBotUnknownCmdListener(
			IChanBotUnknownCmdListener l);

	public abstract void removeChanBotUnknownCmdListener(
			IChanBotUnknownCmdListener l);

	public abstract void sendChanBotUnknownCmdEvent(String source,
			String[] botCommand, boolean isPrivate);

	public abstract void updateIdent(String user, String ident);

	public abstract void rehash();

	public abstract String getChannel();

	public abstract IUser getUserStatus(String user);

	public abstract int getNumUsers();

	public abstract Enumeration getUsers();

	public abstract void join();

	public abstract void part();

}