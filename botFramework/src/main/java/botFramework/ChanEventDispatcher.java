package botFramework;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import botFramework.interfaces.IChanEvent;
import botFramework.interfaces.IChanListener;
import botFramework.interfaces.IChannels;
import botFramework.interfaces.IEventListener;
import botFramework.interfaces.IIrcMessage;

public class ChanEventDispatcher extends DefaultEventDispatcher<IChanEvent> {
	
	private static final Log log = LogFactory.getLog(ChanEventDispatcher.class);

	@Override
	public String getKey(IChanEvent event) {
		IIrcMessage msg = event.getIRCMessage();
		return msg.getPrefix();
	}

	@Override
	public IEventListener<IChanEvent> loadListener(IChanEvent event) {
		log.info("Finding plugin to handle " + event);
		return getPlugins().findAndLoadPlugin(event, IChanListener.class);
	}

	@Override
	protected void addChannelListeners(IChannels channels) {
		log.info("Subscribing to chan events from channel group " + channels);
		channels.getChanEvents().add(this);
	}

	@Override
	protected void removeChannelListeners(IChannels channels) {
		log.info("Unsubscribing to chan events from channel group " + channels);
		channels.getChanEvents().remove(this);
	}
}
