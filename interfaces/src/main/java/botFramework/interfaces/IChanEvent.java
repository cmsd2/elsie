package botFramework.interfaces;

public interface IChanEvent extends IIrcEvent {
	public IIrcMessage getIRCMessage();
	
	public IChannel getChannelSource();
}