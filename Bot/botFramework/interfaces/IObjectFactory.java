package botFramework.interfaces;

public interface IObjectFactory<T> {
	T create(Class<T> c);
}
