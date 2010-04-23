package elsie.plugins;

import botFramework.interfaces.IChanBotEvent;
import botFramework.interfaces.ICommandsMap;

public class AddPlugin extends AbstractPlugin {

	@Override
	public boolean chanBotRespond(IChanBotEvent event) {
		String[] cmd = event.getBotCommand();
		
		if(cmd.length < 3)
		{
			return false;
		}
		
		// cmd[0] == !addplugin
		String hook = cmd[1];
		String cname = cmd[2];
		
		ICommandsMap map = getPlugins().getCommandsMap();
		
		if(map.hasPluginCommand(hook))
		{
			return false;
		} else {
			System.out.println("!addplugin: adding " + hook + " " + cname);
			map.addPluginCommand(hook, cname);
		}
		
		return true;
	}

}
