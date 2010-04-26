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

import botFramework.interfaces.IChanBotEvent;
import botFramework.interfaces.IChannel;
import botFramework.interfaces.IIrcMessage;

public class ChanBotEvent extends EventObject implements IChanBotEvent {
	String cmdSource;
	String[] botCmd;
	boolean isPrivate;
	private IIrcMessage msg;
	
	public ChanBotEvent(Object source, String cmdSource, String[] botCmd, boolean isPrivate, IIrcMessage msg) {
		super(source);
		
		this.cmdSource = cmdSource;
		this.botCmd = botCmd;
		this.isPrivate = isPrivate;
		this.msg = msg;
	}
	/* (non-Javadoc)
	 * @see botFramework.IChanBotEvent#getCommandSource()
	 */
	public String getCommandSource() {
		return cmdSource;
	}
	/* (non-Javadoc)
	 * @see botFramework.IChanBotEvent#getBotCommand()
	 */
	public String[] getBotCommand() {
		return botCmd;
	}
	
	public String getBotCommandName()
	{
		return botCmd[0];
	}
	/* (non-Javadoc)
	 * @see botFramework.IChanBotEvent#getIsPrivate()
	 */
	public boolean getIsPrivate() {
		return isPrivate;
	}
	/* (non-Javadoc)
	 * @see botFramework.IChanBotEvent#toString()
	 */
	public String toString() {
		return cmdSource + " " + botCmd[0];
	}
	
	public IChannel getChannelSource()
	{
		return (IChannel)super.getSource();
	}

	@Override
	public IIrcMessage getIRCMessage() {
		return msg;
	}
}
