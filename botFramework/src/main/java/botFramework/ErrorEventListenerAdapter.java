package botFramework;

import botFramework.interfaces.IErrorEvent;
import botFramework.interfaces.IErrorListener;
import botFramework.interfaces.IEventListener;

public class ErrorEventListenerAdapter implements IEventListener<IErrorEvent> {

	IErrorListener delegate;
	
	public ErrorEventListenerAdapter(IErrorListener delegate)
	{
		this.delegate = delegate;
	}
	
	@Override
	public boolean respond(IErrorEvent event) {
		if(event.getType().equalsIgnoreCase("problem"))
		{
			delegate.problem(event.getModule(), event.getMessage());
		} else {
			delegate.exception(event.getModule(), event.getType(), event.getMessage());
		}
		return true;
	}



}
