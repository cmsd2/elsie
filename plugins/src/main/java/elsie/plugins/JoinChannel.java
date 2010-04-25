package elsie.plugins;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import botFramework.interfaces.IChanBotEvent;
import botFramework.interfaces.IChannel;

public class JoinChannel extends AbstractPlugin {
	private static final Log log = LogFactory.getLog(JoinChannel.class);

	public boolean chanBotRespond(IChanBotEvent event) {
		log.info("Handling event " + event);
		String[] cmd = event.getBotCommand();
		
		if(cmd.length != 2)
		{
			log.error("At least 2 args required");
			return false;
		} else {
			String chan = cmd[1];
			
			log.info("Joining channel " + chan);
			
			IChannel newChan = (IChannel) getApplicationContext().getBean("channel");
			
			newChan.setChannel(chan);
			
			newChan.join();
			
			return true;
		}
	}
}
