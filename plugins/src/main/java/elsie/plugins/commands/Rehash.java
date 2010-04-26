package elsie.plugins.commands;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import elsie.plugins.AbstractPlugin;

import botFramework.interfaces.IChanBotEvent;
import botFramework.interfaces.IChannel;

public class Rehash extends AbstractPlugin {

	private static final Log log = LogFactory.getLog(Rehash.class);

	@Override
	protected boolean chanBotRespond(IChanBotEvent event) {
		
		IChannel chan = event.getChannelSource();
		
		log.info("Received chan bot event rehash");
		chan.rehash();
		
		return true;
	}

}
