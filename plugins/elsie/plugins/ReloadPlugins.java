package elsie.plugins;

import botFramework.interfaces.*;

public class ReloadPlugins extends AbstractPlugin {

	@Override
	public boolean chanBotRespond(IChanBotEvent event) {
		getPlugins().reloadPlugins();
		return true;
	}

}
