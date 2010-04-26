package elsie.plugins.channel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import botFramework.interfaces.IChanEvent;
import botFramework.interfaces.IChannel;
import botFramework.interfaces.IIrcMessage;
import elsie.plugins.AbstractPlugin;

public class Irc311 extends AbstractPlugin {

	private static final Log log = LogFactory.getLog(Irc311.class);

	//command.getCommand().equals("311")
	@Override
	protected void chanRespond(IChanEvent event) {
		log.info("Received chan event " + event);

		IIrcMessage command = event.getIRCMessage();
		
		IChannel chan = event.getChannelSource();

		String nick = command.getParams()[1];
		log.info("Received chan 311 event for nick " + nick);
		getUserFunctions().setStatus(nick, chan, false);
	}

}
