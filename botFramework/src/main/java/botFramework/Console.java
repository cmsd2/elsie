/**
 * @author sffubs
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */

package botFramework;
import java.text.DateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import botFramework.interfaces.IBot;
import botFramework.interfaces.IChanEvent;
import botFramework.interfaces.IChanListener;
import botFramework.interfaces.IChannel;
import botFramework.interfaces.IChannels;
import botFramework.interfaces.IIrcEvent;
import botFramework.interfaces.IIrcListener;
import botFramework.interfaces.IIrcMessage;

public class Console {
	private static final Log log = LogFactory.getLog(Console.class);

	private DateFormat df;
	private IBot bot;
	private IChannels channels;
	
	public Console() {		
		df = DateFormat.getTimeInstance(DateFormat.SHORT);
	}
	
	public IBot getBot()
	{
		return this.bot;
	}

	public void setBot(IBot bot)
	{
		if(this.bot != null)
		{
			log.info("Unsubscribing from irc events from bot " + bot);
			this.bot.getIrcEvents().remove(getIrcListener());
		}
		this.bot = bot;
		if(this.bot != null)
		{
			log.info("Subscribing to irc events from bot " + bot);
			this.bot.getIrcEvents().add(getIrcListener());
		}
	}
	
	public IChannels getChannels()
	{
		return channels;
	}

	public void setChannels(IChannels channels)
	{
		if(this.channels != null)
		{
			this.channels.getChanEvents().remove(getChanListener());
		}
		this.channels = channels;
		if(this.channels != null)
		{
			this.channels.getChanEvents().add(getChanListener());
		}
	}
	
	private IIrcListener ircListener = null;
	public IIrcListener getIrcListener()
	{
		if(ircListener == null)
		{
			ircListener = new IIrcListener() {
				@Override
				public boolean respond(IIrcEvent event) {
					Console.this.respondToIrcEvent(event);
					return true;
				}
			};
		}
		return ircListener;
	}
	
	private IChanListener chanListener = null;
	public IChanListener getChanListener()
	{
		if(chanListener == null)
		{
			chanListener = new IChanListener() {
				@Override
				public boolean respond(IChanEvent event) {
					Console.this.respondToChanEvent(event);
					return true;
				}
			};
		}
		return chanListener;
	}
	
	public void respondToIrcEvent(IIrcEvent event) {
		log.debug("Responding to IRC event " + event);
		IIrcMessage msg = event.getIRCMessage();
		String dateTime = df.format(new Date(System.currentTimeMillis()));
		
		if (msg.getCommand().equalsIgnoreCase("PRIVMSG") & msg.isPrivate() & !msg.getPrefixNick().equalsIgnoreCase(bot.getNick())) {
			System.out.println("[" + dateTime + "]"
				+ " >" + msg.getPrefixNick() + "< " + msg.getEscapedParams());
		}
		else if (msg.getCommand().equalsIgnoreCase("PRIVMSG") & msg.isPrivate()) {
			System.out.println("[" + dateTime + "]"
				+ " --> >" + msg.getParams()[0] + "< " + msg.getEscapedParams());
		}
		else if (msg.getCommand().equalsIgnoreCase("CTCP_ACTION") & msg.isPrivate()) {
			System.out.println("[" + dateTime + "] "
				+ " >*< " + msg.getPrefixNick() + " " + msg.getEscapedParams());
		}
		else if (msg.getCommand().equalsIgnoreCase("NICK")) {
			System.out.println("[" + dateTime + "]"
				+ " * " + msg.getPrefixNick() + " is now known as " + msg.getEscapedParams());
		}
		else if (msg.getCommand().equalsIgnoreCase("QUIT")) {
			System.out.println("[" + dateTime + "]"
				+ " * " + msg.getPrefixNick() + " has quit " + msg.getEscapedParams());
		}
		else if (msg.getCommand().equalsIgnoreCase("ERROR")) {
			System.out.println("[" + dateTime + "]"
				+ " * Error! * (" + msg.getEscapedParams() + ")");
		}
		else if (msg.getCommand().equalsIgnoreCase("CTCP_PING")) {
			System.out.println("[" + dateTime + "]"
				+ " >" + msg.getPrefixNick() + "<" + " *PING* " + msg.getEscapedParams());
		}
		else if (msg.getCommand().equalsIgnoreCase("CTCP_TIME")) {
			System.out.println("[" + dateTime + "]"
				+ " >" + msg.getPrefixNick() + "<" + " *TIME* " + msg.getEscapedParams());
		}
		else if (msg.getCommand().equalsIgnoreCase("CTCP_VERSION")) {
			System.out.println("[" + dateTime + "]"
				+ " >" + msg.getPrefixNick() + "<" + " *VERSION* " + msg.getEscapedParams());
		}
	}
	public void respondToChanEvent(IChanEvent event) {
		log.debug("Responding to Chan event " + event);
		IIrcMessage msg = event.getIRCMessage();
		IChannel chan = event.getChannelSource();
		String dateTime = df.format(new Date(System.currentTimeMillis()));
		if (msg.getCommand().equalsIgnoreCase("PRIVMSG") & !msg.isPrivate()) {
			System.out.println("[" + dateTime + "] " + chan.getChannel() + ":"
				+ " <" + msg.getPrefixNick() + "> " + msg.getEscapedParams());
		}
		else if (msg.getCommand().equalsIgnoreCase("CTCP_ACTION") & !msg.isPrivate()) {
			System.out.println("[" + dateTime + "] " + chan.getChannel() + ":"
				+ " * " + msg.getPrefixNick() + " " + msg.getEscapedParams());
		}
		else if (msg.getCommand().equalsIgnoreCase("JOIN")) {
			System.out.println("[" + dateTime + "] " + chan.getChannel() + ":"
				+ " * " + msg.getPrefixNick() + " has joined");
		}
		else if (msg.getCommand().equalsIgnoreCase("PART")) {
			System.out.println("[" + dateTime + "] " + chan.getChannel() + ":"
				+ " * " + msg.getPrefixNick() + " has left");
		}
		else if (msg.getCommand().equalsIgnoreCase("MODE")) {
			if (msg.getPrefixNick().equals("")) {
				msg.setPrefixNick(msg.getPrefix());
			}
			String parameters;
			int j;
			if (msg.getParams().length < 2) {
				parameters = msg.getParams()[0];
			}
			else {
				parameters = msg.getParams()[1];
				j = 2;
				for (int i = j; i < msg.getParams().length; i++) {
					parameters = parameters + " " + msg.getParams()[i];
				}
			}
			//parameters = parameters + msg.escapedParams;
			System.out.println("[" + dateTime + "] " + chan.getChannel() + ":"
				+ " * " + msg.getPrefixNick() + " sets mode " + parameters);
		}
		else if (msg.getCommand().equalsIgnoreCase("KICK")) {
			System.out.println("[" + dateTime + "] " + chan.getChannel() + ":"
				+ " * " + msg.getPrefixNick() + " has kicked " + msg.getParams()[1] + " (" + msg.getEscapedParams() + ")");
		}
		else if (msg.getCommand().equals("353")) {
			System.out.println("[" + dateTime + "] " + chan.getChannel() + ":"
				+ " * " + msg.getEscapedParams());
		}
		else if (msg.getCommand().equals("311")) {
			System.out.println("[" + dateTime + "] " + chan.getChannel() + ":"
				+ " * " + msg.getParams()[1] + " is " + msg.getParams()[2] + "@" + msg.getParams()[3]);
		}
		/*else {
			System.out.println("[" + dateTime + "] " + chan.getChannel() + ":" + msg.escapedParams);
		}*/
	}
}