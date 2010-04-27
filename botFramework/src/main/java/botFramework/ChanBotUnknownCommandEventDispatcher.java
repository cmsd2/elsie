package botFramework;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import botFramework.interfaces.IChanBotEvent;
import botFramework.interfaces.IChanBotUnknownCmdListener;
import botFramework.interfaces.IChannels;
import botFramework.interfaces.IEventListener;

public class ChanBotUnknownCommandEventDispatcher extends DefaultEventDispatcher<IChanBotEvent> implements IChanBotUnknownCmdListener{
	private static final Log log = LogFactory.getLog(ChanBotUnknownCommandEventDispatcher.class);

	protected void removeChannelListeners(IChannels channels)
	{
		log.info("Unsubscribing to chan bot events from channel group " + channels);
		channels.getUnknownCommandEvents().remove(this);
	}

	protected void addChannelListeners(IChannels channels)
	{
		log.info("Subscribing to chan bot events from channel group " + channels);
		channels.getUnknownCommandEvents().add(this);
	}

	@Override
	public String getKey(IChanBotEvent event) {
		return event.getBotCommand()[0];
	}

	@Override
	public IEventListener<IChanBotEvent> loadListener(IChanBotEvent event) {
		log.info("Finding plugin to handle " + event);
		return getPlugins().findAndLoadPlugin(event, IChanBotUnknownCmdListener.class);
	}
}
