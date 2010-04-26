package elsie.plugins.channel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import botFramework.IrcProtocol;
import botFramework.interfaces.IChanEvent;
import botFramework.interfaces.IChannel;
import botFramework.interfaces.IIrcMessage;
import botFramework.interfaces.IIrcProtocol;
import botFramework.interfaces.IUser;
import elsie.plugins.AbstractPlugin;

public class Join extends AbstractPlugin {

	private static final Log log = LogFactory.getLog(Join.class);

	private IIrcProtocol irc = new IrcProtocol();
	
	//command.getCommand().compareTo("JOIN") == 0
	@Override
	protected void chanRespond(IChanEvent event) {
		log.info("Received chan event " + event);

		IIrcMessage command = event.getIRCMessage();
		
		IChannel chan = event.getChannelSource();
		
		if (command.getPrefixNick().equals(getBot().getNick()) == false) {
			log.info("Received chan JOIN event for user " + command.getPrefixNick());
			IUser user = chan.getUserStatus(command.getPrefixNick());
			String alias = getUserFunctions().deAlias(command.getPrefixNick());
			log.debug("User " + command.getPrefixNick() + " dealiased to " + alias);
			String newAlias;
			if (getUserFunctions().isUser(alias) == false) {
				log.info("User nick " + command.getPrefixNick() + " is not recognised. Checking ident " + user.getIdent());
				newAlias = getUserFunctions().matchIdent(user.getIdent());
				if (newAlias.compareTo("") != 0) {
					log.info("Ident of user " + command.getPrefixNick() + " matched known user " + newAlias + ". adding new alias");
					getUserFunctions().addAlias(newAlias,command.getPrefixNick());
				}
			}
		
			boolean successful = getUserFunctions().setStatus(command.getPrefixNick(), chan, false);
			if (successful == false) {
				log.error("Failed to set userStatus for user " + command.getPrefixNick());
				getBot().sendErrorEvent("ChannelManager.chanRespond(JOIN)","problem","Failed to set userStatus");
			}
			
			alias = getUserFunctions().deAlias(command.getPrefixNick());
			log.debug("User " + command.getPrefixNick() + " dealiased to " + alias);
			if ((getUserFunctions().isUser(alias) == false || getUserFunctions().isRegisteredIdent(alias,user.getIdent()) == false)
					& getUserFunctions().isAngry() == true
					& user.getIdent().matches(".*@.*\\.cam\\.ac\\.uk") == false
					& user.getIdent().matches(".*@131\\.111\\..*") == false) {
				log.info("Getting angry at unrecognised and unregistered user " + alias);
				getBot().enqueueCommand(irc.ban("*!"+user.getIdent(),chan.getChannel()));
				getBot().enqueueCommand(irc.kick(command.getPrefixNick(),chan.getChannel(),"I don't like you"));
			}
		}
	}

}
