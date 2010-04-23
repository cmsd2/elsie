package botFramework.interfaces;

public interface IEventListenerProvider<T> {
	IEventListener<T> getListener(T event);
}
