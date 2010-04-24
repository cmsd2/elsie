package botFramework;

import botFramework.interfaces.IChanEvent;
import botFramework.interfaces.IChanListener;
import botFramework.interfaces.IEventListener;
import botFramework.interfaces.IIrcMessage;
import botFramework.interfaces.IPlugins;

public class ChanEventDispatcher extends EventDispatcher<IChanEvent> {
	
	private IPlugins plugins;
	
	public ChanEventDispatcher(IPlugins plugins)
	{
		this.plugins = plugins;
	}

	@Override
	public String getKey(IChanEvent event) {
		IIrcMessage msg = event.getIRCMessage();
		return msg.getPrefix();
	}


	@Override
	public IEventListener<IChanEvent> loadListener(IChanEvent event) {
		return plugins.findAndLoadPlugin(event, IChanListener.class);
	}
}
