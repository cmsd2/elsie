/**
 * @author sffubs
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */

/*
 * This is the bot.
 * Its job is to
 *  - maintain a connection to the server
 *  - respond to pings
 *  - spot nick collisions & take action
 *  - send IRCEvents
 *  - send BotEvents
 *  - manage public sender object for sending to server
 */
 
package botFramework;

import java.net.Socket;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.Vector;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.SocketTimeoutException;
import java.util.TimerTask;
import java.util.Timer;
import java.util.Date;
import java.text.DateFormat;
import java.util.Locale;

import botFramework.interfaces.IBot;
import botFramework.interfaces.IBotListener;
import botFramework.interfaces.IChannel;
import botFramework.interfaces.IErrorListener;
import botFramework.interfaces.IIRCEvent;
import botFramework.interfaces.IIRCListener;
import botFramework.interfaces.IIRCMessage;

public class Bot extends Thread implements IBot {
	private final String version = "0.63 2003-02-23";
	private final String description = "The Elsie Bot Framework";
	private final String environment;
	
	private final long pauseTime = 50;		//Time to sleep before checking for new messages
	
	private Vector nicks;
	private int currentNick;
	private String myNick;
	private Vector servers;
	private int currentServer;
	private int port;
	private int mode;
	private String hostname;
	private String realname;
	private int consecutiveErrors;
	
	private Socket connection;					//Server connection object
	private BufferedReader receiver;			//Input object
	public BufferedWriter sender;				//Output object

	private IRCProtocol irc;					//IRC protocol object

	//Listeners
	private Vector ircListeners;
	private Vector botListeners;
	private Vector errorListeners;
	private Vector channels;
	
	private Vector queuedCommands;
	private Vector queuedMessages;
	
	private Timer messageTimer;
	
	private DateFormat df;
	
	private String encoding;
	
	private class Message extends TimerTask {
		String target;
		String message;
		long delay;
		
		public Message(String target,String message,long delay) {
			this.target = target;
			this.message = message;
			this.delay = delay;
		}
		
		public void run() {
			String string = irc.privmsg(target,message);
			String nextString;
			int i;
			String[] params = new String[1];
			final int limit = 510 - (target.length() + myNick.length() + hostname.length() + 15);
			
			while (string.length() > limit) {
				i = string.lastIndexOf(" ",limit - 1);
				if (i <= limit-50) {
					nextString = string.substring(limit);
					string = string.substring(0,limit);
				}
				else {
					nextString = string.substring(i+1);
					string = string.substring(0,i);
				}
				enqueueCommand(string + "\n");
				string = irc.privmsg(target,nextString);
			}
			
			enqueueCommand(string);
			params[0] = target;
			boolean isPrivate;
			
			if (target.startsWith("#") | target.startsWith("&")
				| target.startsWith("!") | target.startsWith("+")) {
				isPrivate = false;
			}
			else {
				isPrivate = true;
			}
			
			if (message.matches("\001ACTION .*\001")) {
				sendIRCEvent(new IRCMessage("CTCP_ACTION",myNick,params,message.replaceAll("\001ACTION (.*)\001","$1"),myNick,"",isPrivate));
			}
			else {
				sendIRCEvent(new IRCMessage("PRIVMSG",myNick,params,message,myNick,"",isPrivate));
			}
		}
	}

	//Mode is the join mode on the server - i.e. invisible etc - see RFC
	public Bot(Vector nicks, Vector servers, int port, int mode, String realname, String encoding) {
		this.nicks = nicks;
		currentNick = 0;
		myNick = (String)nicks.elementAt(currentNick);
		this.servers = servers;
		currentServer = 0;
		this.port = port;
		this.realname = realname;
		this.mode = mode;
		this.encoding = encoding;
		
		environment = System.getProperty("os.name") + " "
			+ System.getProperty("os.version") + " "
			+ System.getProperty("os.arch") + " "
			+ "[Java " + System.getProperty("java.version") + "]";
			
		df = DateFormat.getDateTimeInstance(DateFormat.FULL,DateFormat.LONG,Locale.US);
		
		messageTimer = new Timer();
		
		consecutiveErrors = 0;
		
		try {
			InetAddress localhost = InetAddress.getLocalHost();
			hostname = localhost.getHostName();
		}
		catch (UnknownHostException e) {
			sendErrorEvent("Bot.Bot","UnknownHostException",e.getMessage());
			hostname = "localhost";
		}
		
		irc = new IRCProtocol();
		
		ircListeners = new Vector();
		botListeners = new Vector();
		errorListeners = new Vector();
		
		channels = new Vector();
		
		queuedCommands = new Vector();
		queuedMessages = new Vector();
		
		addIRCListener(getIrcListener());
	}
	
	public IIRCListener getIrcListener()
	{
		return new IIRCListener() {
			@Override
			public boolean respond(IIRCEvent event) {
				Bot.this.respondToIrcEvent(event);
				return true;
			}
		};
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IBot#addChannel(botFramework.Channel)
	 */
	public void addChannel(IChannel c) {
		if (channels.contains(c) == false) {
			channels.addElement(c);
		}
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IBot#addIRCListener(botFramework.interfaces.IIRCListener)
	 */
	public void addIRCListener(IIRCListener l) {
		if (ircListeners.contains(l) == false) {
			System.out.println("adding bot listener " + l);
			ircListeners.addElement(l);
		}
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IBot#removeIRCListener(botFramework.interfaces.IIRCListener)
	 */
	public void removeIRCListener(IIRCListener l) {
		if (ircListeners.contains(l)) {
			System.out.println("removing irc listener " + l);
			ircListeners.removeElement(l);
		}
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IBot#sendIRCEvent(botFramework.IRCMessage)
	 */
	public void sendIRCEvent(IIRCMessage msg) {
		for (int i = 0; i < ircListeners.size(); i++) {
			IIRCListener listener = (IIRCListener)ircListeners.elementAt(i);
			try {
				listener.respond(new IRCEvent(this, msg));
			}
			catch (Exception e) {
				sendErrorEvent("Bot.sendIRCEvent","Exception",e.getMessage());
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IBot#addBotListener(botFramework.BotListener)
	 */
	public void addBotListener(IBotListener l) {
		if (botListeners.contains(l) == false) {
			System.out.println("adding bot listener " + l);
			botListeners.addElement(l);
		}
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IBot#removeBotListener(botFramework.BotListener)
	 */
	public void removeBotListener(IBotListener l) {
		if (botListeners.contains(l)) {
			System.out.println("removing bot listener " + l);
			botListeners.removeElement(l);
		}
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IBot#sendBotEvent(java.lang.String, java.lang.String[], boolean)
	 */
	public void sendBotEvent(String source, String[] botCommand, boolean isPrivate) {
		for (int i = 0; i < botListeners.size(); i++) {
			IBotListener listener = (IBotListener)botListeners.elementAt(i);
			try {
				listener.respond(new BotEvent(this, source, botCommand, isPrivate));
			}
			catch (Exception e) {
				sendErrorEvent("Bot.sendBotEvent","Exception",e.getMessage());
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IBot#addErrorListener(botFramework.ErrorListener)
	 */
	public void addErrorListener(IErrorListener l) {
		if (errorListeners.contains(l) == false) {
			errorListeners.addElement(l);
		}
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IBot#removeErrorListener(botFramework.ErrorListener)
	 */
	public void removeErrorListener(IErrorListener l) {
		if (errorListeners.contains(l)) {
			errorListeners.removeElement(l);
		}
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IBot#sendErrorEvent(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void sendErrorEvent(String module, String type, String message) {
		for (int i = 0; i < errorListeners.size(); i++) {
			IErrorListener listener = (IErrorListener)errorListeners.elementAt(i);
			try {
				if (type.equalsIgnoreCase("problem")) {
					listener.problem(module,message);
				}
				else {
					listener.exception(module,type,message);
				}
			}
			catch (Exception e) {
				//Not using sendErrorEvent due to possible recursion.
				System.out.println("Exception in Bot.sendErrorEvent!");
			}
		}
	}
	
	public void run() {
		Object[] command;
		IRCMessage msg;
		
		long lastIRCEvent = System.currentTimeMillis();
		
		boolean successful = false;
		currentServer--;
		do {
			sendErrorEvent("Bot.run","problem","Connecting...");
			currentServer++;
			if (currentServer == servers.size()) {
				currentServer = 0;
			}
			successful = connect();
		} while (successful == false);
		
		while (true) {
			try {
				sleep(pauseTime);
				
				while (receiver.ready()) {
					command = receive();
				
					lastIRCEvent = System.currentTimeMillis();
					
					for (int i = 0; i < command.length; i++) {
						msg = (IRCMessage)command[i];
						sendIRCEvent(msg);
						if ((msg.getCommand().equalsIgnoreCase("PRIVMSG") & msg.getEscapedParams().matches(myNick + ":? +.*"))
							| (msg.getCommand().equalsIgnoreCase("PRIVMSG") & msg.isPrivate() & !msg.getPrefixNick().equalsIgnoreCase(myNick))) {
							String temp = msg.getEscapedParams().replaceFirst(myNick + ":? +","");
							String[] botCmd = temp.split(" +");
							sendBotEvent(msg.getPrefixNick(),botCmd,msg.isPrivate());
						}
						
						sendMessages();
						sendCommands();
					}
				}
				if (System.currentTimeMillis() - lastIRCEvent > 500000) {
					lastIRCEvent = System.currentTimeMillis();
					sendErrorEvent("Bot.run","problem","Timeout (no data for 500 seconds).");
					sendIRCEvent(new IRCMessage("QUIT", myNick + "!" + myNick + "@" + hostname
					, null, "Timeout (no data for 500 seconds).", myNick, myNick + "@" + hostname,false));
					reconnect("Timeout (no data for 500 seconds)");
				}
				
				sendMessages();
				sendCommands();
			}
			catch (SocketTimeoutException e) {
				sendErrorEvent("Bot.run","SocketTimeoutException",e.getMessage());
				reconnect("Read timeout.");
			}
			catch (IOException e) {
				sendErrorEvent("Bot.run","IOException",e.getMessage());
				reconnect("Read error.");
			}
			catch (InterruptedException e) {
				sendErrorEvent("Bot.run","InterruptedException",e.getMessage());
			}
		}
	}
	
	public synchronized boolean connect() {
		try {
			connection = new Socket((String)servers.elementAt(currentServer),port);
			connection.setSoTimeout(10000);
			receiver = new BufferedReader(new InputStreamReader(connection.getInputStream(),encoding));
			sender = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(),encoding));
		
			sender.write(irc.nick(myNick));
			sender.write(irc.user(myNick,mode,realname));
			sender.flush();
		
			for (int i = 0; i < channels.size(); i++) {
				IChannel c = (IChannel)channels.elementAt(i);
				c.join();
			}
		}
		catch (SocketTimeoutException e) {
			sendErrorEvent("Bot.connect","SocketTimeoutException",e.getMessage());
			return false;
		}
		catch (IOException e) {
			sendErrorEvent("Bot.connect","IOException",e.getMessage());
			return false;
		}
		return true;
	}

	public synchronized void disconnect(String reason) throws IOException {
		for (int i = 0; i < channels.size(); i++) {
			IChannel c = (IChannel)channels.elementAt(i);
			c.part();
		}
		
		sender.write(irc.quit(reason));
		sender.flush();
		
		sender.close();
		receiver.close();

		if (!connection.isClosed()) {
			connection.close();
		}
	}
	
	public synchronized void reconnect(String reason) {
		try {
			disconnect(reason);
		}
		catch (IOException e) {
		}
		
		boolean successful = false;
		currentServer--;
		do {
			sendErrorEvent("Bot.reconnect","problem","Reconnecting...");
			nextServer();
			successful = connect();
		} while (successful == false);
	}
	
	private synchronized void nextServer() {
		currentServer++;
		if (currentServer == servers.size()) {
			currentServer= 0;
		}
	}

	public Object[] receive() throws SocketTimeoutException,IOException {
		Object[] output;
		
		if (receiver.ready() == true) {
			String input = receiver.readLine();
			
			output = irc.parse(input,myNick);
			
			return output;
		}
		else {
			output = null;
			return output;
		}
		
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IBot#send(java.lang.String)
	 */
	public synchronized boolean send(String string) {
		try {
			sender.write(string);
		}
		catch(SocketTimeoutException e) {
			sendErrorEvent("Bot.send","SocketTimeoutException",e.getMessage());
			return false;
		}
		catch (IOException e) {
			sendErrorEvent("Bot.send","IOException",e.getMessage());
			return false;
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IBot#enqueueCommand(java.lang.String)
	 */
	public synchronized void enqueueCommand(String command) {
		queuedCommands.add(command);
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IBot#sendCommands()
	 */
	public void sendCommands() {
		String command;
		boolean success;
		while (!queuedCommands.isEmpty()) {
			command = (String)queuedCommands.remove(0);
			success = send(command);
			if (success == false) {
				queuedCommands.add(0,command);
				sendErrorEvent("Bot.sendCommands","problem","Could not send - reconnecting");
				reconnect("Write error!");
				return;
			}
		}
		try {
			sender.flush();
		}
		catch (SocketTimeoutException e) {
			sendErrorEvent("Bot.sendCommands","SocketTimeoutException",e.getMessage());
		}
		catch (IOException e) {
			sendErrorEvent("Bot.sendCommands","IOException",e.getMessage());
			reconnect("Write error!");
		}
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IBot#enqueueMessage(java.lang.String, java.lang.String, long)
	 */
	public synchronized void enqueueMessage(String target, String message, long delay) {
		queuedMessages.add(new Message(target,message,delay));
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IBot#sendMessages()
	 */
	public void sendMessages() {
		Message msg;
		boolean success;
		if (!queuedMessages.isEmpty()) {
			msg = (Message)queuedMessages.remove(0);
			messageTimer.schedule(msg,msg.delay);
		}
	}
				

	public void respondToIrcEvent(IIRCEvent event) {
		IIRCMessage command = event.getIRCMessage();
		if (command.getCommand().equalsIgnoreCase("PING")) {
			consecutiveErrors = 0;
			enqueueCommand(irc.pong(hostname));
		}
		else if (command.getCommand().equalsIgnoreCase("ERROR")) {
			consecutiveErrors++;
			if (consecutiveErrors > 1) {
				nextServer();
			}
			reconnect("IRC Error!");
		}
		else if (command.getCommand().equals("433")) {		//nick collision
			currentNick++;
			if (currentNick == nicks.size()) {
				currentNick = 0;
			}
			myNick = (String)nicks.elementAt(currentNick);
			enqueueCommand(irc.nick(myNick));
			for (int i = 0; i < channels.size(); i++) {
				IChannel c = (IChannel)channels.elementAt(i);
				c.join();
			}
		}
		else if (command.getCommand().equalsIgnoreCase("CTCP_VERSION")) {
			enqueueCommand(irc.ctcpVersion(command.getPrefixNick(),description, version, environment));
		}
		else if (command.getCommand().equalsIgnoreCase("CTCP_PING")) {
			enqueueCommand(irc.ctcpPing(command.getPrefixNick(),command.getEscapedParams()));
		}
		else if (command.getCommand().equalsIgnoreCase("CTCP_TIME")) {
			String dateTime = df.format(new Date(System.currentTimeMillis()));
			enqueueCommand(irc.ctcpTime(command.getPrefixNick(),dateTime));
		}
	}
	
	public String getNick() {
		return myNick;
	}
	
	public String getEncoding() {
		return encoding;
	}
}
