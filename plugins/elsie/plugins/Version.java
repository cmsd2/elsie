package elsie.plugins;

import botFramework.interfaces.IChanBotEvent;
import botFramework.interfaces.IChanBotListener;

public class Version extends AbstractPlugin {

	@Override
	public boolean chanBotRespond(IChanBotEvent event) {
		getUserFunctions().botMessage(event.getCommandSource(),"version",null,event.getIsPrivate(),event.getChannelSource(),false);
		return true;
	}

}
