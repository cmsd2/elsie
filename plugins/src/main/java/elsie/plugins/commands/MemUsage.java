package elsie.plugins.commands;

/**
 * @author sffubs
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import elsie.plugins.AbstractPlugin;

import botFramework.interfaces.*;

public class MemUsage extends AbstractPlugin {
	
	private static final Log log = LogFactory.getLog(MemUsage.class);
	
	public boolean chanBotRespond(IChanBotEvent event) {

		log.info("Handling event " + event);

		String[] memUsage = new String[1];
		memUsage[0] = Long.toString(Runtime.getRuntime().totalMemory());
			
		System.out.println("!memusage: " + memUsage[0]);
			
		getUserFunctions().botMessage(event.getCommandSource(),"memusage",memUsage,event.getIsPrivate(),event.getChannelSource(),false);
		return true;
	}
}
