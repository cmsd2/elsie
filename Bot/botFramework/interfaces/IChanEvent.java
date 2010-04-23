package botFramework.interfaces;

public interface IChanEvent {
	public IIRCMessage getIRCMessage();
	
	public IChannel getChannelSource();
}