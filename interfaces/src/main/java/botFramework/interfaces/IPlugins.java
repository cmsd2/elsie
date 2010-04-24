package botFramework.interfaces;


public interface IPlugins {
	<T> Object findAndLoadPlugin(T event);
	
	<T,K> K findAndLoadPlugin(T event, Class<K> c);
	
	public Object findPluginForCommand(String cmd);
	
	public <K> K findPluginForCommand(String cmd, Class<K> iface);
	
	void reloadPlugins();
	
	Object loadPlugin(String className);

	public ICommandsMap getCommandsMap();
}
