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
import botFramework.interfaces.IIRCMessage;

public class ChanEvent extends EventObject implements IChanEvent{
	private IIRCMessage msg;
	public ChanEvent(Object source, IIRCMessage msg) {
		super(source);
		
		this.msg = msg;
	}
	public IChannel getChannelSource()
	{
		return (IChannel)super.getSource();
	}
	public IIRCMessage getIRCMessage() {
		return msg;
	}
	public String toString() {
		return msg.getPrefix() + "|" + msg.getCommand() + "|" + msg.getEscapedParams();
	}
}
