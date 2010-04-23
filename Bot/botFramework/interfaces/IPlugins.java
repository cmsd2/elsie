package botFramework.interfaces;

import java.util.Hashtable;

public interface IPlugins {
	IChanListener findPlugin(IChanEvent event);
	
	Object findPlugin(IChanBotEvent event);
	
	<T> T findInterface(Object o, Class<T> c);
	
	void reloadPlugins();
	
	Object loadPlugin(String className, IChanBotEvent event);
	
	public abstract IUserFunctions getUserFunctions();
	
	public abstract void setUserFunctions(IUserFunctions f);
	
	public Hashtable<String, String> getChanBotPluginClasses();
}
