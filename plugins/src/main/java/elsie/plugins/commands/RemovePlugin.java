package elsie.plugins.commands;

import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import elsie.plugins.AbstractPlugin;

import botFramework.interfaces.IChanBotEvent;
import botFramework.interfaces.IChanBotListener;
import botFramework.interfaces.ICommandsMap;

public class RemovePlugin extends AbstractPlugin {

	private static final Log log = LogFactory.getLog(RemovePlugin.class);

	@Override
	public boolean chanBotRespond(IChanBotEvent event) {
		log.info("Handling event " + event);

		String[] cmd = event.getBotCommand();
		
		if(cmd.length < 2)
		{
			log.error("At least 2 args required");
			return false;
		}
		
		// cmd[0] == !addplugin
		String hook = cmd[1];
		
		ICommandsMap map = getPlugins().getCommandsMap();
		
		if(hook.equals("!addplugin") || hook.equals("!removeplugin") || hook.equals("!reload"))
		{
			log.error("Cowardly refusing to remove core plugin " + hook);
			return false;
		} else {
			log.info("Removing " + hook);
			map.removePluginCommand(hook);
		}
		
		return true;
	}

}
