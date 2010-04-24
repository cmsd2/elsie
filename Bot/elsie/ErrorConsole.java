package elsie;

import elsie.util.attributes.Inject;
import botFramework.*;
import botFramework.interfaces.IBot;
import botFramework.interfaces.IErrorListener;
import botFramework.interfaces.IEventListener;

/**
 * @author sffubs
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ErrorConsole implements IErrorListener {
	
	private IBot bot;
	
	public IBot getBot()
	{
		return bot;
	}
	
	@Inject
	public void setBot(IBot bot)
	{
		if(this.bot != null)
		{
			this.bot.getErrors().remove(getErrorListener());
		}
		this.bot = bot;
		if(this.bot != null)
		{
			this.bot.getErrors().add(getErrorListener());
		}
	}
	
	public ErrorConsole() {
		
	}
	
	IEventListener<IErrorEvent> listener = null;
	public IEventListener<IErrorEvent> getErrorListener()
	{
		if(listener == null)
		{
			listener = new ErrorEventListenerAdapter(this);
		}
		return listener;
	}
	
	public void exception(String module, String type, String message) {
		System.out.println(module + ": Caught exception " + type + ": " + message);
	}
	
	public void problem(String module, String problem) {
		System.out.println(module + ": Problem: " + problem);
	}
}
