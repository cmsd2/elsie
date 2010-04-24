package elsie.plugins;

import elsie.util.attributes.Inject;
import botFramework.interfaces.IChanBotEvent;
import botFramework.interfaces.IChannel;
import botFramework.interfaces.IChannels;

public class PartChannel extends AbstractPlugin {
	
	private IChannels channels;
	
	public IChannels getChannels()
	{
		return channels;
	}
	
	@Inject
	public void setChannels(IChannels channels)
	{
		this.channels = channels;
	}
	
	public boolean chanBotRespond(IChanBotEvent event) {
		String[] cmd = event.getBotCommand();
		
		if(cmd.length != 2)
		{
		} else {
			String chanName = cmd[1];
			
			IChannel chan = getChannels().getChannel(chanName);
			
			chan.part();
			
			chan.setChannels(null);
			chan.setBot(null);
			
			return true;
		}
		return false;
	}
}
