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
		
		Channel chan = new Channel(args[2]);
		elsieBot.getIrcEvents().add(chan.getIrcEventListener());
		chan.setBot(elsieBot);
		
		//Console listener - something to look at
		Console console = new Console(elsieBot);
		elsieBot.getIrcEvents().add(console.getIrcListener());
		chan.addChanListener(console.getChanListener());
		
		elsieBot.start();		//let the mayhem begin
	}
}