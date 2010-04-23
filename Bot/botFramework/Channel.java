package botFramework;

/**
 * @author sffubs
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */

/*
 * The job of the Channel object is to
 *  - join a channel
 *  - maintain the public userStatus hashtable containing
 *    the ident and status of each nick (User objects for each nick)
 *  - send ChanEvent events (events relevant to the channel)
 *  - send ChanBotEvent events (bot commands issued on this channel)
 *  - send ChanBotEvent to ChanBotUnknownCmdListener objects if nothing
 *    responds to a ChanBotEvent.
 * 
 * A Channel must be registered with the bot as a Channel to ensure auto-joining
 * on connect and reconnect.
 * 
 * Note that a PRIVATE command is considered relevant to the channel if
 * the user that issued it is on that channel. This can lead to multiple
 * channels responding to the same PRIVATE bot command.
 */

import java.util.Hashtable;
import java.io.IOException;
import java.util.Vector;
import java.util.Enumeration;

import elsie.InputConsole;

import botFramework.interfaces.IBot;
import botFramework.interfaces.IChanBotEvent;
import botFramework.interfaces.IChanBotListener;
import botFramework.interfaces.IChanBotUnknownCmdListener;
import botFramework.interfaces.IChanEvent;
import botFramework.interfaces.IChanListener;
import botFramework.interfaces.IChannel;
import botFramework.interfaces.IEventListener;
import botFramework.interfaces.IIrcEvent;
import botFramework.interfaces.IIrcListener;
import botFramework.interfaces.IIrcMessage;

public class Channel implements IChannel {
	private IBot bot;
	private String channel;
	
	private Hashtable userStatus;
	private IrcProtocol irc;
	
	private Vector<IEventListener<IChanEvent>> chanListeners;
	private Vector<IEventListener<IChanBotEvent>> chanBotListeners;
	private Vector chanBotUnknownCmdListeners;
	
	
	public Channel (String c) {
		channel = c;
		
		userStatus = new Hashtable();
		irc = new IrcProtocol();
		
		chanListeners = new Vector<IEventListener<IChanEvent>>();
		chanBotListeners = new Vector<IEventListener<IChanBotEvent>>();
		chanBotUnknownCmdListeners = new Vector();
		
		addChanListener(getChanEventListener());
	}
	
	public IChanListener getChanEventListener() {
		return new IChanListener() {
			@Override
			public boolean respond(IChanEvent event) {
				Channel.this.respondToChanEvent(event);
				return true;
			}
		};
	}
	
	public IIrcListener getIrcEventListener() {
		return new IIrcListener() {
			@Override
			public boolean respond(IIrcEvent event) {
				Channel.this.respondToIrcEvent(event);
				return true;
			}
		};
	}
	
	public IBot getBot()
	{
		return bot;
	}
	
	public void setBot(IBot bot)
	{
		if(this.bot != null)
		{
			this.bot.getChannels().remove(this);
		}
		this.bot = bot;
		if(this.bot != null)
		{
			this.bot.getChannels().add(this);
		}
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IChannel#addChanListener(botFramework.interfaces.IChanListener)
	 */
	public void addChanListener(IEventListener<IChanEvent> l) {
		if (chanListeners.contains(l) == false) {
			System.out.println("adding chan listener " + l);
			chanListeners.addElement(l);
		}
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IChannel#removeChanListener(botFramework.interfaces.IChanListener)
	 */
	public void removeChanListener(IEventListener<IChanEvent> l) {
		if (chanListeners.contains(l)) {
			System.out.println("removing chan listener " + l);
			chanListeners.removeElement(l);
		}
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IChannel#sendChanEvent(botFramework.IRCMessage)
	 */
	public void sendChanEvent(IIrcMessage msg) {
		for (int i = 0; i < chanListeners.size(); i++) {
			IEventListener<IChanEvent> listener = chanListeners.elementAt(i);
			listener.respond(new ChanEvent(this, msg));
		}
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IChannel#addChanBotListener(botFramework.interfaces.IChanBotListener)
	 */
	public void addChanBotListener(IEventListener<IChanBotEvent> l) {
		if (chanBotListeners.contains(l) == false) {
			System.out.println("adding chan bot listener " + l);
			chanBotListeners.addElement(l);
		}
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IChannel#removeChanBotListener(botFramework.interfaces.IChanBotListener)
	 */
	public void removeChanBotListener(IEventListener<IChanBotEvent> l) {
		if (chanBotListeners.contains(l)) {
			System.out.println("removing chan bot listener " + l);
			chanBotListeners.removeElement(l);
		}
	}	
	
	/* (non-Javadoc)
	 * @see botFramework.IChannel#sendChanBotEvent(java.lang.String, java.lang.String[], boolean)
	 */
	public void sendChanBotEvent(String source, String[] botCommand, boolean isPrivate) {
		boolean responded = false;
		for (int i = 0; i < chanBotListeners.size(); i++) {
			IEventListener<IChanBotEvent> listener = chanBotListeners.elementAt(i);
			try {
				responded = responded | listener.respond(new ChanBotEvent(this, source, botCommand, isPrivate));
			}
			catch (Exception e) {
				e.printStackTrace(System.err);
				bot.sendErrorEvent("Channel.sendChanBotEvent","Exception",e.getMessage());
			}
		}
		if (responded == false) {
			sendChanBotUnknownCmdEvent(source, botCommand, isPrivate);
		}
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IChannel#addChanBotUnknownCmdListener(botFramework.ChanBotUnknownCmdListener)
	 */
	public void addChanBotUnknownCmdListener(IChanBotUnknownCmdListener l) {
		if (chanBotUnknownCmdListeners.contains(l) == false) {
			System.out.println("adding chan bot unknown cmd listener " + l);
			chanBotUnknownCmdListeners.addElement(l);
		}
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IChannel#removeChanBotUnknownCmdListener(botFramework.ChanBotUnknownCmdListener)
	 */
	public void removeChanBotUnknownCmdListener(IChanBotUnknownCmdListener l) {
		if (chanBotUnknownCmdListeners.contains(l)) {
			System.out.println("removing chan bot unknown cmd listener " + l);
			chanBotUnknownCmdListeners.removeElement(l);
		}
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IChannel#sendChanBotUnknownCmdEvent(java.lang.String, java.lang.String[], boolean)
	 */
	public void sendChanBotUnknownCmdEvent(String source, String[] botCommand, boolean isPrivate) {
		for (int i = 0; i < chanBotUnknownCmdListeners.size(); i++) {
			IChanBotUnknownCmdListener listener = (IChanBotUnknownCmdListener)chanBotUnknownCmdListeners.elementAt(i);
			boolean success;
			try {
				success = listener.respond(new ChanBotEvent(this, source, botCommand, isPrivate));
				if (success) {
					return;
				}
			}
			catch (Exception e) {
				bot.sendErrorEvent("Channel.sendChanBotUnknownCmdEvent","Exception",e.getMessage());
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IChannel#ircRespond(botFramework.IRCEvent)
	 */
	public void respondToIrcEvent(IIrcEvent event) {
		IIrcMessage msg = event.getIRCMessage();
		
		if (msg.getEscapedParams() == null) {
			msg.setEscapedParams("");
		}
		
		if (msg.getPrefixNick() == null) {
			msg.setPrefixNick("");
		}
		
		if (msg.getParams() == null) {
			msg.setParams(new String[] { "" });
		}
		
		if (((msg.getCommand().equalsIgnoreCase("PRIVMSG") & msg.isPrivate())
			| msg.getCommand().equalsIgnoreCase("NICK")
			| msg.getCommand().equalsIgnoreCase("QUIT")) & userStatus.containsKey(msg.getPrefixNick())) {
				sendChanEvent(event.getIRCMessage());
		}
		
		if (msg.getParams().length >= 3) {
			if (msg.getCommand().equalsIgnoreCase("353") & msg.getParams()[2].equalsIgnoreCase(channel)) {
				sendChanEvent(event.getIRCMessage());
			}
		}		
		
		if (msg.getParams().length >= 2) {
			if (msg.getCommand().equals("311") & userStatus.containsKey(msg.getParams()[1])) {
				sendChanEvent(event.getIRCMessage());
			}
		}
		
		if (msg.getParams().length >= 1) {
			if ((msg.getCommand().equalsIgnoreCase("MODE")
				| msg.getCommand().equalsIgnoreCase("PRIVMSG")
				| msg.getCommand().equalsIgnoreCase("TOPIC")
				| msg.getCommand().equalsIgnoreCase("KICK")
				| msg.getCommand().equalsIgnoreCase("CTCP_ACTION"))
				&
				msg.getParams()[0].equalsIgnoreCase(channel)
				) {
			
				sendChanEvent(event.getIRCMessage());
			}
			else if ((msg.getCommand().equalsIgnoreCase("JOIN")
				| msg.getCommand().equalsIgnoreCase("PART"))	
				&
				(msg.getParams()[0].equalsIgnoreCase(channel)
				| msg.getEscapedParams().equalsIgnoreCase(channel)
				)) {
				
				sendChanEvent(event.getIRCMessage());
			}
		}
		
		if ((msg.getCommand().equalsIgnoreCase("PRIVMSG") & msg.getEscapedParams().matches(bot.getNick() + ":? +.*")
			& msg.getParams()[0].equalsIgnoreCase(channel))
			| (msg.getCommand().equalsIgnoreCase("PRIVMSG") & !msg.getPrefixNick().equalsIgnoreCase(bot.getNick()) & msg.isPrivate() & userStatus.containsKey(msg.getPrefixNick()))) {
			String temp = msg.getEscapedParams().replaceFirst(bot.getNick() + ":? +","");
			String[] botCmd = temp.split(" +");
			sendChanBotEvent(msg.getPrefixNick(),botCmd,msg.isPrivate());
		}
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IChannel#chanRespond(botFramework.interfaces.IChanEvent)
	 */
	public void respondToChanEvent(IChanEvent event) {
		IIrcMessage command = event.getIRCMessage();
		
		if (command.getCommand().equals("353")) {		//Names command; update user hashtable with current status.
			User temp = new User();
			String[] names = command.getEscapedParams().split("[ ]");
			String name;
			String status;

			for (int i = 0; i < names.length; i++) {
				status = null;
				status = names[i].substring(0,1);
				name = names[i].replaceFirst("[+|@]","");
				if (userStatus.containsKey(name) == false) {
					userStatus.put(name, new User("",status));
				}
				else {
					temp = (User)userStatus.get(name);
					if (temp.status.compareTo(status) != 0) {
						userStatus.remove(name);
						userStatus.put(name, new User(temp.ident,status));
					}
				}
			}
		}
		else if (command.getCommand().equalsIgnoreCase("MODE")) {
			bot.enqueueCommand(irc.names(channel));
			if (userStatus.containsKey(command.getPrefixNick())) {
				updateIdent(command.getPrefixNick(), command.getIdent());
			}
		}
		else if (command.getCommand().equalsIgnoreCase("PRIVMSG")) {
			if (userStatus.containsKey(command.getPrefixNick()) == true) {
				updateIdent(command.getPrefixNick(), command.getIdent());
			}
		}
		else if (command.getCommand().equals("311")) {
			String user = command.getParams()[1];
			String ident = command.getParams()[2] + "@" + command.getParams()[3];

			updateIdent(user,ident);
		}
		else if (command.getCommand().equalsIgnoreCase("JOIN")) {
			if (userStatus.containsKey(command.getPrefixNick()) == false) {
				userStatus.put(command.getPrefixNick(), new User(command.getIdent(),command.getPrefixNick().substring(1,2)));
			}
			else {
				bot.sendErrorEvent("Channel.ircRespond(JOIN)","problem","Hashtable corrupted - resetting");
				rehash();
				userStatus.put(command.getPrefixNick(), new User(command.getIdent(),command.getPrefixNick().substring(1,2)));
			}
		}
		else if (command.getCommand().equalsIgnoreCase("PART")) {
			if (userStatus.containsKey(command.getPrefixNick()) == true) {
				userStatus.remove(command.getPrefixNick());
			}
		}
		else if (command.getCommand().equalsIgnoreCase("QUIT")) {
			if (userStatus.containsKey(command.getPrefixNick()) == true) {
				userStatus.remove(command.getPrefixNick());
			}
		}
		else if (command.getCommand().equalsIgnoreCase("KICK")) {
			if (userStatus.containsKey(command.getParams()[1]) == true) {
				userStatus.remove(command.getParams()[1]);
			}
		}
		else if (command.getCommand().equalsIgnoreCase("NICK")) {
			User temp;
			if (userStatus.containsKey(command.getPrefixNick()) == true) {
				temp = (User)userStatus.get(command.getPrefixNick());
				userStatus.remove(command.getPrefixNick());
				userStatus.put(command.getEscapedParams(), new User(command.getIdent(), temp.status));
			}
			else {
				bot.sendErrorEvent("Channel.ircRespond(NICK)","problem","Hashtable corrupted - resetting");
				rehash();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IChannel#updateIdent(java.lang.String, java.lang.String)
	 */
	public void updateIdent(String user,String ident) {
		if (userStatus.containsKey(user) == true) {
			User temp = (User)userStatus.get(user);
			if (ident.compareTo(temp.ident) != 0) {
				userStatus.remove(user);
				userStatus.put(user,new User(ident,temp.status));
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IChannel#rehash()
	 */
	public void rehash() {
		userStatus.clear();
		bot.enqueueCommand(irc.names(channel));
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IChannel#getChannel()
	 */
	public String getChannel() {
		return channel;
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IChannel#getUserStatus(java.lang.String)
	 */
	public User getUserStatus(String user) {
		if (userStatus.containsKey(user)) {
			return (User)userStatus.get(user);
		}
		else {
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IChannel#getNumUsers()
	 */
	public int getNumUsers() {
		return userStatus.size();
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IChannel#getUsers()
	 */
	public Enumeration getUsers() {
		return userStatus.keys();
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IChannel#join()
	 */
	public void join() {
		bot.enqueueCommand(irc.join(channel));
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IChannel#part()
	 */
	public void part() {
		bot.enqueueCommand(irc.part(channel));
		userStatus.clear();
	}
}
