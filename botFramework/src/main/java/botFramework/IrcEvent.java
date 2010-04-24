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

import botFramework.interfaces.IIrcEvent;
import botFramework.interfaces.IIrcMessage;


public class IrcEvent extends EventObject implements IIrcEvent {
	private IIrcMessage msg;
	public IrcEvent(Object source, IIrcMessage msg) {
		super(source);
		
		this.msg = msg;
	}
	/* (non-Javadoc)
	 * @see botFramework.IIRCEvent#getIRCMessage()
	 */
	public IIrcMessage getIRCMessage() {
		return msg;
	}
	public String toString() {
		return msg.getPrefix() + "|" + msg.getCommand() + "|" + msg.getEscapedParams();
	}
}