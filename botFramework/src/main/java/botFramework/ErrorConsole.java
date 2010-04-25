package botFramework;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import botFramework.interfaces.IBot;
import botFramework.interfaces.IErrorEvent;
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
	
	private static final Log log = LogFactory.getLog(ErrorConsole.class);

	private IBot bot;
	
	public IBot getBot()
	{
		return bot;
	}

	public void setBot(IBot bot)
	{
		if(this.bot != null)
		{
			log.info("Unsubscribing from error events from bot " + bot);
			this.bot.getErrors().remove(getErrorListener());
		}
		this.bot = bot;
		if(this.bot != null)
		{
			log.info("Subscribing to error events from bot " + bot);
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
