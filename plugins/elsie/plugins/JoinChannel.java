package elsie.plugins;

import botFramework.interfaces.IChanBotEvent;
import botFramework.interfaces.IChannel;

public class JoinChannel extends AbstractPlugin {
	public boolean chanBotRespond(IChanBotEvent event) {
		String[] cmd = event.getBotCommand();
		
		if(cmd.length != 2)
		{
		} else {
			String chan = cmd[1];
			
			IChannel newChan = getContext().getObject(IChannel.class);
			
			newChan.setChannel(chan);
			
			newChan.join();
			
			return true;
		}
		return false;
	}
}
