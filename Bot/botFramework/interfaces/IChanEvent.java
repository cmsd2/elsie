package botFramework.interfaces;

public interface IChanEvent {
	public IIrcMessage getIRCMessage();
	
	public IChannel getChannelSource();
}