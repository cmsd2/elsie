package botFramework;

import botFramework.interfaces.IIrcMessage;

public class UnknownCommandChanBotEvent extends ChanBotEvent {

	public UnknownCommandChanBotEvent(Object source, String cmdSource,
			String[] botCmd, boolean isPrivate, IIrcMessage msg) {
		super(source, cmdSource, botCmd, isPrivate, msg);
	}
	
	public String getEventCommandId()
	{
		return "unknown";
	}

	public String toString()
	{
		return "unknownCmd(" + super.toString() + ")";
	}
}
