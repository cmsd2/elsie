package elsie.plugins.commands;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import elsie.plugins.AbstractPlugin;

import botFramework.interfaces.IChanBotEvent;
import botFramework.interfaces.IChannel;
import botFramework.interfaces.IUser;

public class Angry extends AbstractPlugin {

	private static final Log log = LogFactory.getLog(Angry.class);

	@Override
	protected boolean chanBotRespond(IChanBotEvent event) {
		String source = event.getCommandSource();
		
		IChannel chan = event.getChannelSource();
		
		IUser user = chan.getUserStatus(source);
		
		if (user.getStatus().compareTo("@") == 0) {
			log.info("Received chan bot event angry");

			boolean angry = getUserFunctions().isAngry();
			
			String[] replace = new String[1];
			replace[0] = source;
			if (!angry) {
				log.info("Setting angry to on");
				getUserFunctions().botMessage(chan.getChannel(),"angry_on",replace,false,chan,false);
				
				angry = true;
			} else {
				log.info("Setting angry to off");
				getUserFunctions().botMessage(chan.getChannel(),"angry_off",replace,false,chan,false);
				
				angry = false;
			}
			
			getUserFunctions().setAngry(angry);
		}
		
		return true;
	}

}
