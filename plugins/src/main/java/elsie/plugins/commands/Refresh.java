package elsie.plugins.commands;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import elsie.plugins.AbstractPlugin;

import botFramework.interfaces.IChanBotEvent;
import botFramework.interfaces.IChannel;

public class Refresh extends AbstractPlugin {

	private static final Log log = LogFactory.getLog(Refresh.class);

	@Override
	protected boolean chanBotRespond(IChanBotEvent event) {
		
		IChannel chan = event.getChannelSource();
		
		log.info("Received chan bot event refresh");

		for(String user : chan.getUsers())
		{
			getUserFunctions().setStatus(user, chan, false);
		}
		
		return true;
	}

}
