package luser;

/**
 * @author sffubs
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
import botFramework.*;
import botFramework.interfaces.IChannel;
import elsie.Console;

import java.util.Vector;

public class Luser {
	public static void main(String[] args) {
		Vector nicks = new Vector();
		nicks.addElement("luser1");
		nicks.addElement("luser2");
		nicks.addElement("luser3");
		nicks.addElement("luser4");
		nicks.addElement("luser5");
		
		Vector servers = new Vector();
		//servers.addElement("irc.isdnet.fr");
		servers.addElement("efnet.demon.co.uk");
		
		Bot luser1 = new Bot(nicks, servers, 6667, 0, "drone1","iso-8859-1");
		Bot luser2 = new Bot(nicks, servers, 6667, 0, "drone2","iso-8859-1");
		Bot luser3 = new Bot(nicks, servers, 6667, 0, "drone3","iso-8859-1");
		
		Channel chan1 = new Channel("#luserX");
		chan1.setBot(luser1);
		
		Console console = new Console(luser1);
		luser1.getIrcEvents().add(console.getIrcListener());
		chan1.addChanListener(console.getChanListener());
		
		Channel chan2 = new Channel("#luserX");
		chan2.setBot(luser2);
		
		Channel chan3 = new Channel("#luserX");
		chan3.setBot(luser3);
		
		luser1.start();
		luser2.start();
		luser3.start();
	}
}
