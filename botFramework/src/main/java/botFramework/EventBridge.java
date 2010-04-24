package botFramework;

import botFramework.interfaces.IBotEvent;
import botFramework.interfaces.IBotListener;
import botFramework.interfaces.IChanBotEvent;
import botFramework.interfaces.IChanBotListener;
import botFramework.interfaces.IChanEvent;
import botFramework.interfaces.IChanListener;
import botFramework.interfaces.IEventSink;
import botFramework.interfaces.IIrcEvent;
import botFramework.interfaces.IIrcListener;

public class EventBridge {
	private IEventSink eventSink;
	
	public EventBridge(IEventSink eventSink)
	{
		this.eventSink = eventSink;
	}

	private IChanListener chanListener = null;
	public IChanListener getChanListener()
	{
		if(chanListener == null)
		{
			chanListener = new IChanListener() {
				
				@Override
				public boolean respond(IChanEvent event) {
					return EventBridge.this.respondToChanEvent(event);
				}
			};
		}
		return chanListener;
	}
	
	private IChanBotListener chanBotListener = null;
	public IChanBotListener getChanBotListener()
	{
		if(chanBotListener == null)
		{
			chanBotListener = new IChanBotListener() {
				
				@Override
				public boolean respond(IChanBotEvent event) {
					return EventBridge.this.respondToChanBotEvent(event);
				}
			};
		}
		return chanBotListener;
	}
	
	private IChanBotListener unknownCommandListener = null;
	public IChanBotListener getUnknownCommandListener()
	{
		if(unknownCommandListener == null)
		{
			unknownCommandListener = new IChanBotListener() {
				
				@Override
				public boolean respond(IChanBotEvent event) {
					return EventBridge.this.respondToUnknownCommandEvent(event);
				}
			};
		}
		return unknownCommandListener;
	}
	
	private IIrcListener ircListener = null;
	public IIrcListener getIrcListener()
	{
		if(ircListener == null)
		{
			ircListener = new IIrcListener() {
				
				@Override
				public boolean respond(IIrcEvent event) {
					return EventBridge.this.respondToIrcEvent(event);
				}
			};
		}
		return ircListener;
	}
	
	private IBotListener botListener = null;
	public IBotListener getBotListener()
	{
		if(botListener == null)
		{
			botListener = new IBotListener() {
				@Override
				public boolean respond(IBotEvent event) {
					return EventBridge.this.respondToBotEvent(event);
				}
			};
		}
		return botListener;
	}
	
	public boolean respondToChanEvent(IChanEvent event)
	{
		return eventSink.respondToChanEvent(event);
	}
	
	public boolean respondToChanBotEvent(IChanBotEvent event)
	{
		return eventSink.respondToChanBotEvent(event);
	}
	
	public boolean respondToBotEvent(IBotEvent event)
	{
		return eventSink.respondToBotEvent(event);
	}
	
	public boolean respondToIrcEvent(IIrcEvent event)
	{
		return eventSink.respondToIrcEvent(event);
	}
	
	public boolean respondToUnknownCommandEvent(IChanBotEvent event)
	{
		return eventSink.respondToUnknownCommandListener(event);
	}
}
