package elsie.plugins;

/**
 * @author sffubs
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */

import botFramework.interfaces.*;

public class MemUsage extends AbstractPlugin {
	
	public boolean chanBotRespond(IChanBotEvent event) {
		if (event.getBotCommand()[0].equalsIgnoreCase("!memusage")) {
			String[] memUsage = new String[1];
			memUsage[0] = Long.toString(Runtime.getRuntime().totalMemory());
			
			System.out.println("!memusage: " + memUsage[0]);
			
			getUserFunctions().botMessage(event.getCommandSource(),"memusage",memUsage,event.getIsPrivate(),event.getChannelSource(),false);
			return true;
		}
		return false;
	}
}
