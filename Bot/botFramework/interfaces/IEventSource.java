package botFramework.interfaces;

import java.util.Set;

import botFramework.IErrorEvent;

public interface IEventSource<T> {

	public abstract IEventSource<IErrorEvent> getErrorEventSource();

	public abstract void add(IEventListener<T> listener);

	public abstract void remove(IEventListener<T> listener);

	public abstract Set<IEventListener<T>> getListeners();

	public abstract boolean sendEvent(String module, T event);

}