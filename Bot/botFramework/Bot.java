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
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.Date;
import java.text.DateFormat;
import java.util.Locale;

import botFramework.interfaces.IBot;
import botFramework.interfaces.IBotEvent;
import botFramework.interfaces.IBotListener;
import botFramework.interfaces.IChanBotListener;
import botFramework.interfaces.IChannel;
import botFramework.interfaces.IErrorListener;
import botFramework.interfaces.IEventSource;
import botFramework.interfaces.IIrcEvent;
import botFramework.interfaces.IIrcListener;
import botFramework.interfaces.IIrcMessage;
import botFramework.interfaces.IIrcProtocol;

public class Bot extends Thread implements IBot {
	private final String version = "0.63 2003-02-23";
	private final String description = "The Elsie Bot Framework";
	private final String environment;
	
	private final long pauseTime = 50;		//Time to sleep before checking for new messages
	
	private List<String> nicks;
	private int currentNick;
	private String myNick;
	private List<String> servers;
	private int currentServer;
	private int port;
	private int mode;
	private String hostname;
	private String realname;
	private int consecutiveErrors;
	
	private Socket connection;					//Server connection object
	private BufferedReader receiver;			//Input object
	private BufferedWriter sender;				//Output object

	private IrcProtocol irc;					//IRC protocol object

	//Listeners
	private Set<IIrcListener> ircListeners = new HashSet<IIrcListener>();
	private Set<IBotListener> botListeners = new HashSet<IBotListener>();
	private Set<IErrorListener> errorListeners = new HashSet<IErrorListener>();
	private Set<IChannel> channels = new HashSet<IChannel> ();
	private IEventSource<IIrcEvent> ircEventSource;
	private IEventSource<IErrorEvent> errorEventSource;
	private IEventSource<IBotEvent> botEventSource;
	
	private Deque<String> queuedCommands;
	private Deque<BotMessage> queuedMessages;
	
	private Timer messageTimer;
	
	private DateFormat df;
	
	private String encoding;
	
	//Mode is the join mode on the server - i.e. invisible etc - see RFC
	public Bot(List<String> nicks, List<String> servers, int port, int mode, String realname, String encoding) {
		this.nicks = nicks;
		currentNick = 0;
		myNick = (String)nicks.get(currentNick);
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
		
		errorEventSource = new EventSource<IErrorEvent>(this, null);
		ircEventSource = new EventSource<IIrcEvent>(this, errorEventSource);
		botEventSource = new EventSource<IBotEvent>(this, errorEventSource);
		
		try {
			InetAddress localhost = InetAddress.getLocalHost();
			hostname = localhost.getHostName();
		}
		catch (UnknownHostException e) {
			sendErrorEvent("Bot.Bot","UnknownHostException",e.getMessage());
			hostname = "localhost";
		}
		
		irc = new IrcProtocol();
		
		queuedCommands = new LinkedList<String> ();
		queuedMessages = new LinkedList<BotMessage>();
		
		ircEventSource.add(getIrcListener());
	}
	
	private IIrcListener ircListener = null;
	public IIrcListener getIrcListener()
	{
		if(ircListener == null)
		{
			ircListener = new IIrcListener() {
				@Override
				public boolean respond(IIrcEvent event) {
					Bot.this.respondToIrcEvent(event);
					return true;
				}
			};
		}
		return ircListener;
	}
	
	public Set<IChannel> getChannels()
	{
		return channels;
	}
	
	public IEventSource<IIrcEvent> getIrcEvents()
	{
		return this.ircEventSource;
	}
	
	public IEventSource<IErrorEvent> getErrors()
	{
		return errorEventSource;
	}
	
	public IEventSource<IBotEvent> getBotEvents()
	{
		return botEventSource;
	}

	public void sendIRCEvent(IIrcMessage msg) {
		IIrcEvent event = new IrcEvent(this, msg);
		
		ircEventSource.sendEvent("Bot.sendIrcEvent", event);
	}

	public void sendBotEvent(String source, String[] botCommand, boolean isPrivate) {
		IBotEvent event = new BotEvent(this, source, botCommand, isPrivate);

		botEventSource.sendEvent("Bot.sendBotEvent", event);
	}

	public void sendErrorEvent(String module, String type, String message) {
		ErrorEvent event = new ErrorEvent(this, module, type, message);

		errorEventSource.sendEvent(module, event);
	}
	
	public void run() {
		Object[] command;
		IrcMessage msg;
		
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
						msg = (IrcMessage)command[i];
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
					sendIRCEvent(new IrcMessage("QUIT", myNick + "!" + myNick + "@" + hostname
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
			connection = new Socket((String)servers.get(currentServer),port);
			connection.setSoTimeout(10000);
			receiver = new BufferedReader(new InputStreamReader(connection.getInputStream(),encoding));
			sender = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(),encoding));
		
			sender.write(irc.nick(myNick));
			sender.write(irc.user(myNick,mode,realname));
			sender.flush();
		
			for (IChannel c: channels) {
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
		for(IChannel c: channels)
		{
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
			command = queuedCommands.removeFirst();
			success = send(command);
			if (success == false) {
				queuedCommands.addFirst(command);
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
		queuedMessages.add(new BotMessage(this, target,message,delay));
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IBot#sendMessages()
	 */
	public void sendMessages() {
		BotMessage msg;
		boolean success;
		if (!queuedMessages.isEmpty()) {
			msg = queuedMessages.removeFirst();
			messageTimer.schedule(msg,msg.getDelay());
		}
	}
				

	public void respondToIrcEvent(IIrcEvent event) {
		IIrcMessage command = event.getIRCMessage();
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
			myNick = (String)nicks.get(currentNick);
			enqueueCommand(irc.nick(myNick));
			for(IChannel c: channels)
			{
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
	
	public IIrcProtocol getIrc()
	{
		return irc;
	}
	
	public String getNick() {
		return myNick;
	}
	
	public String getEncoding() {
		return encoding;
	}
	
	public String getHostname() {
		return hostname;
	}
}
