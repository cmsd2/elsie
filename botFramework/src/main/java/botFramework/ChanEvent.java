package botFramework;

/**
 * @author sffubs
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */

import java.util.EventObject;

import botFramework.interfaces.IChanEvent;
import botFramework.interfaces.IChannel;
import botFramework.interfaces.IIrcMessage;

public class ChanEvent extends EventObject implements IChanEvent{
	private IIrcMessage msg;
	public ChanEvent(Object source, IIrcMessage msg) {
		super(source);
		
		this.msg = msg;
	}
	public String getBotCommandName()
	{
		return msg.getCommand();
	}
	public IChannel getChannelSource()
	{
		return (IChannel)super.getSource();
	}
	public IIrcMessage getIRCMessage() {
		return msg;
	}
	public String toString() {
		return msg.getPrefix() + "|" + msg.getCommand() + "|" + msg.getEscapedParams();
	}
}
