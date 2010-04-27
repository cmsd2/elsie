package botFramework.interfaces;


public interface IPlugins {
	String ROLE = IPlugins.class.getName();

	<T extends IIrcEvent> Object findAndLoadPlugin(T event);
	
	<T extends IIrcEvent,K> K findAndLoadPlugin(T event, Class<K> c);
	
	public Object findPluginForCommand(String cmd);
	
	public <K> K findPluginForCommand(String cmd, Class<K> iface);
	
	void reloadPlugins();
	
	Object loadPluginBean(String beanId);
	
	Object loadPlugin(String className) throws Exception;

	public ICommandsMap getCommandsMap();
}
