package elsie;

import botFramework.interfaces.IObjectFactory;

public class SingletonObjectFactory<T> implements IObjectFactory<T> {

	private T instance = null;
	private IObjectFactory<T> delegate;
	
	public SingletonObjectFactory(IObjectFactory<T> delegate)
	{
		this.delegate = delegate;
	}
	
	public SingletonObjectFactory(IObjectFactory<T> delegate, T instance)
	{
		this.delegate = delegate;
		this.instance = instance;
	}
	
	@Override
	public T create(Class<T> c) {
		if(instance == null)
		{
			instance = delegate.create(c);
		}
		return instance;
	}

	
}
