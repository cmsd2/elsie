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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import botFramework.interfaces.IBot;
import botFramework.interfaces.IBotEvent;
import botFramework.interfaces.IChanBotEvent;
import botFramework.interfaces.IChanEvent;
import botFramework.interfaces.IChanListener;
import botFramework.interfaces.IChannel;
import botFramework.interfaces.IChannels;
import botFramework.interfaces.IErrorEvent;
import botFramework.interfaces.IEventSink;
import botFramework.interfaces.IEventSource;
import botFramework.interfaces.IIrcEvent;
import botFramework.interfaces.IIrcListener;
import botFramework.interfaces.IIrcMessage;
import elsie.util.IrcProtocol;

public class Channel implements IChannel, IEventSink {
	private static final Log log = LogFactory.getLog(Channel.class);

	private IBot bot;
	private IChannels channels;
	
	private String channel;
	
	private Map<String,User> userStatus = new HashMap<String, User>();
	private IrcProtocol irc = new IrcProtocol();
	
	private IEventSource<IErrorEvent> errorEvents;
	private IEventSource<IChanEvent> chanEvents;
	private IEventSource<IChanBotEvent> chanBotEvents;
	private IEventSource<IChanBotEvent> unknownCommandEvents;
	
	private EventBridge eventHandler;
	
	public Channel () {
		eventHandler = new EventBridge(this);
		errorEvents = new EventSource<IErrorEvent> (this, null);
		chanEvents = new EventSource<IChanEvent> (this, errorEvents);
		chanBotEvents = new EventSource<IChanBotEvent> (this, errorEvents);
		unknownCommandEvents = new EventSource<IChanBotEvent> (this, errorEvents);
		
		chanEvents.add(getChanListener());
	}
	
	public String getChannel()
	{
		return channel;
	}
	
	public void setChannel(String name)
	{
		this.channel = name;
	}

	public void init()
	{
	}

	public IChanListener getChanListener() {
		return eventHandler.getChanListener();
	}

	public IIrcListener getIrcListener() {
		return eventHandler.getIrcListener();
	}
	
	public IChannels getChannels()
	{
		return channels;
	}

	public void setChannels(IChannels channels)
	{
		if(this.channels != null)
		{
			chanEvents.remove(this.channels.getChanListener());
			chanBotEvents.remove(this.channels.getChanBotListener());
			unknownCommandEvents.remove(this.channels.getUnknownCommandListener());
			this.channels.removeChannel(this);
		}
		this.channels = channels;
		if(this.channels != null)
		{
			chanEvents.add(this.channels.getChanListener());
			chanBotEvents.add(this.channels.getChanBotListener());
			unknownCommandEvents.add(this.channels.getUnknownCommandListener());
			this.channels.addChannel(this);
		}
	}
	
	public IBot getBot()
	{
		return bot;
	}

	public void setBot(IBot bot)
	{
		if(this.bot != null)
		{
			this.bot.getIrcEvents().remove(this.getIrcListener());
			this.bot.getChannels().remove(this);
		}
		this.bot = bot;
		if(this.bot != null)
		{
			this.bot.getChannels().add(this);
			this.bot.getIrcEvents().add(this.getIrcListener());
		}
	}
	
	public IEventSource<IErrorEvent> getErrors()
	{
		return errorEvents;
	}
	
	public IEventSource<IChanEvent> getChanEvents()
	{
		return chanEvents;
	}
	
	public IEventSource<IChanBotEvent> getChanBotEvents()
	{
		return chanBotEvents;
	}
	
	public IEventSource<IChanBotEvent> getUnknownCommandEvents()
	{
		return unknownCommandEvents;
	}

	public void sendChanEvent(IIrcMessage msg) {
		chanEvents.sendEvent("Channel.sendChanEvent", new ChanEvent(this, msg));
	}

	public void sendChanBotEvent(String source, String[] botCommand, boolean isPrivate, IIrcMessage msg) {

		boolean responded = chanBotEvents.sendEvent("Channel.sendChanBotEvent", new ChanBotEvent(this, source, botCommand, isPrivate, msg));
		
		if (responded == false) {
			sendChanBotUnknownCmdEvent(source, botCommand, isPrivate, msg);
		}
	}

	/* (non-Javadoc)
	 * @see botFramework.IChannel#sendChanBotUnknownCmdEvent(java.lang.String, java.lang.String[], boolean)
	 */
	public void sendChanBotUnknownCmdEvent(String source, String[] botCommand, boolean isPrivate, IIrcMessage msg) {
		IChanBotEvent event = new UnknownCommandChanBotEvent(this, source, botCommand, isPrivate, msg);
		
		unknownCommandEvents.sendEvent("Channel.sendChanBotUnknownCmdEvent", event);
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IChannel#ircRespond(botFramework.IRCEvent)
	 */
	public boolean respondToIrcEvent(IIrcEvent event) {
		log.debug("Responding to IRC event " + event);

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
			|| msg.getCommand().equalsIgnoreCase("NICK")
			|| msg.getCommand().equalsIgnoreCase("QUIT")) & userStatus.containsKey(msg.getPrefixNick())) {
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
				|| msg.getCommand().equalsIgnoreCase("PRIVMSG")
				|| msg.getCommand().equalsIgnoreCase("TOPIC")
				|| msg.getCommand().equalsIgnoreCase("KICK")
				|| msg.getCommand().equalsIgnoreCase("CTCP_ACTION"))
				&
				msg.getParams()[0].equalsIgnoreCase(channel)
				) {
			
				sendChanEvent(event.getIRCMessage());
			}
			else if ((msg.getCommand().equalsIgnoreCase("JOIN")
				|| msg.getCommand().equalsIgnoreCase("PART"))	
				&
				(msg.getParams()[0].equalsIgnoreCase(channel)
				|| msg.getEscapedParams().equalsIgnoreCase(channel)
				)) {
				
				sendChanEvent(event.getIRCMessage());
			}
		}
		
		if ((msg.getCommand().equalsIgnoreCase("PRIVMSG") & msg.getEscapedParams().matches(bot.getNick() + ":? +.*")
			& msg.getParams()[0].equalsIgnoreCase(channel))
			|| (msg.getCommand().equalsIgnoreCase("PRIVMSG") & !msg.getPrefixNick().equalsIgnoreCase(bot.getNick()) & msg.isPrivate() & userStatus.containsKey(msg.getPrefixNick()))) {
			String temp = msg.getEscapedParams().replaceFirst(bot.getNick() + ":? +","");
			String[] botCmd = temp.split(" +");
			sendChanBotEvent(msg.getPrefixNick(),botCmd,msg.isPrivate(),event.getIRCMessage());
		}
		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IChannel#chanRespond(botFramework.interfaces.IChanEvent)
	 */
	public boolean respondToChanEvent(IChanEvent event) {
		log.debug("Responding to Chan event " + event);

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
		return true;
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IChannel#updateIdent(java.lang.String, java.lang.String)
	 */
	public void updateIdent(String user,String ident) {
		if (userStatus.containsKey(user) == true) {
			User temp = (User)userStatus.get(user);
			if (ident.compareTo(temp.ident) != 0) {
				log.info("Updating ident for user " + user + " to " + ident);
				userStatus.remove(user);
				userStatus.put(user,new User(ident,temp.status));
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IChannel#rehash()
	 */
	public void rehash() {
		log.info("rehashing");
		userStatus.clear();
		bot.enqueueCommand(irc.names(channel));
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
	public Set<String> getUsers() {
		return userStatus.keySet();
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IChannel#join()
	 */
	public void join() {
		if(bot.isRegistered())
		{
			log.info("joining " + channel);
		
			String joinCmd = irc.join(channel);
		
			bot.enqueueCommand(joinCmd);
		} else {
			log.info("Not joining yet " + channel + ": not registered");
		}
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IChannel#part()
	 */
	public void part() {
		log.info("parting " + channel);
		bot.enqueueCommand(irc.part(channel));
		userStatus.clear();
	}

	@Override
	public boolean respondToBotEvent(IBotEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean respondToChanBotEvent(IChanBotEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean respondToUnknownCommandListener(IChanBotEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
}
