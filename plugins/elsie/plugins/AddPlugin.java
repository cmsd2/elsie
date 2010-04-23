package elsie.plugins;

import java.util.Hashtable;

import botFramework.interfaces.IChanBotEvent;
import botFramework.interfaces.IChanBotListener;

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
		
		Hashtable<String,String> table = getPlugins().getChanBotPluginClasses();
		
		if(table.containsKey(hook))
		{
			return false;
		} else {
			System.out.println("!addplugin: adding " + hook + " " + cname);
			table.put(hook, cname);
		}
		
		return true;
	}

}
