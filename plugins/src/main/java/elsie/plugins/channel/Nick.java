package elsie.plugins.channel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import botFramework.interfaces.IChanEvent;
import botFramework.interfaces.IChannel;
import botFramework.interfaces.IIrcMessage;
import botFramework.interfaces.IUser;
import elsie.plugins.AbstractPlugin;

public class Nick extends AbstractPlugin {

	private static final Log log = LogFactory.getLog(Nick.class);

	//command.getCommand().compareTo("NICK") == 0
	@Override
	protected void chanRespond(IChanEvent event) {
		log.info("Received chan event " + event);

		IIrcMessage command = event.getIRCMessage();
		
		IChannel chan = event.getChannelSource();
		
		log.info("Received chan NICK event for user " + command.getPrefixNick());
		boolean successful = false;
		String alias1 = getUserFunctions().deAlias(command.getPrefixNick());
		String alias2 = getUserFunctions().deAlias(command.getEscapedParams());
		IUser user = chan.getUserStatus(command.getEscapedParams());
		log.debug("User old nick " + command.getPrefixNick() + " dealiased to " + alias1);
		log.debug("User new nick " + command.getEscapedParams() + " dealiased to " + alias2);

		if (alias1.compareTo(alias2) != 0) {
			if (getUserFunctions().isUser(alias1) && getUserFunctions().isRegisteredIdent(alias1,user.getIdent())) {
				log.info("Adding new nick " + command.getEscapedParams() + " for recognised user " + alias1 + " ident " + user.getIdent());
				successful = getUserFunctions().addAlias(alias1,command.getEscapedParams());
			}
			else if (getUserFunctions().isUser(alias2) && getUserFunctions().isRegisteredIdent(alias2,user.getIdent())) {
				log.info("Adding old nick " + command.getPrefixNick() + " for recognised user " + alias2 + " ident " + user.getIdent());
				successful = getUserFunctions().addAlias(alias2,command.getPrefixNick());
				getUserFunctions().setStatus(command.getEscapedParams(), chan, false);
			}
			
			if (successful == false) {
				log.error("Failed to add either alias " + alias1 + " or " + alias2 + " for user with ident " + user.getIdent());
				getBot().sendErrorEvent("ChannelManager.chanRespond(NICK)","problem","Could not add alias");
			}
	
		}	
	}

}
