package botFramework.interfaces;

public interface IEventListener<T> {
	boolean respond(T event);
}
