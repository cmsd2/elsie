package elsie;

import java.util.HashSet;
import java.util.Hashtable;

import botFramework.interfaces.IBot;
import botFramework.interfaces.IChanBotListener;
import botFramework.interfaces.IUserFunctions;

public class EventListenerProvider<T> {
	private Hashtable<String, String> eventListenerClasses = new Hashtable<String, String> ();
	private Hashtable<Class<T>, T> eventListenerPlugins = new Hashtable<Class<T>, T>();
}
