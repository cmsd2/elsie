package elsie.plugins.commands;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import elsie.plugins.AbstractPlugin;

import botFramework.interfaces.IChanBotEvent;
import botFramework.interfaces.IChannel;
import botFramework.interfaces.IChannels;

public class PartChannel extends AbstractPlugin {

	private static final Log log = LogFactory.getLog(PartChannel.class);

	public IChannels getChannels()
	{
		return (IChannels) getApplicationContext().getBean("channels");
	}
	
	public boolean chanBotRespond(IChanBotEvent event) {
		log.info("Handling event " + event);

		String[] cmd = event.getBotCommand();
		
		if(cmd.length != 2)
		{
			log.error("At least 2 arguments required");
		} else {
			String chanName = cmd[1];
			
			log.info("Parting channel " + chanName);
			
			IChannel chan = getChannels().getChannel(chanName);
			
			chan.part();
			
			chan.setChannels(null);
			chan.setBot(null);
			
			return true;
		}
		return false;
	}
}
