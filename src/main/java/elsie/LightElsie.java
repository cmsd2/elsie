package elsie;

/**
 * @author sffubs
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */

import java.util.Vector;

import botFramework.*;
import botFramework.interfaces.IChannels;

class LightElsie {
	public static void main(String[] args) {
			
		Vector nicks = new Vector();
		nicks.addElement(args[1]);
		nicks.addElement("jelsie");
		nicks.addElement("jelsieDev");
		nicks.addElement("kelsie");
		nicks.addElement("kelsieDev");
		
		Vector servers = new Vector();
		servers.addElement(args[0]);
		servers.addElement("efnet.demon.co.uk");
		servers.addElement("irc.isdnet.fr");
		
		Bot elsieBot = new Bot(nicks, servers, 6667, 0, "me","iso-8859-1");
		
		//Error listener - when it all goes wrong
		ErrorConsole err = new ErrorConsole();
		elsieBot.getErrors().add(new ErrorEventListenerAdapter(err));
		
		IChannels chans = new Channels();
		chans.setBot(elsieBot);
		
		//Console listener - something to look at
		Console console = new Console();
		console.setBot(elsieBot);
		console.setChannels(chans);
		
		Channel chan = new Channel();
		chan.setChannel(args[2]);
		chan.setBot(elsieBot);
		chan.setChannels(chans);
		chan.initialise();
		
		elsieBot.start();		//let the mayhem begin
	}
}