package botFramework;

import botFramework.interfaces.IChanBotEvent;
import botFramework.interfaces.IChanBotListener;
import botFramework.interfaces.IEventListener;
import botFramework.interfaces.IPlugins;

public class ChanBotEventDispatcher extends EventDispatcher<IChanBotEvent> {
	private IPlugins plugins;
	
	public ChanBotEventDispatcher(IPlugins plugins)
	{
		this.plugins = plugins;
	}

	@Override
	public String getKey(IChanBotEvent event) {
		return event.getBotCommand()[0];
	}

	@Override
	public IEventListener<IChanBotEvent> loadListener(IChanBotEvent event) {
		Object o = plugins.findPlugin(event);
		
		return plugins.findInterface(o, IChanBotListener.class);
	}
}
