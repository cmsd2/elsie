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

public class ChanBotEvent extends EventObject implements IChanBotEvent {
	String cmdSource;
	String[] botCmd;
	boolean isPrivate;
	
	public ChanBotEvent(Object source, String cmdSource, String[] botCmd, boolean isPrivate) {
		super(source);
		
		this.cmdSource = cmdSource;
		this.botCmd = botCmd;
		this.isPrivate = isPrivate;
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
}
