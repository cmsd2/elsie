package elsie.plugins;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import botFramework.interfaces.IChanBotEvent;
import botFramework.interfaces.IChanBotListener;

public class Version extends AbstractPlugin {

	private static final Log log = LogFactory.getLog(Version.class);

	@Override
	public boolean chanBotRespond(IChanBotEvent event) {
		log.info("Handling event " + event);

		getUserFunctions().botMessage(event.getCommandSource(),"version",null,event.getIsPrivate(),event.getChannelSource(),false);
		return true;
	}

}
