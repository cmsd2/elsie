package botFramework;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import botFramework.interfaces.IEventListener;

public abstract class EventDispatcher<T> implements IEventListener<T> {
	private static final Log log = LogFactory.getLog(EventDispatcher.class);

	private Map<String, IEventListener<T>> channelEventHandlers = new HashMap<String, IEventListener<T>>();
	
	public EventDispatcher()
	{
	}

	public boolean respond(T event) {
		log.debug("Responding to event " + event);

		IEventListener<T> listener = findListener(event);
		
		if(listener == null)
		{
			log.debug("No listener found, loading one");
			listener = loadListener(event);
			if(listener != null)
			{
				log.info("Loaded handler " + listener + " to handle " + event);
			}
		}
		
		if(listener != null)
		{
			log.debug("Using " + listener + " to handle " + event);
			return listener.respond(event);
		}
		
		log.debug("No listener found to handle " + event);
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
		
		log.info("Saving listener mapping " + cmd + " => "+ listener);
		
		channelEventHandlers.put(cmd, listener);
	}
}
