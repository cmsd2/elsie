package botFramework.interfaces;

import java.util.Enumeration;

public interface IChannel {

	public abstract void sendChanEvent(IIrcMessage msg);

	public abstract void sendChanBotEvent(String source, String[] botCommand,
			boolean isPrivate);
	
	public abstract boolean respondToChanEvent(IChanEvent event);
	
	public abstract boolean respondToIrcEvent(IIrcEvent event);

	public abstract void sendChanBotUnknownCmdEvent(String source,
			String[] botCommand, boolean isPrivate);

	public abstract void updateIdent(String user, String ident);

	public abstract void rehash();

	public abstract String getChannel();
	
	public abstract void setChannel(String channel);

	public abstract IUser getUserStatus(String user);

	public abstract int getNumUsers();

	public abstract Enumeration getUsers();

	public abstract void join();

	public abstract void part();
	
	public IChannels getChannels();
	public void setChannels(IChannels channels);
	public IBot getBot();
	public void setBot(IBot bot);

}