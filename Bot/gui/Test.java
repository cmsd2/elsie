package gui;

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

import java.util.Vector;

public class Test {
	public static void main(String[] args) {
		Vector nicks = new Vector();
		nicks.addElement(args[1]);
		nicks.addElement("luser1");
		nicks.addElement("luser2");
		nicks.addElement("luser3");
		nicks.addElement("luser4");
		nicks.addElement("luser5");
		
		Vector servers = new Vector();
		servers.addElement(args[0]);
		servers.addElement("irc.isdnet.fr");
		servers.addElement("efnet.demon.co.uk");
		
		Bot luser1 = new Bot(nicks, servers, 6667, 0, "drone1","iso-8859-1");
		Bot luser2 = new Bot(nicks, servers, 6667, 0, "drone2","iso-8859-1");
		Bot luser3 = new Bot(nicks, servers, 6667, 0, "drone3","iso-8859-1");
		
		Channel chan1 = new Channel("#chu");
		chan1.setBot(luser1);
		
		Channel chan2 = new Channel("#chu");
		chan2.setBot(luser2);
		
		Channel chan3 = new Channel("#chu");
		chan3.setBot(luser3);
	}
}
