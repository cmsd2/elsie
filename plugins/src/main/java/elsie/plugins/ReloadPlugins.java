package elsie.plugins;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import botFramework.interfaces.*;

public class ReloadPlugins extends AbstractPlugin {

	private static final Log log = LogFactory.getLog(ReloadPlugins.class);
	
	@Override
	public boolean chanBotRespond(IChanBotEvent event) {
		log.info("Handling event " + event);

		getPlugins().reloadPlugins();
		return true;
	}

}
