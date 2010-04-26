package elsie.plugins.channel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import botFramework.interfaces.IChanEvent;
import botFramework.interfaces.IChannel;
import botFramework.interfaces.IIrcMessage;
import elsie.plugins.AbstractPlugin;

public class Irc353 extends AbstractPlugin {

	private static final Log log = LogFactory.getLog(Irc353.class);

	//command.getCommand().equals("353")
	@Override
	protected void chanRespond(IChanEvent event) {
		log.info("Received chan event " + event);

		IIrcMessage command = event.getIRCMessage();
		
		IChannel chan = event.getChannelSource();
		
		if (!command.getPrefixNick().equalsIgnoreCase(getBot().getNick())) {
			log.info("Received chan 353 event for nick " + command.getPrefixNick());

			for(String userName : chan.getUsers())
			{
				getUserFunctions().setStatus(userName, chan, true);
			}
		}
	}

}
