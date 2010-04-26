package elsie.plugins.commands;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import elsie.plugins.AbstractPlugin;

import botFramework.interfaces.IChanBotEvent;
import botFramework.interfaces.ICommandsMap;

public class AddPlugin extends AbstractPlugin {
	private static final Log log = LogFactory.getLog(AddPlugin.class);

	@Override
	public boolean chanBotRespond(IChanBotEvent event) {
		log.info("Handling event " + event);
		String[] cmd = event.getBotCommand();
		
		if(cmd.length < 3)
		{
			log.error("Atleast 3 args required");
			return false;
		}
		
		// cmd[0] == !addplugin
		String hook = cmd[1];
		String cname = cmd[2];
		
		ICommandsMap map = getPlugins().getCommandsMap();
		
		if(map.hasPluginCommand(hook))
		{
			log.error("Not adding plugin for existing hook " + hook);
			return false;
		} else {
			log.info("Adding " + hook + " for class " + cname);
			map.addPluginCommand(hook, cname);
		}
		
		return true;
	}

}
