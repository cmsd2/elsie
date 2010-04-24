package elsie;

/**
 * @author sffubs
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */

import elsie.util.attributes.Inject;
import botFramework.interfaces.IChanBotEvent;
import botFramework.interfaces.IChanBotUnknownCmdListener;
import botFramework.interfaces.IChannel;
import botFramework.interfaces.IChannels;
import botFramework.interfaces.IUserFunctions;

public class InvalidCommandHandler implements IChanBotUnknownCmdListener {
	private IUserFunctions usr;
	private IChannels channels;
	
	public InvalidCommandHandler() {
	}
	
	public IUserFunctions getUserFunctions()
	{
		return usr;
	}
	
	@Inject
	public void setUserFunctions(IUserFunctions usr)
	{
		this.usr = usr;
	}
	
	public IChannels getChannels()
	{
		return channels;
	}
	
	@Inject
	public void setChannels(IChannels channels)
	{
		if(this.channels != null)
		{
			this.channels.getUnknownCommandEvents().remove(this);
		}
		this.channels = channels;
		if(this.channels != null)
		{
			this.channels.getUnknownCommandEvents().add(this);
		}
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
