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

import botFramework.interfaces.IIRCEvent;
import botFramework.interfaces.IIRCMessage;


public class IRCEvent extends EventObject implements IIRCEvent {
	private IIRCMessage msg;
	public IRCEvent(Object source, IIRCMessage msg) {
		super(source);
		
		this.msg = msg;
	}
	/* (non-Javadoc)
	 * @see botFramework.IIRCEvent#getIRCMessage()
	 */
	public IIRCMessage getIRCMessage() {
		return msg;
	}
	public String toString() {
		return msg.getPrefix() + "|" + msg.getCommand() + "|" + msg.getEscapedParams();
	}
}