/**
 * @author sffubs
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */

package elsie;
import java.util.Date;
import java.text.DateFormat;

import botFramework.*;
import botFramework.interfaces.IChanEvent;
import botFramework.interfaces.IChanListener;
import botFramework.interfaces.IChannel;
import botFramework.interfaces.IIRCEvent;
import botFramework.interfaces.IIRCListener;
import botFramework.interfaces.IIRCMessage;

public class Console {
	DateFormat df;
	Bot bot;
	
	public Console(Bot bot) {		
		df = DateFormat.getTimeInstance(DateFormat.SHORT);
		this.bot = bot;
	}
	
	public IIRCListener getIrcListener()
	{
		return new IIRCListener() {
			@Override
			public boolean respond(IIRCEvent event) {
				Console.this.respondToIrcEvent(event);
				return true;
			}
		};
	}
	
	public IChanListener getChanListener()
	{
		return new IChanListener() {
			
			@Override
			public boolean respond(IChanEvent event) {
				Console.this.respondToChanEvent(event);
				return true;
			}
		};
	}
	
	public void respondToIrcEvent(IIRCEvent event) {
		IIRCMessage msg = event.getIRCMessage();
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
		IIRCMessage msg = event.getIRCMessage();
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