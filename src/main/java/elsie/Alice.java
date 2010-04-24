package elsie;

/**
 * @author sffubs
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */

import botFramework.*;
import botFramework.interfaces.IChanBotEvent;
import botFramework.interfaces.IChanBotListener;
import botFramework.interfaces.IChanBotUnknownCmdListener;
import botFramework.interfaces.IChanEvent;
import botFramework.interfaces.IChanListener;
import botFramework.interfaces.IChannel;
import botFramework.interfaces.IIrcMessage;
import botFramework.interfaces.IUser;
import botFramework.interfaces.IUserFunctions;

import java.net.Socket;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.URLEncoder;
import java.util.Vector;
import java.util.TimerTask;
import java.util.Timer;

public class Alice {
	Bot bot;
	String server;
	String cgi;
	IUserFunctions usr;
	boolean enabled;
	boolean annoy;
	Vector chanBotCommands;
	Vector chanCommands;
	Timer replyQueue;
	
	public Alice (Bot bot, IUserFunctions usr, String server, String cgi) {
		this.bot = bot;
		this.usr = usr;
		this.server = server;
		this.cgi = cgi;
		enabled = false;
		annoy = false;
		
		replyQueue = new Timer();
	}
	
	public IChanListener getChanListener()
	{
		return new IChanListener() {
			
			@Override
			public boolean respond(IChanEvent event) {
				Alice.this.chanRespond(event);
				return true;
			}
		};
	}
	
	public IChanBotListener getChanBotListener()
	{
		return new IChanBotListener() {
			
			@Override
			public boolean respond(IChanBotEvent event) {
				return Alice.this.chanBotRespond(event);
			}
		};
	}
	
	public IChanBotUnknownCmdListener getChanBotUnknownCmdListener()
	{
		return new IChanBotUnknownCmdListener() {
			@Override
			public boolean respond(IChanBotEvent event) {
				Alice.this.chanBotUnknownCmdRespond(event);
				return true;
			}
		};
	}
	
	public boolean chanBotRespond(IChanBotEvent event) {
		String source = event.getCommandSource();
		String[] botCmd = event.getBotCommand();
		boolean isPrivate = event.getIsPrivate();
		boolean responded = false;
		
		IChannel chan = event.getChannelSource();
		
		IUser user = chan.getUserStatus(source);
		
		if (botCmd[0].equalsIgnoreCase("chat") & user.getStatus().equals("@")) {
			responded = true;
			
			if (enabled == false) {
				enabled = true;
				usr.botMessage(source,"alice_on",null,isPrivate,chan,false);
			}
			else {
				enabled = false;
				usr.botMessage(source,"alice_off",null,isPrivate,chan,false);
			}
		}
		else if (botCmd[0].equalsIgnoreCase("annoy") & user.getStatus().equals("@")) {
			responded = true;
			
			if (annoy == false) {
				annoy = true;
				usr.botMessage(source,"annoy_on",null,isPrivate,chan,false);
			}
			else {
				annoy = false;
				usr.botMessage(source,"annoy_off",null,isPrivate,chan,false);
			}
		}
		return responded;
	}
			
	public void chanRespond(IChanEvent e) {
		IIrcMessage msg = e.getIRCMessage();
		IChannel chan = e.getChannelSource();
		
		if ((msg.getCommand().equalsIgnoreCase("PRIVMSG") & !msg.isPrivate())
			| (msg.getCommand().equalsIgnoreCase("CTCP_ACTION") & !msg.isPrivate())) {
			String alias = usr.deAlias(msg.getPrefixNick());
			if (((!usr.isUser(alias) | !usr.isRegisteredIdent(alias, msg.getIdent()))
				& annoy
				& !msg.getPrefixNick().equalsIgnoreCase(bot.getNick())
				& !msg.getIdent().matches(".*@.*\\.cam\\.ac\\.uk")
				& !msg.getIdent().matches(".*@131\\.111\\..*")
				& !chan.getUserStatus(msg.getPrefixNick()).getStatus().equalsIgnoreCase("+")
				& !chan.getUserStatus(msg.getPrefixNick()).getStatus().equalsIgnoreCase("@")) |
				(usr.annoyUser(alias) & annoy)) {
				
				replyQueue.schedule(new Reply(chan,"",msg.getEscapedParams(),false),0);
				
				//String[] reply = new String[1];
				//reply[0] = reply(msg.escapedParams);
				//usr.botMessage(chan.getChannel(),"default",reply,false,chan,true);
			}
		}
		else if (msg.getCommand().equalsIgnoreCase("JOIN")) {
			String alias = usr.deAlias(msg.getPrefixNick());
			if ((!usr.isUser(alias) | !usr.isRegisteredIdent(alias, msg.getIdent()))
				& annoy				
				& !msg.getIdent().matches(".*@.*\\.cam\\.ac\\.uk")
				& !msg.getIdent().matches(".*@131\\.111\\..*")) {
					
				usr.botMessage(msg.getPrefixNick(),"annoy",null,false,chan,true);
			}
		}
	}
	
	
	public boolean chanBotUnknownCmdRespond (IChanBotEvent event) {
		
		if (enabled == true) {
			String[] botCmd = event.getBotCommand();
		
			String temp = botCmd[0];

			for (int i = 1; i < botCmd.length; i++) {
				temp = temp + " " + botCmd[i];
			}
		
			replyQueue.schedule(new Reply(event.getChannelSource(),event.getCommandSource(),temp,event.getIsPrivate()),0);
		
			//String received[] = new String[1];
			//received[0] = reply(temp);
			//if (received[0] == null) {
			//	return false;
			//}
			//else {
			//	usr.botMessage(event.getCommandSource(),"default",received,event.getIsPrivate(),(Channel)event.getSource(),true);
			//	return true;
			//}
			
			return true;
		}
		return false;
	}
		
	private class Reply extends TimerTask{
		String nick;
		String input;
		IChannel chan;
		boolean isPrivate;
		
		public Reply(IChannel chan, String nick, String input, boolean isPrivate) {
			this.input = input;
			this.nick = nick;
			this.chan = chan;
			this.isPrivate = isPrivate;
		}
		
		public void run() {
			String[] reply = new String[1];
			reply[0] = reply(input);
			if (nick.equalsIgnoreCase("")) {
				usr.botMessage(chan.getChannel(),"default",reply,isPrivate,chan,true);
			}
			else {
				usr.botMessage(nick,"default",reply,isPrivate,chan,true);
			}
		}
	}
	
	public Socket send(String string) {
		try {
			string = "input=" + URLEncoder.encode(string,"UTF-8");
 			Socket s = new Socket(server, 80);
  			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
  			out.write("POST "+ cgi + " HTTP/1.0\r\n");
  			out.write("Host: " + server + "\r\n");
  			out.write("User-Agent: Mozilla/4.0 (compatible; MSIE 6.0b; Windows 98)\r\n");
  			out.write("Accept: image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, image/png, image/tiff, multipart/x-mixed-replace, */*\r\n");
  			out.write("Accept-Charset: iso-8859-1, utf-8, iso-10646-ucs-2, macintosh, windows-1252, *\r\n");
  			out.write("Referer: " + server + cgi + "\r\n");
  			out.write("Content-type: application/x-www-form-urlencoded\r\n");
  			out.write("Content-length: " + string.length() + "\r\n");
  			out.write("\r\n");
  			out.write(string + "\r\n");
  			out.flush();
  			return s;
 		}
		catch (IOException e) {
  			bot.sendErrorEvent("Alice.send","IOException",e.getMessage());
  		}
  		return null;
	}
	
	public String receive(Socket s) {
		try {
			BufferedReader receiver = new BufferedReader(new InputStreamReader(s.getInputStream()));
			String line;
			
			int count = 0;
			do {
				count++;
				line = receiver.readLine();
			} while (!line.matches("o\\:.*") | count > 1000);
			
			if (count > 1000) {
				return null;
			}
			else {
				return line.substring(3);
			}
		}
		catch (IOException e) {
			bot.sendErrorEvent("Alice.receive","IOException",e.getMessage());
			return null;
		}
	}
	
	public String reply(String input) {
		Socket s = send(input);
		String received;
		if (s != null) {
			received = receive(s);
			if (received == null) {
				bot.sendErrorEvent("Alice.reply","problem","Could not receive answer.");
				return null;
			}
			else {
				return received;
			}
		}
		else {
			bot.sendErrorEvent("Alice.reply","problem","Could not send request.");
			return null;
		}
	}
			
}
