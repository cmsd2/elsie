package botFramework;

import java.util.Hashtable;

import botFramework.interfaces.IChanBotEvent;
import botFramework.interfaces.IChanBotListener;
import botFramework.interfaces.IEventListener;
import botFramework.interfaces.IIrcMessage;
import botFramework.interfaces.IPlugins;

public abstract class EventDispatcher<T> implements IEventListener<T> {
	private Hashtable<String, IEventListener<T>> channelEventHandlers = new Hashtable<String, IEventListener<T>>();
	
	public EventDispatcher()
	{
	}

	public boolean respond(T event) {
		IEventListener<T> listener = findListener(event);
		
		if(listener == null)
		{
			listener = loadListener(event);
		}
		
		if(listener != null)
		{
			return listener.respond(event);
		}
		
		return false;
	}
	
	public IEventListener<T> findListener(T event)
	{
		String cmd = getKey(event);
		
		return channelEventHandlers.get(cmd);
	}
	
	public abstract IEventListener<T> loadListener(T event);
	
	public abstract String getKey(T event);
	
	public void saveListener(T event, IEventListener<T> listener)
	{
		String cmd = getKey(event);
		
		channelEventHandlers.put(cmd, listener);
	}
}
