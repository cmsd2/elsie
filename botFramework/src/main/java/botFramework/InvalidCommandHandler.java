package botFramework;

/**
 * @author sffubs
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import botFramework.interfaces.IChanBotEvent;
import botFramework.interfaces.IChanBotUnknownCmdListener;
import botFramework.interfaces.IChannel;
import botFramework.interfaces.IChannels;
import botFramework.interfaces.IUserFunctions;

public class InvalidCommandHandler implements IChanBotUnknownCmdListener {
	private static final Log log = LogFactory.getLog(InvalidCommandHandler.class);

	private IUserFunctions usr;
	private IChannels channels;
	
	public InvalidCommandHandler() {
	}
	
	public IUserFunctions getUserFunctions()
	{
		return usr;
	}

	public void setUserFunctions(IUserFunctions usr)
	{
		this.usr = usr;
	}
	
	public IChannels getChannels()
	{
		return channels;
	}

	public void setChannels(IChannels channels)
	{
		if(this.channels != null)
		{
			log.info("Unsubscribing from chan bot events from channel group " + channels);
			this.channels.getUnknownCommandEvents().remove(this);
		}
		this.channels = channels;
		if(this.channels != null)
		{
			log.info("Subscribing to chan bot events from channel group " + channels);
			this.channels.getUnknownCommandEvents().add(this);
		}
	}

	public boolean respond(IChanBotEvent event) {
		log.info("Responding to event " + event);
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
