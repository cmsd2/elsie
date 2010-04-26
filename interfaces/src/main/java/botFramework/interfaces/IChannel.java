package botFramework.interfaces;

import java.util.Set;

public interface IChannel {
	
	String ROLE = IChannel.class.getName();

	public abstract void sendChanEvent(IIrcMessage msg);

	public abstract void sendChanBotEvent(String source, String[] botCommand,
			boolean isPrivate, IIrcMessage msg);
	
	public abstract boolean respondToChanEvent(IChanEvent event);
	
	public abstract boolean respondToIrcEvent(IIrcEvent event);

	public abstract void sendChanBotUnknownCmdEvent(String source,
			String[] botCommand, boolean isPrivate, IIrcMessage msg);

	public abstract void updateIdent(String user, String ident);

	public abstract void rehash();

	public abstract String getChannel();
	
	public abstract void setChannel(String channel);

	public abstract IUser getUserStatus(String user);

	public abstract int getNumUsers();

	public abstract Set<String> getUsers();

	public abstract void join();

	public abstract void part();
	
	public IChannels getChannels();
	public void setChannels(IChannels channels);
	public IBot getBot();
	public void setBot(IBot bot);

}