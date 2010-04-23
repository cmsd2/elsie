package elsie;

/**
 * @author sffubs
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */

import botFramework.interfaces.IChanBotEvent;
import botFramework.interfaces.IChanBotUnknownCmdListener;
import botFramework.interfaces.IChannel;
import botFramework.interfaces.IUserFunctions;

public class InvalidCommandHandler implements IChanBotUnknownCmdListener {
	IUserFunctions usr;
	
	public InvalidCommandHandler(IUserFunctions usr) {
		this.usr = usr;
	}
	public boolean respond(IChanBotEvent event) {
		String source = event.getCommandSource();
		String[] botCmd = event.getBotCommand();
		boolean isPrivate = event.getIsPrivate();

		IChannel chan = event.getChannelSource();
		
		String temp = botCmd[0];

		for (int i = 1; i < botCmd.length; i++) {
			temp = temp + " " + botCmd[i];
		}
		String [] replace = {temp};
		usr.botMessage(source, "cmd_invalid", replace, isPrivate, chan, false);
		return true;
	}
}
