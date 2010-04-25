package elsie;

/**
 * @author sffubs
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import botFramework.IrcProtocol;
import botFramework.interfaces.IBot;
import botFramework.interfaces.IChanBotEvent;
import botFramework.interfaces.IChanBotListener;
import botFramework.interfaces.IChanEvent;
import botFramework.interfaces.IChanListener;
import botFramework.interfaces.IChannel;
import botFramework.interfaces.IChannels;
import botFramework.interfaces.IDatabase;
import botFramework.interfaces.IIrcMessage;
import botFramework.interfaces.IIrcProtocol;
import botFramework.interfaces.IUser;
import botFramework.interfaces.IUserFunctions;

public class ChannelManager {
	private static final Log log = LogFactory.getLog(ChannelManager.class);

	private IBot bot;
	private IIrcProtocol irc;
	private IUserFunctions usr;
	private IChannels channels;
	
	private boolean angry;

	private IDatabase mysql;
	
	private Pattern regexLink;
	private PreparedStatement queryLinks;

	public ChannelManager() {
	}
	
	public void init()
	{	
		angry = false;

		try {
			queryLinks = mysql.getConnection().prepareStatement("SELECT DISTINCT `Nick`,`Description` FROM `transcript` WHERE `Channel`=? AND `Nick`!=? AND (`Event`=\"PRIVMSG\" OR `Event`=\"TOPIC\") AND `Description` LIKE ? AND (`Description` LIKE \"%http://%\" OR `Description` LIKE \"%ftp://%\" OR `Description` LIKE \"%https://%\" OR `Description` LIKE \"%www.%\") ORDER BY `DateTime` DESC LIMIT 5");
		}
		catch (SQLException e) {
			bot.sendErrorEvent("ChannelManager.ChannelManager","SQLException",e.getMessage());
		}
		regexCompile();
		
		irc = new IrcProtocol();
	}
	
	public IBot getBot()
	{
		return bot;
	}

	public void setBot(IBot bot)
	{
		this.bot = bot;
	}
	
	public IUserFunctions getUserFunctions()
	{
		return usr;
	}

	public void setUserFunctions(IUserFunctions usr)
	{
		this.usr = usr;
	}
	
	public IDatabase getDatabase()
	{
		return mysql;
	}

	public void setDatabase(IDatabase mysql)
	{
		this.mysql = mysql;
	}
	
	public IChannels getChannels()
	{
		return channels;
	}

	public void setChannels(IChannels channels)
	{
		if(this.channels != null)
		{
			log.info("Subscribing to chan bot events from channel group " + channels);
			this.channels.getChanBotEvents().remove(getChanBotListener());
			this.channels.getChanEvents().remove(getChanListener());
		}
		this.channels = channels;
		if(this.channels != null)
		{
			log.info("Unsubscribing from chan bot events from channel group " + channels);
			this.channels.getChanEvents().add(getChanListener());
			this.channels.getChanBotEvents().add(getChanBotListener());
		}
	}
	
	public IChanListener getChanListener()
	{
		return new IChanListener() {
			
			@Override
			public boolean respond(IChanEvent event) {
				ChannelManager.this.chanRespond(event);
				return true;
			}
		};
	}
	
	public IChanBotListener getChanBotListener()
	{
		return new IChanBotListener() {
			
			@Override
			public boolean respond(IChanBotEvent event) {
				return ChannelManager.this.chanBotRespond(event);
			}
		};
	}
	
	public void regexCompile() {
		regexLink = Pattern.compile(".*((http\\://|(^| )www\\.|ftp\\://|https\\://).*) ?.*");
	}
	
	public void chanRespond(IChanEvent event) {
		log.info("Received chan event " + event);

		IIrcMessage command = event.getIRCMessage();
		
		IChannel chan = event.getChannelSource();
		
		if (command.getCommand().compareTo("JOIN") == 0) {
			if (command.getPrefixNick().equals(bot.getNick()) == false) {
				log.info("Received chan JOIN event for user " + command.getPrefixNick());
				IUser user = chan.getUserStatus(command.getPrefixNick());
				String alias = usr.deAlias(command.getPrefixNick());
				log.debug("User " + command.getPrefixNick() + " dealiased to " + alias);
				String newAlias;
				if (usr.isUser(alias) == false) {
					log.info("User nick " + command.getPrefixNick() + " is not recognised. Checking ident " + user.getIdent());
					newAlias = usr.matchIdent(user.getIdent());
					if (newAlias.compareTo("") != 0) {
						log.info("Ident of user " + command.getPrefixNick() + " matched known user " + newAlias + ". adding new alias");
						usr.addAlias(newAlias,command.getPrefixNick());
					}
				}
			
				boolean successful = usr.setStatus(command.getPrefixNick(), chan, false);
				if (successful == false) {
					log.error("Failed to set userStatus for user " + command.getPrefixNick());
					bot.sendErrorEvent("ChannelManager.chanRespond(JOIN)","problem","Failed to set userStatus");
				}
				
				alias = usr.deAlias(command.getPrefixNick());
				log.debug("User " + command.getPrefixNick() + " dealiased to " + alias);
				if ((usr.isUser(alias) == false || usr.isRegisteredIdent(alias,user.getIdent()) == false)
						& angry == true
						& user.getIdent().matches(".*@.*\\.cam\\.ac\\.uk") == false
						& user.getIdent().matches(".*@131\\.111\\..*") == false) {
					log.info("Getting angry at unrecognised and unregistered user " + alias);
					bot.enqueueCommand(irc.ban("*!"+user.getIdent(),chan.getChannel()));
					bot.enqueueCommand(irc.kick(command.getPrefixNick(),chan.getChannel(),"I don't like you"));
				}
			}
		}
		else if (command.getCommand().compareTo("NICK") == 0) {
			log.info("Received chan NICK event for user " + command.getPrefixNick());
			boolean successful = false;
			String alias1 = usr.deAlias(command.getPrefixNick());
			String alias2 = usr.deAlias(command.getEscapedParams());
			IUser user = chan.getUserStatus(command.getEscapedParams());
			log.debug("User old nick " + command.getPrefixNick() + " dealiased to " + alias1);
			log.debug("User new nick " + command.getEscapedParams() + " dealiased to " + alias2);

			if (alias1.compareTo(alias2) != 0) {
				if (usr.isUser(alias1) && usr.isRegisteredIdent(alias1,user.getIdent())) {
					log.info("Adding new nick " + command.getEscapedParams() + " for recognised user " + alias1 + " ident " + user.getIdent());
					successful = usr.addAlias(alias1,command.getEscapedParams());
				}
				else if (usr.isUser(alias2) && usr.isRegisteredIdent(alias2,user.getIdent())) {
					log.info("Adding old nick " + command.getPrefixNick() + " for recognised user " + alias2 + " ident " + user.getIdent());
					successful = usr.addAlias(alias2,command.getPrefixNick());
					usr.setStatus(command.getEscapedParams(), chan, false);
				}
				
				if (successful == false) {
					log.error("Failed to add either alias " + alias1 + " or " + alias2 + " for user with ident " + user.getIdent());
					bot.sendErrorEvent("ChannelManager.chanRespond(NICK)","problem","Could not add alias");
				}
		
			}	
		}
		else if (command.getCommand().compareTo("311") == 0) {
			String nick = command.getParams()[1];
			log.info("Received chan 311 event for nick " + nick);
			usr.setStatus(nick, chan, false);
		}
		else if (command.getCommand().equals("353")) {
			if (!command.getPrefixNick().equalsIgnoreCase(bot.getNick())) {
				log.info("Received chan 353 event for nick " + command.getPrefixNick());
				int numUsers = chan.getNumUsers();
				Enumeration users = chan.getUsers();

				for (int i = 0; i < numUsers; i++) {
					usr.setStatus((String)users.nextElement(), chan, true);
				}
			}
		}
		else {
			log.info("Received chan event " + command + ". No action.");
		}
	}
	public boolean chanBotRespond(IChanBotEvent event) {
		String source = event.getCommandSource();
		String[] botCmd = event.getBotCommand();
		boolean isPrivate = event.getIsPrivate();
		boolean responded = false;
		
		IChannel chan = event.getChannelSource();
		String channel = chan.getChannel();
		
		IUser user = chan.getUserStatus(source);
		
		if (botCmd[0].equalsIgnoreCase("refresh")) {
			log.info("Received chan bot event refresh");
			responded = true;
			int numUsers = chan.getNumUsers();
			Enumeration users = chan.getUsers();

			for (int i = 0; i < numUsers; i++) {
				usr.setStatus((String)users.nextElement(), chan, false);
			}
		}
		else if (botCmd[0].equalsIgnoreCase("showhash")) {
			log.info("Received chan bot event showhash");
			responded = true;
			int numUsers = chan.getNumUsers();
			Enumeration users = chan.getUsers();
			String[] output = new String[4];
			IUser userInfo;
			
			for (int i = 0; i < numUsers; i++) {
				output[0] = chan.getChannel();
				
				output[1] = (String)users.nextElement();
				
				userInfo = chan.getUserStatus(output[1]);
				
				output[2] = userInfo.getIdent();
				output[3] = userInfo.getStatus();
				
				usr.botMessage(source, "showhash", output, isPrivate, chan, false);
			}
			
		}
		else if (botCmd[0].equalsIgnoreCase("rehash")) {
			log.info("Received chan bot event rehash");
			responded = true;
			chan.rehash();
		}
		else if (botCmd[0].equalsIgnoreCase("angry") & user.getStatus().compareTo("@") == 0) {
			log.info("Received chan bot event angry");
			responded = true;
			String[] replace = new String[1];
			replace[0] = source;
			if (angry == false) {
				log.info("Setting angry to on");
				usr.botMessage(chan.getChannel(),"angry_on",replace,false,chan,false);
				angry = true;
			}
			else if (angry == true) {
				log.info("Setting angry to off");
				usr.botMessage(chan.getChannel(),"angry_off",replace,false,chan,false);
				angry = false;
			}
		}
		else {
			log.info("Received chan bot event " + botCmd[0] + ". No action");
			responded = false;
			/*
			String temp = botCmd[0];

			for (int i = 1; i < botCmd.length; i++) {
				temp = temp + " " + botCmd[i];
			}
			String [] replace = {temp};
			usr.botMessage(source, "cmd_invalid", replace, isPrivate, chan);
			*/
		}
		return responded;
	}
	
}
