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
		
		IChannel chan1 = new Channel(luser1, "#luserX");
		luser1.addChannel(chan1);
		
		Console console = new Console(luser1);
		luser1.addIRCListener(console.getIrcListener());
		chan1.addChanListener(console.getChanListener());
		
		IChannel chan2 = new Channel(luser2, "#luserX");
		luser2.addChannel(chan2);
		
		IChannel chan3 = new Channel(luser3, "#luserX");
		luser3.addChannel(chan3);
		
		luser1.start();
		luser2.start();
		luser3.start();
	}
}
