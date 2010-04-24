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

import botFramework.interfaces.IBotEvent;

public class BotEvent extends EventObject implements IBotEvent {
	private String cmdSource;
	private String[] botCmd;
	private boolean isPrivate;
	
	public BotEvent(Object source, String cmdSource, String[] botCmd, boolean isPrivate) {
		super(source);
		
		this.cmdSource = cmdSource;
		this.botCmd = botCmd;
		this.isPrivate = isPrivate;
	}
	/* (non-Javadoc)
	 * @see botFramework.IBotEvent#getCommandSource()
	 */
	public String getCommandSource() {
		return cmdSource;
	}
	/* (non-Javadoc)
	 * @see botFramework.IBotEvent#getBotCommand()
	 */
	public String[] getBotCommand() {
		return botCmd;
	}
	/* (non-Javadoc)
	 * @see botFramework.IBotEvent#getIsPrivate()
	 */
	public boolean getIsPrivate() {
		return isPrivate;
	}
	public String toString() {
		return cmdSource + " " + botCmd[0];
	}
}
