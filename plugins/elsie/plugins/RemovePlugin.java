package elsie.plugins;

import java.util.Hashtable;

import botFramework.interfaces.IChanBotEvent;
import botFramework.interfaces.IChanBotListener;

public class RemovePlugin extends AbstractPlugin {

	@Override
	public boolean chanBotRespond(IChanBotEvent event) {
		String[] cmd = event.getBotCommand();
		
		if(cmd.length < 2)
		{
			return false;
		}
		
		// cmd[0] == !addplugin
		String hook = cmd[1];
		
		Hashtable<String,String> table = getPlugins().getChanBotPluginClasses();
		
		if(hook.equals("!addplugin") || hook.equals("!removeplugin") || hook.equals("!reload"))
		{
			return false;
		} else {
			System.out.println("!removeplugin: removing " + hook);
			table.remove(hook);
		}
		
		return true;
	}

}
