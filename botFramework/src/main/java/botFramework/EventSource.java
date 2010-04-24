package botFramework;

import java.util.HashSet;
import java.util.Set;

import botFramework.interfaces.IErrorEvent;
import botFramework.interfaces.IEventListener;
import botFramework.interfaces.IEventSource;

public class EventSource<T> implements IEventSource<T> {
	private Set<IEventListener<T>> listeners = new HashSet<IEventListener<T>>();
	private IEventSource<IErrorEvent> errorEventSource;

	private Object owner;
	
	public EventSource(Object owner, IEventSource<IErrorEvent> errorEventSource)
	{
		this.owner = owner;
		this.errorEventSource = errorEventSource;
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IEventSource#getErrorEventSource()
	 */
	public IEventSource<IErrorEvent> getErrorEventSource()
	{
		return errorEventSource;
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IEventSource#add(botFramework.interfaces.IEventListener)
	 */
	public void add(IEventListener<T> listener)
	{
		listeners.add(listener);
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IEventSource#remove(botFramework.interfaces.IEventListener)
	 */
	public void remove(IEventListener<T> listener)
	{
		listeners.remove(listener);
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IEventSource#getListeners()
	 */
	public Set<IEventListener<T>> getListeners()
	{
		return listeners;
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IEventSource#sendEvent(java.lang.String, T)
	 */
	public boolean sendEvent(String module, T event)
	{
		boolean respondedTo = false;
		
		for(IEventListener<T> listener: listeners)
		{
			try {
				respondedTo |= listener.respond(event);
			} catch (Exception e)
			{
				if(errorEventSource != null)
				{
					errorEventSource.sendEvent(module, new ErrorEvent(owner, module, "Exception", e.toString()));
				}
			}
		}
		return respondedTo;
	}
}
