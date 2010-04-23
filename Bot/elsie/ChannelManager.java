package elsie;

/**
 * @author sffubs
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Enumeration;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

import botFramework.*;
import botFramework.interfaces.IChanBotEvent;
import botFramework.interfaces.IChanBotListener;
import botFramework.interfaces.IChanEvent;
import botFramework.interfaces.IChanListener;
import botFramework.interfaces.IChannel;
import botFramework.interfaces.IIRCMessage;
import botFramework.interfaces.IUser;
import botFramework.interfaces.IUserFunctions;

public class ChannelManager {
	Bot bot;
	IRCProtocol irc;
	IUserFunctions usr;
	
	boolean angry;

	DBHandler mysql;
	
	Pattern regexLink;
	PreparedStatement queryLinks;

	public ChannelManager(Bot b, DBHandler m, IUserFunctions usr) {
		bot = b;
		mysql = m;
		this.usr = usr;
		
		angry = false;

		try {
			queryLinks = queryLinks = mysql.db.prepareStatement("SELECT DISTINCT `Nick`,`Description` FROM `transcript` WHERE `Channel`=? AND `Nick`!=? AND (`Event`=\"PRIVMSG\" OR `Event`=\"TOPIC\") AND `Description` LIKE ? AND (`Description` LIKE \"%http://%\" OR `Description` LIKE \"%ftp://%\" OR `Description` LIKE \"%https://%\" OR `Description` LIKE \"%www.%\") ORDER BY `DateTime` DESC LIMIT 5");
		}
		catch (SQLException e) {
			bot.sendErrorEvent("ChannelManager.ChannelManager","SQLException",e.getMessage());
		}
		regexCompile();
		
		irc = new IRCProtocol();
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
		IIRCMessage command = event.getIRCMessage();
		
		IChannel chan = event.getChannelSource();
		
		if (command.getCommand().compareTo("JOIN") == 0) {
			if (command.getPrefixNick().equals(bot.getNick()) == false) {
				IUser user = chan.getUserStatus(command.getPrefixNick());
				String alias = usr.deAlias(command.getPrefixNick());
				String newAlias;
				if (usr.isUser(alias) == false) {
					newAlias = usr.matchIdent(user.getIdent());
					if (newAlias.compareTo("") != 0) {
						usr.addAlias(newAlias,command.getPrefixNick());
					}
				}
			
				boolean successful = usr.setStatus(command.getPrefixNick(), chan, false);
				if (successful == false) {
					bot.sendErrorEvent("ChannelManager.chanRespond(JOIN)","problem","Failed to set userStatus");
				}
				
				alias = usr.deAlias(command.getPrefixNick());
				if ((usr.isUser(alias) == false | usr.isRegisteredIdent(alias,user.getIdent()) == false)
						& angry == true
						& user.getIdent().matches(".*@.*\\.cam\\.ac\\.uk") == false
						& user.getIdent().matches(".*@131\\.111\\..*") == false) {
					
					bot.enqueueCommand(irc.ban("*!"+user.getIdent(),chan.getChannel()));
					bot.enqueueCommand(irc.kick(command.getPrefixNick(),chan.getChannel(),"I don't like you"));
				}
			}
		}
		else if (command.getCommand().compareTo("NICK") == 0) {
			boolean successful = false;
			String alias1 = usr.deAlias(command.getPrefixNick());
			String alias2 = usr.deAlias(command.getEscapedParams());
			IUser user = chan.getUserStatus(command.getEscapedParams());

			if (alias1.compareTo(alias2) != 0) {
				if (usr.isUser(alias1) & usr.isRegisteredIdent(alias1,user.getIdent())) {
					successful = usr.addAlias(alias1,command.getEscapedParams());
				}
				else if (usr.isUser(alias2) & usr.isRegisteredIdent(alias2,user.getIdent())) {
					successful = usr.addAlias(alias2,command.getPrefixNick());
					usr.setStatus(command.getEscapedParams(), chan, false);
				}
				
				if (successful == false) {
					bot.sendErrorEvent("ChannelManager.chanRespond(NICK)","problem","Could not add alias");
				}
		
			}	
		}
		else if (command.getCommand().compareTo("311") == 0) {
			String nick = command.getParams()[1];
			usr.setStatus(nick, chan, false);
		}
		else if (command.getCommand().equals("353")) {
			if (!command.getPrefixNick().equalsIgnoreCase(bot.getNick())) {	
				int numUsers = chan.getNumUsers();
				Enumeration users = chan.getUsers();

				for (int i = 0; i < numUsers; i++) {
					usr.setStatus((String)users.nextElement(), chan, true);
				}
			}
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
			responded = true;
			int numUsers = chan.getNumUsers();
			Enumeration users = chan.getUsers();

			for (int i = 0; i < numUsers; i++) {
				usr.setStatus((String)users.nextElement(), chan, false);
			}
		}
		else if (botCmd[0].equalsIgnoreCase("showhash")) {
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
			responded = true;
			chan.rehash();
		}
		else if (botCmd[0].equalsIgnoreCase("angry") & user.getStatus().compareTo("@") == 0) {
			responded = true;
			String[] replace = new String[1];
			replace[0] = source;
			if (angry == false) {
				usr.botMessage(chan.getChannel(),"angry_on",replace,false,chan,false);
				angry = true;
			}
			else if (angry == true) {
				usr.botMessage(chan.getChannel(),"angry_off",replace,false,chan,false);
				angry = false;
			}
		}
		else {
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
